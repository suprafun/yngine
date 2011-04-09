package sk.yin.yngine.scripts

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
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import sk.yin.yngine.scene.attributes.ParticleUnitAttribute;
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

// Logger.getLogger(this.class.getName())

/**
 *
 * @author Yin
 */

boolean DISABLE_SHADERS
Texture texture
Texture[] textures
ShaderProgram shader
SceneGraph scene
LookAtCamera camera
GenericLightNode light0, light1

setup()

def setup() {
    texture = setupTextures()
    shader = setupShaders(gl)
}

def setupTextures() {
    String[] filenames = [
            "tex07.png",
            "tex06.2.png",
            "tex05-c.png",
            "tex05.png",
            "tex04.2.png",
            "tex04.png",
            "tex03.png",
            "tex2.png",
            "tex1.png"]
    List<URL> urls = ResourceGetter.getResources(filenames)

    textures = new Texture[urls.size()]
    int i = 0
    for (URL url : urls) {
        texture = TextureLoader.getInstance().load(url)
        texture.setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT)
        texture.setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT)
        texture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR)
        texture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR)
        textures[i++] = texture
    }
    if (gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic")) {
        float[] max = new float[1]
        gl.glGetFloatv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, max, 0)

        println "Enabling anisotropic filtering: " + max[0] + "X"
        gl.glTexParameterf(GL.GL_TEXTURE_2D,
            GL.GL_TEXTURE_MAX_ANISOTROPY_EXT,
            max[0])
    }

    return texture
}

/**
 * Loads a shader and configures it. It gets added to scene.
 * @param gl
 * @return
 */
def setupShaders(GL gl) {
    if (DISABLE_SHADERS) {
        ShaderProgram.disableShaders(gl)
    }
    shader = ShaderFactory.getInstance().loadShader(gl, "ffpe", true)
    println "Groovy) Shader to use in scene: " + shader.toString()
    return shader
}

def setupScene() {
    scene = new SceneGraph()
}

/**
 * Sets up scene camera, which can follow an object with smooth movements.
 * It gets added to scene.
 */
def setupCamera() {
    camera = new LookAtCamera()
    camera.setPosition(new Vector3f(333f, 250f, 1000f))
    camera = new SmoothingCameraProxy(camera)
    scene.addChild(new GenericSceneNode(camera))
    scene.setCamera(camera)
}


/**
 * Sets up lighting. Two lights are added to scene.
 * @param gl
 */
def setupLights(GL gl) {
    //TODO(yin): Don't use GL instance directly
    gl.glEnable(gl.GL_LIGHTING)
    gl.glLightModeli(GL.GL_LIGHT_MODEL_COLOR_CONTROL,
        GL.GL_SEPARATE_SPECULAR_COLORn)

    MaterialDef.Full.use(gl)
    gl.glColorMaterial(glFace, GL.GL_AMBIENT_AND_DIFFUSE)
    gl.glEnable(gl.GL_COLOR_MATERIAL)

    if (DISABLE_LIGHTING) {
        gl.glDisable(GL.GL_LIGHTING)
    }

    light0 = new GenericLightNode(LightType.Spot)
    light0.ambient([ 0.02f, 0.02f, 0.02f, 1.0f ])
    light0.diffuse([ 0.8f, 0.8f, 0.8f, 1.0f ])
    light0.specular([ 1.0f, 1.0f, 1.0f, 1.0f ])
    light0.cutoff(60.0f)
    light0.stopExp(0.75f)
    light0.attenuationLinear(0.01f)
    scene.addChild(light0)

    light1 = new GenericLightNode(LightType.Spot)
    light1.ambient([ 0.02f, 0.02f, 0.02f, 1.0f ])
    light1.diffuse([ 0.8f, 0.8f, 0.8f, 1.0f ])
    light1.specular([ 0.3f, 0.3f, 0.3f, 1.0f ])
    light1.cutoff(50.0f)
    light1.position(new Vector3f(0, 100.0f, 0))
    light1.stopExp(0.33f)
    light1.direction(new Vector3f(0, -1.0f, 0))
    scene.addChild(light1)
}


/**
 * Sets up JBullet simulation world.
 */
def setupPhysics() {
    CollisionConfiguration config = new DefaultCollisionConfiguration();
    SequentialImpulseConstraintSolver solver =
    new SequentialImpulseConstraintSolver();
    CollisionDispatcher dispatcher = new CollisionDispatcher(config);
    BroadphaseInterface broadphase = new DbvtBroadphase();
    bulletWorld =
    new DiscreteDynamicsWorld(dispatcher, broadphase, solver, config);
}

/**
 * Creates scene ground. It gets added to scene.
 * @param gl
 */
def setupGround(GL gl) {
    Model box = BoxModelGenerator.instance().createBox(
        new ModelBuilder().addDecorators(
            new StaticColorDecorator(0.7f, 0.7f, 0.7f)),
        100f, 1f, 100f);
    box.setTexture(texture);
    box.setShader(shader);
    GenericSceneNode node = new GenericSceneNode(
        new GeometryAttribute(box, MaterialDef.Floor),
        new TransformAttribute(new Vector3f(0f, -10f, 0f)));
    scene.addChild(node);

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

/**
 * Creates scene objects and models. They get added to scene.
 * @param gl
 */
def setupObjects(GL gl) {
    // Create models
    for (int i = 0; i < MODEL_NUM; i++) {
        SphereModelGenerator.BasePolyhedron base =
        SphereModelGenerator.BasePolyhedron.OCTAHEDRON;
        ModelBuilder builder = new ModelBuilder();
        if (i % 2 == 0) {
            // Sphere
            builder.addDecorators(
                new VertexBasedColorDecorator(),
                new NormalBasedTextureDecorator());
            models[i] =
            SphereModelGenerator.instance().createSphere(DEFUALT_OBJECT_RADIUS, 5, builder, base);
            // TODO(mgagyi): This should be done by decorators in ModelBuilder
            models[i].setTexture(texture);
        } else {
            // Box
            builder.addDecorators(
                new NormalBasedColorDecorator());
            models[i] = BoxModelGenerator.instance().createBox(builder, DEFUALT_OBJECT_RADIUS, DEFUALT_OBJECT_RADIUS,
                DEFUALT_OBJECT_RADIUS);
            //models[i].setTexture(texture);
        }
        models[i].setShader(shader);
        Log.log("SceneModel #" + i + ": " + models[i].toString());
    }

    // Create scene nodes
    float x0 = -20.0f * (MODEL_NUM - 1)
    float xi = 40.0f
    for (int i = 0; i < MODEL_NUM; i++) {
        GeometryAttribute geometry = new GeometryAttribute(models[i], MaterialDef.Full);
        PhysicsAttribute physics =
        new PhysicsAttribute(new Vector3f(x0 + i*xi, 40f, 0));
        GenericSceneNode obj = new GenericSceneNode(geometry, physics);
        scene.addChild(obj);
        sceneObjectNode[i] = obj;
        motionState[i] = physics;

        // Add object to JBullet world
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
        rbInfo.friction = 0.5f;

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
