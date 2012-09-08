package sk.yin.yngine.main;

import javax.vecmath.Vector3f;

import sk.yin.yngine.scene.GenericSceneNode;
import sk.yin.yngine.scene.attributes.PhysicsAttribute;
import sk.yin.yngine.scene.camera.LookAtCamera;
import sk.yin.yngine.util.Log;

class CameraPositionChangeStrategy implements IGLRendererStrategy {

    LookAtCamera camera;
    int sceneIdx = -1;
    int positionIdx = -1;
    public final Vector3f[] POSITIONS = new Vector3f[]{new Vector3f(0, 50.0F, 150.0F), new Vector3f(randomize(0.0F, 300.0F), randomize(50.0F, 100.0F), randomize(0.0F, 300.0F)), new Vector3f(randomize(0.0F, 300.0F), randomize(50.0F, 100.0F), randomize(0.0F, 300.0F)), new Vector3f(randomize(0.0F, 300.0F), randomize(50.0F, 100.0F), randomize(0.0F, 300.0F))};
    GLRenderer outer;

    public CameraPositionChangeStrategy(LookAtCamera camera, GLRenderer outer) {
        this.outer = outer;
        this.camera = camera;
        applyStrategy();
    }

    public void applyStrategy() {
        double rnd = Math.random();
        if (positionIdx == -1 || sceneIdx == -1) {
            Log.log(this.getClass().getName() + " => camera init(...)");
            changeTarget();
            changePosition();
        } else if (rnd <= 0.125) {
            Log.log(this.getClass().getName() + " => camera changeTarget");
            changeTarget();
        } else if (rnd <= 0.375) {
            Log.log(this.getClass().getName() + " => camera changePosition");
            changePosition();
        }
    }

    private void changeTarget() {
        sceneIdx = (sceneIdx + 1) % GLRenderer.MODEL_NUM;
        GenericSceneNode target = outer.sceneObjectNode[sceneIdx];
        Vector3f targetOrigin = target.attribute(PhysicsAttribute.class).origin();
        camera.setTarget(targetOrigin);
    }

    private void changePosition() {
        positionIdx = (positionIdx + 1) % POSITIONS.length;
        camera.setPosition(POSITIONS[positionIdx]);
    }

    private float randomize(float mid, float spread) {
        return mid - spread / 2 + (float) Math.random() * spread;
    }
}
