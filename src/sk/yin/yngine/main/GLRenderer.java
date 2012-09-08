package sk.yin.yngine.main;

import java.util.logging.Logger;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;

import org.python.core.PyArray;

import sk.yin.yngine.geometry.Model;
import sk.yin.yngine.geometry.Point3f;
import sk.yin.yngine.particlesystem.ParticleUnit;
import sk.yin.yngine.particlesystem.SimpleConfig;
import sk.yin.yngine.particlesystem.SimpleFactory;
import sk.yin.yngine.render.config.AnisotropicConfig;
import sk.yin.yngine.render.lights.MaterialDef;
import sk.yin.yngine.render.shaders.ShaderFactory;
import sk.yin.yngine.render.shaders.ShaderProgram;
import sk.yin.yngine.scene.GenericLightNode;
import sk.yin.yngine.scene.GenericLightNode.LightType;
import sk.yin.yngine.scene.GenericSceneNode;
import sk.yin.yngine.scene.SceneGraph;
import sk.yin.yngine.scene.attributes.GeometryAttribute;
import sk.yin.yngine.scene.attributes.ParticleUnitAttribute;
import sk.yin.yngine.scene.attributes.PhysicsAttribute;
import sk.yin.yngine.scene.attributes.TransformAttribute;
import sk.yin.yngine.scene.camera.LookAtCamera;
import sk.yin.yngine.scene.camera.SmoothingCameraProxy;
import sk.yin.yngine.scene.decorators.NormalBasedColorDecorator;
import sk.yin.yngine.scene.decorators.NormalBasedTextureDecorator;
import sk.yin.yngine.scene.decorators.StaticColorDecorator;
import sk.yin.yngine.scene.decorators.VertexBasedColorDecorator;
import sk.yin.yngine.scene.generators.BoxModelGenerator;
import sk.yin.yngine.scene.generators.ModelBuilder;
import sk.yin.yngine.scene.generators.SphereModelGenerator;
import sk.yin.yngine.scene.io.TextureLoader;
import sk.yin.yngine.scripts.jython.JythonConnector;
import sk.yin.yngine.util.Log;

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
import com.jogamp.opengl.util.texture.Texture;

/**
 * Event loop hooks. Based on Brian Paul'models and others code.
 * 
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine.src@gmail.com)
 */
public class GLRenderer implements GLEventListener {

	public static final boolean DISABLE_SHADERS = false;
	public static final boolean DISABLE_LIGHTING = false;
	public static final int MODEL_NUM = 2;
	public static final float DEFUALT_OBJECT_RADIUS = 13.0f;
	public static final float SPHERE_MASS = 5.0f;
	public static final float BOX_MASS = 2.0f;
	public static final float WORLD_RADIUS = 250.0f,
			WORLD_RADIUS_SQUARED = WORLD_RADIUS * WORLD_RADIUS;
	Model models[] = new Model[MODEL_NUM];
	GenericSceneNode sceneObjectNode[] = new GenericSceneNode[MODEL_NUM];
	float r;
	long t0 = 0, frames = 0;
	private SceneGraph scene;
	private LookAtCamera camera;
	private static final int glFace = GL2.GL_FRONT;
	private int steps;
	Texture texture;
	ShaderProgram shader;
	DynamicsWorld bulletWorld;
	RigidBody groundBody;
	MotionState motionState[] = new MotionState[MODEL_NUM];
	RigidBody rigidBodies[] = new RigidBody[MODEL_NUM];
	private GenericLightNode light0, light1;
	// Animation
	private ResetBodyStrategy resetBodyStrategy;
	private TorqueImpulseStrategy torqueImpulseStrategy;
	private JumpStrategy jumpStrategy;
	private DynamicSpotLightStrategy spotLightStrategy;
	private CameraPositionChangeStrategy cameraPositionChangeStrategy;
	private Texture[] textures;
	private CycleTexturesStrategy cycleTexturesStrategy;
	// Development
	private static final Logger log = Logger.getLogger(GLRenderer.class
			.getName());
	private JythonConnector demoMainScriptPy;

	@Override
	public void init(GLAutoDrawable drawable) {
		// Use debug pipeline
		// drawable.setGL(new DebugGL(drawable.getGL()));

		GL2 gl = (GL2) drawable.getGL();
		System.err.println("INIT GL2 IS: " + gl.getClass().getName());

		// OpenGL2 config
		setupGL(gl);

		demoMainScriptPy = new JythonConnector("DemoMainScript.py");
		demoMainScriptPy.set("gl", gl);
		demoMainScriptPy.set("DISABLE_SHADERS", DISABLE_SHADERS);
		demoMainScriptPy.run();
		PyArray ary = (PyArray) demoMainScriptPy.get("textures");
		textures = (Texture[]) ary.getArray();

		shader = (ShaderProgram) demoMainScriptPy.get("shader").__tojava__(
				ShaderProgram.class);
		/*
		 * try {
		 * 
		 * GroovyScriptEngine gse = new GroovyScriptEngine("C:"); Binding
		 * binding = new Binding(); binding.setVariable("gl", gl);
		 * binding.setVariable("DISABLE_SHADERS", false); Object obj =
		 * gse.run("../scripts/DemoMainScript.groovy", binding); texture =
		 * (Texture) binding.getVariable("texture"); textures = (Texture[])
		 * binding.getVariable("textures"); shader = (ShaderProgram)
		 * binding.getVariable("shader");
		 * Log.log("Loaded textures from groovy: " + textures.length); } catch
		 * (IOException ex) { log.log(Level.SEVERE, null, ex); } catch
		 * (ResourceException ex) { log.log(Level.SEVERE, null, ex); } catch
		 * (ScriptException ex) { log.log(Level.SEVERE, null, ex); } catch
		 * (MissingPropertyException ex) { log.log(Level.WARNING, null, ex); }
		 * //
		 */

		// Textures
		if (texture == null) {
			if (textures == null || textures.length == 0) {
				texture = setupTexture(gl);
			} else {
				texture = textures[0];
			}
		} else {
			log.info("Textures already loaded!");
		}

		// Shaders
		if (shader == null) {
			shader = setupShaders(gl);
		} else {
			log.info("Shaders already loaded!");
		}

		// Scene graph
		scene = new SceneGraph();

		// Camera
		setupCamera();

		// Lights
		setupLights(gl);

		// JBullet physics engine
		setupPhysics();

		// Ground
		setupGround(gl);

		// Objects/Models
		setupObjects(gl);
		r = 0;

		// Particle effects
		// ParticleUnit particleUnit = setupParticles(gl);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL2 gl = (GL2) drawable.getGL();
		GLU glu = new GLU();

		if (height <= 0) {
			// avoid a divide by zero error!
			height = 1;
		}
		final float h = (float) width / (float) height;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, h, 1.0, 2000.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
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

		if (t0 % 750 > t1 % 750) {
			if (resetBodyStrategy == null) {
				resetBodyStrategy = new ResetBodyStrategy(this);
			}
			resetBodyStrategy.applyStrategy();

			if (torqueImpulseStrategy == null) {
				torqueImpulseStrategy = new TorqueImpulseStrategy(0, 1, this);
			}
			torqueImpulseStrategy.applyStrategy();

		}

		if (t0 % 4000 > t1 % 4000) {
			if (jumpStrategy == null) {
				jumpStrategy = new JumpStrategy(1, this);
			}
			jumpStrategy.applyStrategy();

			if (cycleTexturesStrategy == null) {
				cycleTexturesStrategy = new CycleTexturesStrategy(models[0],
						textures);
			}
			cycleTexturesStrategy.applyStrategy();
		}

		bulletWorld.stepSimulation(dt * 2.0f, 24);

		GL2 gl = (GL2) drawable.getGL();

		int interval = 1000;
		if (t0 % interval > t1 % interval) {
			Log.log("fps: " + 1 / dt + " deltaTime: " + dt);
		}

		r += dt;

		if (spotLightStrategy == null) {
			spotLightStrategy = new DynamicSpotLightStrategy(light0, this);
		}
		spotLightStrategy.applyStrategy();

		if (t0 / 1000 % 2 > t1 / 1000 % 2 || frames == 0) {
			if (cameraPositionChangeStrategy == null) {
				cameraPositionChangeStrategy = new CameraPositionChangeStrategy(
						camera, this);
			}
			cameraPositionChangeStrategy.applyStrategy();
		}

		// Clear the drawing area
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		scene.frame(gl, dt);
		// Flush all drawing operations to the graphics card
		gl.glFlush();
		t0 = t1;
		frames++;
	}

	public void dispose(GLAutoDrawable canvas) {
		GL2 gl = (GL2) canvas.getGL();
		if (shader != null) {
			shader.destroy(gl);
		}
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}

	/**
	 * Sets up OpenGL2.
	 * 
	 * @param gl
	 */
	private void setupGL(GL2 gl) {
		// Enable VSync
		gl.setSwapInterval(1);

		// Setup the drawing area and shading mode
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glShadeModel(GL2.GL_SMOOTH);

		gl.glEnable(gl.GL_DEPTH_TEST);
		gl.glDepthFunc(gl.GL_LESS);
	}

	/**
	 * Loads a texture and sets up texturing.
	 * 
	 * @param gl
	 * @return
	 */
	private Texture setupTexture(GL2 gl) {

		AnisotropicConfig.instance().setMaxAnisotropy(gl);

		textures = TextureLoader.getInstance().loadResource(gl, 
				new String[] { "tex07.png", "tex06.2.png", "tex05-c.png",
						"tex05.png", "tex04.2.png", "tex04.png", "tex03.png",
						"tex2.png", "tex1.png" });

		return textures[0];
	}

	/**
	 * Loads a shader and configures it. It gets added to scene.
	 * 
	 * @param gl
	 * @return
	 */
	private ShaderProgram setupShaders(GL2 gl) {
		if (DISABLE_SHADERS) {
			ShaderProgram.disableShaders(gl);
		}
		shader = ShaderFactory.getInstance().loadShader(gl, "ffpe");
		Log.log("Shader to use in scene: " + shader.toString());
		ShaderProgram.ShaderProgramInterface iface = shader.use(gl);
		iface.uniform(gl, "spotFadeOff", 1);
		return shader;
	}

	/**
	 * Sets up scene camera, which can follow an object with smooth movements.
	 * It gets added to scene.
	 */
	private void setupCamera() {
		camera = new LookAtCamera();
		camera.setPosition(new Vector3f(333f, 250f, 1000f));
		camera = new SmoothingCameraProxy(camera);
		scene.addChild(new GenericSceneNode(camera));
		scene.setCamera(camera);
	}

	/**
	 * Sets up lighting. It gets added to scene.
	 * 
	 * @param gl
	 */
	private void setupLights(GL2 gl) {
		gl.glEnable(gl.GL_LIGHTING);
		gl.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL,
				GL2.GL_SEPARATE_SPECULAR_COLOR);

		MaterialDef.Full.use(gl);
		gl.glColorMaterial(glFace, GL2.GL_AMBIENT_AND_DIFFUSE);
		gl.glEnable(gl.GL_COLOR_MATERIAL);

		if (DISABLE_LIGHTING) {
			gl.glDisable(GL2.GL_LIGHTING);
		}

		light0 = new GenericLightNode(LightType.Spot);
		light0.ambient(new float[] { 0.02f, 0.02f, 0.02f, 1.0f });
		light0.diffuse(new float[] { 0.8f, 0.8f, 0.8f, 1.0f });
		light0.specular(new float[] { 1.0f, 1.0f, 1.0f, 1.0f });
		light0.cutoff(60.0f);
		light0.stopExp(0.75f);
		light0.attenuationLinear(0.01f);
		scene.addChild(light0);

		light1 = new GenericLightNode(LightType.Spot);
		light1.ambient(new float[] { 0.02f, 0.02f, 0.02f, 1.0f });
		light1.diffuse(new float[] { 0.8f, 0.8f, 0.8f, 1.0f });
		light1.specular(new float[] { 0.3f, 0.3f, 0.3f, 1.0f });
		light1.cutoff(50.0f);
		light1.stopExp(0.33f);
		light1.position(new Vector3f(0, 100.0f, 0));
		light1.direction(new Vector3f(0, -1.0f, 0));
		scene.addChild(light1);
	}

	/**
	 * Sets up JBullet simulation world.
	 */
	private void setupPhysics() {
		CollisionConfiguration config = new DefaultCollisionConfiguration();
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		CollisionDispatcher dispatcher = new CollisionDispatcher(config);
		BroadphaseInterface broadphase = new DbvtBroadphase();
		bulletWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver,
				config);
	}

	/**
	 * Creates scene ground. It gets added to scene.
	 * 
	 * @param gl
	 */
	private void setupGround(GL2 gl) {
		Model box = BoxModelGenerator.instance().createBox(
				new ModelBuilder().addDecorators(new StaticColorDecorator(.7f,
						.7f, .7f)), 100f, 1f, 100f);
		box.setTexture(texture);
		box.setShader(shader);
		GenericSceneNode node = new GenericSceneNode(new GeometryAttribute(box,
				MaterialDef.Floor), new TransformAttribute(new Vector3f(0f,
				-10f, 0f)));
		scene.addChild(node);

		{
			CollisionShape shape = new BoxShape(new Vector3f(100.0f - 0.04f,
					1.0f - 0.04f, 100.0f - 0.04f));

			Transform transform = new Transform();
			transform.origin.set(new Vector3f(0.0f, -10.0f, 0.0f));
			transform.basis.setIdentity();
			DefaultMotionState motion = new DefaultMotionState(transform);
			RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(0,
					motion, shape);
			rbInfo.restitution = 0.6f;

			groundBody = new RigidBody(rbInfo);
			bulletWorld.addRigidBody(groundBody);
		}

	}

	/**
	 * Creates scene objects and models. They get added to scene.
	 * 
	 * @param gl
	 */
	private void setupObjects(GL2 gl) {
		// Create models
		for (int i = 0; i < MODEL_NUM; i++) {
			SphereModelGenerator.BasePolyhedron base = SphereModelGenerator.BasePolyhedron.OCTAHEDRON;
			ModelBuilder builder = new ModelBuilder();
			if (i % 2 == 0) {
				// Sphere
				builder.addDecorators(new VertexBasedColorDecorator(),
						new NormalBasedTextureDecorator());
				models[i] = SphereModelGenerator.instance().createSphere(
						DEFUALT_OBJECT_RADIUS, 5, builder, base);
				// TODO(mgagyi): This should be done by decorators in
				// ModelBuilder
				models[i].setTexture(texture);
			} else {
				// Box
				builder.addDecorators(new NormalBasedColorDecorator());
				models[i] = BoxModelGenerator.instance().createBox(builder,
						DEFUALT_OBJECT_RADIUS, DEFUALT_OBJECT_RADIUS,
						DEFUALT_OBJECT_RADIUS);
				// models[i].setTexture(texture);
			}
			models[i].setShader(shader);
			Log.log("SceneModel #" + i + ": " + models[i].toString());
		}

		// Create scene nodes
		float x = -20.0f * (MODEL_NUM - 1), xi = 40.0f;
		for (int i = 0; i < MODEL_NUM; i++, x += xi) {
			GeometryAttribute geometry = new GeometryAttribute(models[i],
					MaterialDef.Full);
			PhysicsAttribute physics = new PhysicsAttribute(new Vector3f(x,
					40f, 0));
			GenericSceneNode obj = new GenericSceneNode(geometry, physics);
			scene.addChild(obj);
			sceneObjectNode[i] = obj;
			motionState[i] = physics;

			// Add object to JBullet world
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
				RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(
						SPHERE_MASS, motionState[i], shape, localInertia);
				rbInfo.restitution = 0.1f;
				rbInfo.friction = .5f;

				RigidBody body = new RigidBody(rbInfo);

				bulletWorld.addRigidBody(body);
				rigidBodies[i] = body;

				if (i == 0) {
					// body.applyCentralImpulse(new Vector3f(300.0f, -150.0f,
					// 10.0f));
				} else {
					// body.applyCentralImpulse(new Vector3f(0.0f, 10.0f,
					// 0.0f));
					//
				}
			}
		}
	}

	/**
	 * Sets up a patricle simulation unit. It gets added to scene.
	 * 
	 * @param gl
	 * @return
	 */
	private ParticleUnit setupParticles(GL2 gl) {
		SimpleConfig e1c = new SimpleConfig();
		e1c.count = 100;
		e1c.gravity = new Point3f(0, -1.00f, 0);
		e1c.invResistance = 0.999999f;
		e1c.boundingBox = 21.0f;
		e1c.boundingBoxBounce = 0.99f;
		ParticleUnit e1 = new ParticleUnit(new SimpleFactory(), e1c);
		scene.addChild(new GenericSceneNode(new ParticleUnitAttribute(e1)));

		return e1;
	}
}
