package sk.yin.yngine.main;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import com.sun.opengl.util.texture.Texture;
import java.net.URL;
import sk.yin.yngine.scene.generators.SphereModelGenerator;
import sk.yin.yngine.geometry.Model;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;
import sk.yin.yngine.geometry.Point3f;
import sk.yin.yngine.resources.ResourceGetter;
import sk.yin.yngine.particlesystem.ParticleUnit;
import sk.yin.yngine.particlesystem.SimpleConfig;
import sk.yin.yngine.particlesystem.SimpleFactory;
import sk.yin.yngine.render.lights.MaterialDef;
import sk.yin.yngine.render.shaders.ShaderFactory;
import sk.yin.yngine.render.shaders.ShaderProgram;
import sk.yin.yngine.scene.GenericLightNode;
import sk.yin.yngine.scene.GenericLightNode.LightType;
import sk.yin.yngine.scene.io.TextureLoader;
import sk.yin.yngine.scene.camera.LookAtCamera;
import sk.yin.yngine.scene.SceneGraph;
import sk.yin.yngine.scene.GenericSceneNode;
import sk.yin.yngine.scene.attributes.GeometryAttribute;
import sk.yin.yngine.scene.attributes.PhysicsAttribute;
import sk.yin.yngine.scene.camera.SmoothingCameraProxy;
import sk.yin.yngine.scene.attributes.TransformAttribute;
import sk.yin.yngine.scene.decorators.NormalBasedColorDecorator;
import sk.yin.yngine.scene.generators.BoxModelGenerator;
import sk.yin.yngine.scene.generators.ModelBuilder;
import sk.yin.yngine.scene.decorators.NormalBasedTextureDecorator;
import sk.yin.yngine.scene.decorators.StaticColorDecorator;
import sk.yin.yngine.scene.decorators.VertexBasedColorDecorator;
import sk.yin.yngine.util.Log;

/**
 * Event loop hooks. Based on Brian Paul's and others code.
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine.src@gmail.com)
 */
public class GLRenderer implements GLEventListener {

    private static final int MODEL_NUM = 2;
    Model s[] = new Model[MODEL_NUM];
    GenericSceneNode sceneObjectNode[] = new GenericSceneNode[MODEL_NUM];
    float r;
    long t0 = 0, frames = 0;
    private SceneGraph scene;
    private LookAtCamera camera;
    private static final int glFace = GL.GL_FRONT;
    private int steps;
    ShaderProgram shader;
    DynamicsWorld bulletWorld;
    RigidBody groundBody;
    MotionState motionState[] = new MotionState[MODEL_NUM];
    private RigidBody rigidBodies[] = new RigidBody[MODEL_NUM];
    private static final boolean DISABLE_SHADERS = false;
    private static final boolean DISABLE_LIGHTING = false;
    private static final float DEFUALT_OBJECT_RADIUS = 13.0f;
    private static final float SPHERE_MASS = 10.0f;
    private static final float BOX_MASS = 1.0f;
    private static final float WORLD_RADIUS = 250.0f,
            WORLD_RADIUS_SQUARED = WORLD_RADIUS * WORLD_RADIUS;
    private GenericLightNode light0, light1, light2 = null;
    // Animation
    private ResetBodyStrategy resetBodyStrategy;
    private TorqueImpulseStrategy torqueImpulseStrategy;
    private JumpStrategy jumpStrategy;

    public void init(GLAutoDrawable drawable) {
        // Use debug pipeline
        //drawable.setGL(new DebugGL(drawable.getGL()));

        CollisionConfiguration config = new DefaultCollisionConfiguration();
        SequentialImpulseConstraintSolver solver =
                new SequentialImpulseConstraintSolver();
        CollisionDispatcher dispatcher = new CollisionDispatcher(config);
        BroadphaseInterface broadphase = new DbvtBroadphase();
        bulletWorld =
                new DiscreteDynamicsWorld(dispatcher, broadphase, solver, config);

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
        gl.glLightModeli(GL.GL_LIGHT_MODEL_COLOR_CONTROL,
                GL.GL_SEPARATE_SPECULAR_COLOR);

        MaterialDef.Full.use(gl);
        gl.glColorMaterial(glFace, GL.GL_AMBIENT_AND_DIFFUSE);
        gl.glEnable(gl.GL_COLOR_MATERIAL);

        if (DISABLE_LIGHTING) {
            gl.glDisable(GL.GL_LIGHTING);
        }

        //
        // Textures
        //
        URL url;
        String filenames[] = new String[]{
            //"tex07.png",
            //"tex06.2.png",
            //"tex05-c.png",
            //"tex05.png",
            //"tex04.2.png",
            //"tex04.png",
            //"tex03.png",
            //"tex2.png",
            "tex1.png"};
        Texture texture = null,
                t = null;
        url = ResourceGetter.getFirstResourcePresent(filenames);
        if (url != null) {
            texture = TextureLoader.getInstance().load(url);
            texture.setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
            texture.setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        }
        if (gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic")) {
            float max[] = new float[1];
            gl.glGetFloatv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, max, 0);

            gl.glTexParameterf(GL.GL_TEXTURE_2D,
                    GL.GL_TEXTURE_MAX_ANISOTROPY_EXT,
                    max[0]);
        }

        //
        // Shaders
        //
        if (DISABLE_SHADERS) {
            ShaderProgram.disableShaders(gl);
        }
        shader = ShaderFactory.getInstance().loadShader(gl, "ffpe");
        Log.log("Shader to use in scene: " + shader.toString());
        ShaderProgram.ShaderProgramInterface iface = shader.use(gl);
        iface.setUniform(gl, "spotFadeOff", 1);
        //
        // Ground
        //
        {
            CollisionShape shape =
                    new BoxShape(new Vector3f(100.0f - 0.04f, 1.0f - 0.04f, 100.0f - 0.04f));

            Transform transform = new Transform();
            transform.origin.set(new Vector3f(0.0f, -10.0f, 0.0f));
            transform.basis.setIdentity();
            DefaultMotionState motion = new DefaultMotionState(transform);
            RigidBodyConstructionInfo rbInfo =
                    new RigidBodyConstructionInfo(0, motion, shape);
            rbInfo.restitution = 0.6f;

            groundBody = new RigidBody(rbInfo);
            bulletWorld.addRigidBody(groundBody);
        }

        // Spheres
        for (int i = 0; i < MODEL_NUM; i++) {
            SphereModelGenerator.BasePolyhedron base =
                    SphereModelGenerator.BasePolyhedron.OCTAHEDRON;
            ModelBuilder builder = new ModelBuilder();
            if (i % 2 == 0) {
                builder.addDecorators(
                        new VertexBasedColorDecorator(),
                        new NormalBasedTextureDecorator());
                s[i] =
                        SphereModelGenerator.instance().createSphere(DEFUALT_OBJECT_RADIUS, 5, builder, base);
                // TODO(mgagyi): This should be done by decorators in ModelBuilder
                s[i].setTexture(texture);
            } else {
                builder.addDecorators(
                        new NormalBasedColorDecorator());
                s[i] = BoxModelGenerator.instance().createBox(builder, DEFUALT_OBJECT_RADIUS, DEFUALT_OBJECT_RADIUS,
                        DEFUALT_OBJECT_RADIUS);
                //s[i].setTexture(texture);
            }
            s[i].setShader(shader);
            Log.log("SceneModel #" + i + ": " + s[i].toString());
        }
        r = 0;

        //
        // Particle effects
        //
        SimpleConfig e1c = new SimpleConfig();
        e1c.count = 100;
        e1c.gravity = new Point3f(0, -1.00f, 0);
        e1c.invResistance = 0.999999f;
        e1c.boundingBox = 21.0f;
        e1c.boundingBoxBounce = 0.99f;
        ParticleUnit e1 = new ParticleUnit(new SimpleFactory(), e1c);

        //
        // Scene graph
        //
        scene = new SceneGraph();
        camera = new LookAtCamera();
        camera.setPosition(new Vector3f(333f, 250f, 1000f));
        camera = new SmoothingCameraProxy(camera);
        camera.setPosition(new Vector3f(0, 50f, 150f));
        scene.addChild(new GenericSceneNode(camera));
        scene.setCamera(camera);
        //scene.addChild(new GenericSceneNode(new ParticleUnitAttribute(e1)));

        // Lights
        light0 = new GenericLightNode(LightType.Spot);
        light0.ambient(new float[]{0.1f, 0.1f, 0.1f, 1.0f});
        light0.diffuse(new float[]{0.8f, 0.8f, 0.8f, 1.0f});
        light0.specular(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        light0.cutoff(60.0f);
        light0.stopExp(0.75f);
        scene.addChild(light0);

        light1 = new GenericLightNode(LightType.Spot);
        light1.ambient(new float[]{0.1f, 0.1f, 0.1f, 1.0f});
        light1.diffuse(new float[]{0.7f, 0.7f, 0.7f, 1.0f});
        light1.specular(new float[]{0.3f, 0.3f, 0.3f, 1.0f});
        light1.cutoff(50.0f);
        light1.stopExp(0.33f);
        light1.position(new Vector3f(0, 100.0f, 0));
        light1.direction(new Vector3f(0, -1.0f, 0));
        scene.addChild(light1);

        // Ground
        Model box = BoxModelGenerator.instance().createBox(
                new ModelBuilder().addDecorators(
                new StaticColorDecorator(.7f, .7f, .7f)),
                100f, 1f, 100f);
        box.setTexture(texture);
        box.setShader(shader);
        GenericSceneNode node = new GenericSceneNode(
                new GeometryAttribute(box, MaterialDef.Floor),
                new TransformAttribute(new Vector3f(0f, -10f, 0f)));
        scene.addChild(node);

        // Balls

        float x = -20.0f * (MODEL_NUM - 1),
                xi = 40.0f;
        for (int i = 0; i < MODEL_NUM; i++, x += xi) {
            GeometryAttribute geometry = new GeometryAttribute(s[i], MaterialDef.Full);
            PhysicsAttribute physics =
                    new PhysicsAttribute(new Vector3f(x, 40f, 0));
            GenericSceneNode obj = new GenericSceneNode(geometry, physics);
            scene.addChild(obj);
            sceneObjectNode[i] = obj;
            motionState[i] = physics;

            {
                CollisionShape shape;
                float mass;
                if (i % 2 == 0) {
                    shape = new SphereShape(DEFUALT_OBJECT_RADIUS);
                    mass = SPHERE_MASS;
                } else {
                    shape = new BoxShape(new Vector3f(
                            DEFUALT_OBJECT_RADIUS - 0.04f,
                            DEFUALT_OBJECT_RADIUS - 0.04f,
                            DEFUALT_OBJECT_RADIUS - 0.04f));
                    mass = BOX_MASS;
                }
                Vector3f localInertia = new Vector3f(0.0f, 0.0f, 0.0f);
                shape.calculateLocalInertia(SPHERE_MASS, localInertia);
                RigidBodyConstructionInfo rbInfo =
                        new RigidBodyConstructionInfo(SPHERE_MASS, motionState[i], shape, localInertia);
                rbInfo.restitution = 0.1f;

                RigidBody body = new RigidBody(rbInfo);

                bulletWorld.addRigidBody(body);
                rigidBodies[i] = body;

                if (i == 0) {
                    //body.applyCentralImpulse(new Vector3f(300.0f, -150.0f, 10.0f));
                } else {
                    //body.applyCentralImpulse(new Vector3f(0.0f, 10.0f, 0.0f));
                    //
                }
            }
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
        // Limit the framerate to minimum of 5 fps.
        if (dt > .2) {
            dt = 0.2f;
        }

        if (t0 % 800 > t1 % 800) {
            if (resetBodyStrategy == null) {
                resetBodyStrategy = new ResetBodyStrategy();
            }
            resetBodyStrategy.applyStrategy();

            if (torqueImpulseStrategy == null) {
                torqueImpulseStrategy = new TorqueImpulseStrategy(0, 1);
            }
            torqueImpulseStrategy.applyStrategy();

        }

        if (t0 % 2250 > t1 % 2250) {
            if (jumpStrategy == null) {
                jumpStrategy = new JumpStrategy(1);
            }
            jumpStrategy.applyStrategy();
        }

        bulletWorld.stepSimulation(dt * 2.0f, (int) (60 / 5 * 2.0f));

        GenericSceneNode target = sceneObjectNode[(int) (t1 / 8000 % 2)];
        camera.setTarget(target.attribute(PhysicsAttribute.class).origin());

        GL gl = drawable.getGL();

        int interval = 1000;
        if (t0 % interval > t1 % interval) {
            Log.log("fps: " + 1 / dt + " deltaTime: " + dt);
        }

        r += dt;

        double dr = Math.sin(r * 1.9),
                rotSpeed = 0.5,
                lDist = (Math.cos(r / 2.3) + 1) / 2;
        lDist = 100 - lDist * lDist * 40;
        light0.position(new Vector3f(
                (float) (Math.sin(r * rotSpeed) * lDist),
                30.0f,
                (float) (Math.cos(r * rotSpeed) * lDist)));
        light0.direction(new Vector3f((float) -Math.sin(r * rotSpeed + dr), -1.55f, (float) -Math.cos(r * rotSpeed + dr)));

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
        if (shader != null) {
            shader.destroy(gl);
        }
    }

    public interface IGLRendererStrategy {

        public void applyStrategy();
    }

    private class ResetBodyStrategy implements IGLRendererStrategy {

        public void applyStrategy() {
            Transform t = new Transform();
            for (int i = 0; i < MODEL_NUM; i++) {
                MotionState ms = motionState[i];
                ms.getWorldTransform(t);

                if (t.origin.lengthSquared() > WORLD_RADIUS_SQUARED) {
                    if (ms instanceof PhysicsAttribute) {
                        Log.log(this.getClass().getName() + " => body resetOrigin");
                        ((PhysicsAttribute) ms).resetToStartOrigin(rigidBodies[i]);
                        rigidBodies[i].setAngularVelocity(new Vector3f(0.0f, 0.0f, 0.0f));
                        rigidBodies[i].setLinearVelocity(new Vector3f(0.0f, 0.0f, 0.0f));
                    }
                }
            }
        }
    }

    private class TorqueImpulseStrategy implements IGLRendererStrategy {

        public static final double RANDOM_SIZE = 2.0;
        public static final double RANDOM_BASE = -RANDOM_SIZE / 2;
        public static final float IMPULSE_SIZE = 660;
        int forcedIdx, targetIdx;

        public TorqueImpulseStrategy(int forcedIdx, int targetIdx) {
            this.forcedIdx = forcedIdx;
            this.targetIdx = targetIdx;
        }

        public void applyStrategy() {
            Transform t = new Transform();
            Vector3f forcedOrigin, targetOrigin, // inputs
                    forcedTargetVector, rightVector, randomness, // middle steps
                    applyTorque;                                  // results

            rigidBodies[forcedIdx].getWorldTransform(t);
            forcedOrigin = new Vector3f(t.origin);

            rigidBodies[targetIdx].getWorldTransform(t);
            targetOrigin = new Vector3f(t.origin);

            forcedTargetVector = new Vector3f(forcedOrigin);
            forcedTargetVector.negate();
            forcedTargetVector.add(targetOrigin);

            forcedTargetVector.normalize();

            rightVector = new Vector3f(0.0f, 1.0f, 0.0f);
            rightVector.cross(rightVector, forcedTargetVector);

            randomness = new Vector3f(random(), random(), random());

            //Log.log(this.getClass().getName() + "(" + forcedIdx + ", " + targetIdx + ")");
            //Log.log(this.getClass().getName() + ".Forced    Origin = " + forcedOrigin.toString());
            //Log.log(this.getClass().getName() + ".Target    Origin = " + targetOrigin.toString());
            //Log.log(this.getClass().getName() + ".Random Direction = " + randomness.toString());
            //Log.log(this.getClass().getName() + ".T-F    Direction = " + forcedTargetVector.toString());
            //Log.log(this.getClass().getName() + ".r(T-F) Direction = " + rightVector.toString());

            applyTorque = new Vector3f(rightVector);

            applyTorque.add(randomness);
            applyTorque.normalize();
            applyTorque.scale(IMPULSE_SIZE);

            Log.log(this.getClass().getName() + " => forced applyImpulse: Torque(" + applyTorque.toString() + ")");

            rigidBodies[forcedIdx].applyTorqueImpulse(applyTorque);
        }

        protected float random() {
            return (float) (Math.random() * RANDOM_SIZE + RANDOM_BASE);
        }
    }

    private class JumpStrategy implements IGLRendererStrategy {

        public final Vector3f JUMP_VECTOR = new Vector3f(0.0f, 125.0f, 0.0f);
        int jumpIdx;

        public JumpStrategy(int jumpIdx) {
            this.jumpIdx = jumpIdx;
        }

        public void applyStrategy() {
            Log.log(this.getClass().getName() + " => jump applyImpulse Central(" + JUMP_VECTOR.toString() + ")");
            rigidBodies[jumpIdx].applyCentralImpulse(JUMP_VECTOR);
        }
    }
}
