package io.github.pmdm;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class ParallaxBackground {
    static class Layer {
        Texture texture;
        float factor; // 0 = estático, 1 = se mueve con cámara
        public Layer(Texture t, float f) { this.texture = t; this.factor = f; }
    }

    private Array<Layer> layers = new Array<>();

    public void addLayer(Texture t, float factor) {
        layers.add(new Layer(t, factor));
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        for (Layer layer : layers) {
            // El desplazamiento depende de la cámara multiplicada por el factor
            float offsetX = camera.position.x * (1 - layer.factor);
            float offsetY = camera.position.y * (1 - layer.factor);

            // Dibujamos la capa. Se suele dibujar más grande para cubrir el desplazamiento
            batch.draw(layer.texture, offsetX, offsetY, 2500, 2000);
        }
    }

    public void dispose() {
        for (Layer layer : layers) layer.texture.dispose();
    }
}
