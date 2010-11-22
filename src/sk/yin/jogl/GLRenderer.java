package sk.yin.jogl;

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
import sk.yin.jogl.scene.ParticleUnitSceneNode;
import sk.yin.jogl.scene.SceneCamera;
import sk.yin.jogl.scene.SceneGraph;
import sk.yin.jogl.scene.SceneObject;

/**
 * GLRenderer.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel) <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */
public class GLRenderer implements GLEventListener {

    private static final int MODEL_NUM = 3;
    Model s[] = new Model[MODEL_NUM];
    SceneObject so[] = new SceneObject[MODEL_NUM];
    float r;
    long t0 = 0, frames = 0;
    private SceneGraph scene;

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
        gl.glEnable(gl.GL_COLOR_MATERIAL);

        //gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE);
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[]{0.2f, 0.2f, 0.2f, 1.0f}, 0);
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, new float[]{0.7f, 0.7f, 0.7f, 1.0f}, 0);
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 0);
        gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, new float[]{35.0f}, 0);

        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, new float[]{0.0f, 0.0f, 0.0f, 1.0f}, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, new float[]{0.5f, 0.5f, 0.5f, 1.0f}, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[]{0.0f, 4.0f, 3.0f, 1.0f}, 0);

        //
        // Models
        //
        for (int i = 0; i < MODEL_NUM; i++) {
            SphereModelFactory.BasePolyhedron base = SphereModelFactory.BasePolyhedron.values()[i];
            s[i] = SphereModelFactory.getInstance().createSphere(13.0f, 5, base);
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

        float x = -15.0f * (MODEL_NUM-1),
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

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
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

        if(t0 == 0) t0 = t1;

        dt = (float)(t1 - t0) / 1000;

        if(frames % 50 == 0)
            System.out.println("fps: " + 1/dt + " deltaTime: " + dt);
        
        GL gl = drawable.getGL();

        r += dt;
        //int i0 = ((int)r / 100) % 10;
        for (int i = 0; i < MODEL_NUM; i++) {
            so[i].setR(r*10);
        }

        // Clear the drawing area
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        scene.frame(gl, dt);
        // Flush all drawing operations to the graphics card
        gl.glFlush();
        t0 = t1;
        frames++;
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
}
