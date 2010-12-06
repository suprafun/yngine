package sk.yin.yngine.main;

import com.sun.opengl.util.texture.Texture;
import java.net.URL;
import sk.yin.yngine.scene.util.SphereModelFactory;
import sk.yin.yngine.math.Model;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import sk.yin.yngine.math.Point3f;
import sk.yin.yngine.resources.ResourceGetter;
import sk.yin.yngine.particlesystem.ParticleUnit;
import sk.yin.yngine.particlesystem.SimpleConfig;
import sk.yin.yngine.particlesystem.SimpleFactory;
import sk.yin.yngine.render.shaders.ShaderFactory;
import sk.yin.yngine.render.shaders.ShaderProgram;
import sk.yin.yngine.scene.util.TextureLoader;
import sk.yin.yngine.scene.ParticleUnitSceneNode;
import sk.yin.yngine.scene.SceneCamera;
import sk.yin.yngine.scene.SceneGraph;
import sk.yin.yngine.scene.SceneObject;
import sk.yin.yngine.util.Log;

/**
 * Event loop hooks. Based on Brian Paul's and others code.
 */
public class GLRenderer implements GLEventListener {
    private static final int MODEL_NUM = 2;
    Model s[] = new Model[MODEL_NUM];
    SceneObject so[] = new SceneObject[MODEL_NUM];
    float r;
    long t0 = 0, frames = 0;
    private SceneGraph scene;
    private static final int glFace = GL.GL_FRONT_AND_BACK;
    private int steps;
    ShaderProgram shader;
    private static final boolean DISABLE_SHADERS = false;
    private static final float SPHERE_RADIUS = 13.0f;
    private static final float SPHERE_MASS = 10.0f;

    private enum MaterialDef {
        Copper(0.3f, 0.7f, 0.6f, 6.0f, 1.8f, 228, 123, 87),
        Rubber(0.3f, 0.7f, 0.0f, 0.0f, 1.00f, 3, 139, 251),
        Brass(0.3f, 0.7f, 0.7f, 8.00f, 2.0f, 228, 187, 34),
        Glass(0.3f, 0.7f, 0.7f, 32.00f, 1.0f, 199, 227, 208),
        Plastic(0.3f, 0.9f, 0.9f, 32.0f, 1.0f, 0, 19, 252),
        Pearl(1.5f, -0.5f, 2.0f, 99.0f, 1.0f, 255, 138, 138),
        Full(0.4f, 0.7f, 0.7f, 16.0f, 1.0f, 255, 255, 255),
        Half(0.2f, 0.4f, 0.6f, 32.0f, 1.0f, 255, 255, 255);
        public final float ambient[], diffuse[], specular[], shininess, briliance, c[];

        MaterialDef(float ambient, float diffuse, float specular, float shinines,
                float brilliance, int r, int g, int b) {
            this.ambient = new float[]{ambient, ambient, ambient, 1.0f};
            this.diffuse = new float[]{diffuse, diffuse, diffuse, 1.0f};
            this.specular = new float[]{specular, specular, specular, 1.0f};
            this.shininess = shinines;
            this.briliance = brilliance;
            c = new float[]{(float) r / 255, (float) g / 255, (float) b / 512, 1.0f};
        }

        public void use(GL gl) {
            gl.glMaterialfv(glFace, GL.GL_AMBIENT, ambient, 0);
            gl.glMaterialfv(glFace, GL.GL_DIFFUSE, diffuse, 0);
            gl.glMaterialfv(glFace, GL.GL_SPECULAR, specular, 0);
            gl.glMaterialfv(glFace, GL.GL_SHININESS, new float[]{shininess}, 0);
        }
    }

    public void init(GLAutoDrawable drawable) {
        // Use debug pipeline
        // drawable.setGL(new DebugGL(drawable.getGL()));

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
        gl.glLightModeli(GL.GL_LIGHT_MODEL_COLOR_CONTROL,
                GL.GL_SEPARATE_SPECULAR_COLOR);

        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[]{2.0f, 2.5f, 3.0f, 0.0f}, 0);

        MaterialDef.Full.use(gl);
        gl.glColorMaterial(glFace, GL.GL_DIFFUSE);
        gl.glEnable(gl.GL_COLOR_MATERIAL);

        //
        // Models
        //
        URL url;
        String filenames[] = new String[]{
            //"tex05-c.png",
            "tex05.png",
            //"tex04.2.png",
            //"tex04.png",
            "tex03.png",
            "tex2.png",
            "tex1.png"};
        Texture texture = null,
                t = null;
        url = ResourceGetter.getFirstResourcePresent(filenames);
        if (url != null) {
            texture = TextureLoader.getInstance().load(url);
            texture.setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
            texture.setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        }

        //
        // Shaders
        //
        if(DISABLE_SHADERS)
            ShaderProgram.disableShaders(gl);
        shader = ShaderFactory.getInstance().createShaderProgram(gl);

        // TODO(mgagyi): Implement cube-map loading
        //url = ResourceGetter.getFirstResourcePresent(new String[]{"escher.cubemap.jpg"});
        /*
        try {
            t = CubeMapTextureFactory.getInstance(gl).loadImage(url);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
        
        for (int i = 0; i < MODEL_NUM; i++) {
            SphereModelFactory.BasePolyhedron base =
                    SphereModelFactory.BasePolyhedron.OCTAHEDRON;
            s[i] = SphereModelFactory.getInstance()
                    .createSphere(SPHERE_RADIUS, 5, base);

            // TODO(mgagyi): This should be done by decorators in ModelBuilder
            s[i].setTexture(texture);
            s[i].textureZCorrectCoord(true);

            //* // Change color of models to white.
            float c[] = s[i].colors();
            for (int j = 0; j < c.length; j++) {
                c[j] = 0.5f + c[j] / 2;
            }
            //*/
            if (i == 1) {
                s[i].setShader(shader);
            } else {
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

        int interval = 1000;
        if (t0 % interval > t1 % interval) {
            Log.log("fps: " + 1 / dt + " deltaTime: " + dt);
            if ((++steps % 10) < 5) {
                MaterialDef.Full.use(gl);
            } else {
                MaterialDef.Half.use(gl);
            }

        }

        r += (dt * 5);
        //int i0 = ((int)r / 100) % 10;
        for (int i = 0; i < MODEL_NUM; i++) {
            so[i].setRx(r * 2);
            so[i].setRy(r * 3);
            so[i].setRz(r * 5);
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

    public void destroy(GL gl) {
        if(shader != null)
            shader.destroy(gl);
    }
}
