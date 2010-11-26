package sk.yin.jogl;

import com.sun.opengl.util.texture.Texture;
import sk.yin.jogl.resources.SphereModelFactory;
import sk.yin.jogl.data.Model;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import sk.yin.jogl.data.Point3f;
import sk.yin.jogl.render.particlesystem.ParticleUnit;
import sk.yin.jogl.render.particlesystem.SimpleConfig;
import sk.yin.jogl.render.particlesystem.SimpleFactory;
import sk.yin.jogl.resources.TextureLoader;
import sk.yin.jogl.scene.ParticleUnitSceneNode;
import sk.yin.jogl.scene.SceneCamera;
import sk.yin.jogl.scene.SceneGraph;
import sk.yin.jogl.scene.SceneObject;
import sk.yin.jogl.shaders.ShaderFactory;
import sk.yin.jogl.shaders.ShaderProgram;

/**
 * GLRenderer.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel) <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */
public class GLRenderer implements GLEventListener {
    private static final int MODEL_NUM = 2;
    Model s[] = new Model[MODEL_NUM];
    SceneObject so[] = new SceneObject[MODEL_NUM];
    float r;
    long t0 = 0, frames = 0;
    private SceneGraph scene;
    private static final int glFace =
            //*
            GL.GL_FRONT /*/
            GL.GL_FRONT_AND_BACK
            //*/
            ;
    private int steps;

    private enum MaterialDef {
        Copper(0.3f, 0.7f, 0.6f, 6.0f, 1.8f, 228, 123, 87),
        Rubber(0.3f, 0.7f, 0.0f, 0.0f, 1.00f, 3, 139, 251),
        Brass(0.3f, 0.7f, 0.7f, 8.00f, 2.0f, 228, 187, 34),
        Glass(0.3f, 0.7f, 0.7f, 32.00f, 1.0f, 199, 227, 208),
        Plastic(0.3f, 0.9f, 0.9f, 32.0f, 1.0f, 0, 19, 252),
        Pearl(1.5f, -0.5f, 2.0f, 99.0f, 1.0f, 255, 138, 138),
        Full(0.2f, 0.7f, 0.7f, 32.0f, 1.0f, 255, 255, 255),
        Half(0.1f, 0.4f, 0.4f, 16.0f, 1.0f, 255, 255, 255);

        public final float a[], d[], s[], sh, br, c[];

        MaterialDef(float ambient, float diffuse, float specular, float shinines,
                float brilliance, int r, int g, int b) {
            a = new float[]{ambient, ambient, ambient, 1.0f};
            d = new float[]{diffuse, diffuse, diffuse, 1.0f};
            s = new float[]{specular, specular, specular, 1.0f};
            sh = shinines;
            br = brilliance;
            c = new float[]{(float) r, (float) g, (float) b, 1.0f};
        }

        public void use(GL gl) {
            gl.glMaterialfv(glFace, GL.GL_AMBIENT, a, 0);
            gl.glMaterialfv(glFace, GL.GL_DIFFUSE, d, 0);
            gl.glMaterialfv(glFace, GL.GL_SPECULAR, s, 0);
            gl.glMaterialfv(glFace, GL.GL_SHININESS, new float[] { sh }, 0);
        }
    }

    public void init(GLAutoDrawable drawable) {
        // Use debug pipeline
        //drawable.setGL(new DebugGL(drawable.getGL()));

        GL gl = drawable.getGL();
        System.err.println("INIT GL IS: " + gl.getClass().getName());

        // Enable VSync
        gl.setSwapInterval(1);

        // Setup the drawing area and shading mode
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.

        gl.glEnable(gl.GL_DEPTH_TEST);
        gl.glDepthFunc(gl.GL_LESS);

        gl.glEnable(gl.GL_LIGHTING);
        gl.glEnable(gl.GL_LIGHT0);

        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[]{-0.5f, 4.0f, 3.0f, 1.0f}, 0);

        MaterialDef.Full.use(gl);
        gl.glColorMaterial(glFace, GL.GL_DIFFUSE);
        gl.glEnable(gl.GL_COLOR_MATERIAL);

        //ShaderProgram.disableShaders(gl);
        //
        // Models
        //
        for (int i = 0; i < MODEL_NUM; i++) {
            SphereModelFactory.BasePolyhedron base =
                    /*
                    SphereModelFactory.BasePolyhedron.values()[i];
                    /*/
                    SphereModelFactory.BasePolyhedron.OCTAHEDRON;
            //*/
            s[i] = SphereModelFactory.getInstance().createSphere(13.0f, 5, base);
            if (i == 1) {
                ShaderProgram shader =
                        ShaderFactory.getInstance().createShaderProgram(gl);
                s[i].setShader(shader);
            } else {
                Texture texture = TextureLoader.getInstance().load("C:\\Users\\yin\\tex2.png");
                texture.setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
                texture.setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
                s[i].setTexture(texture);
            }
        }
        r = 0;

        //
        // Particle effects
        //
        SimpleConfig e1c = new SimpleConfig();
        e1c.count = 100;
        e1c.gravity = new Point3f(0, -0.10f, 0);
        e1c.invResistance = 0.999999f;
        e1c.boundingBox = 21.0f;
        e1c.boundingBoxBounce = 0.99f;
        ParticleUnit e1 = new ParticleUnit(new SimpleFactory(), e1c);

        //
        // Scene graph
        //
        scene = new SceneGraph();
        SceneCamera camera = new SceneCamera();
        scene.setCamera(camera);
        camera.setPz(20.0f * (MODEL_NUM + 1));
        scene.addChild(new ParticleUnitSceneNode(e1));

        float x = -15.0f * (MODEL_NUM - 1),
                xi = 30.0f;
        for (int i = 0; i < MODEL_NUM; i++, x += xi) {
            SceneObject obj = new SceneObject(s[i]);
            obj.setPx(x);
            obj.setRx(150.0f);
            obj.setRy(120.0f);
            obj.setRz(220.0f);
            scene.addChild(obj);
            so[i] = obj;
        }
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();

        if (height <= 0) {
            // avoid a divide by zero error!
            height = 1;
        }
        final float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 1.0, 2000.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void display(GLAutoDrawable drawable) {
        long t1 = System.currentTimeMillis();
        float dt;

        if (t0 == 0) {
            t0 = t1;
        }

        dt = (float) (t1 - t0) / 1000;

        GL gl = drawable.getGL();

        if (frames % 50 == 0) {
            System.out.println("fps: " + 1 / dt + " deltaTime: " + dt);
            if ((++steps % 10) < 5)
                MaterialDef.Full.use(gl);
            else
                MaterialDef.Half.use(gl);

        }

        r += dt;
        //int i0 = ((int)r / 100) % 10;
        for (int i = 0; i < MODEL_NUM; i++) {
            so[i].setR(r * 10);
        }

        // Clear the drawing area
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        scene.frame(gl, dt);
        // Flush all drawing operations to the graphics card
        gl.glFlush();
        t0 = t1;
        frames++;
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }
}
