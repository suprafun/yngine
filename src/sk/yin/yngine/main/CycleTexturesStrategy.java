package sk.yin.yngine.main;

import com.sun.opengl.util.texture.Texture;
import sk.yin.yngine.geometry.Model;

class CycleTexturesStrategy implements IGLRendererStrategy {

    Model model;
    Texture[] textures;
    int current = -1;

    public CycleTexturesStrategy(Model model, Texture[] textures) {
        this.model = model;
        this.textures = textures;
    }

    public void applyStrategy() {
        if (model == null || textures == null || textures.length == 0) {
            return;
        }
        current = (current + 1) % textures.length;
        model.setTexture(textures[current]);
    }
}
