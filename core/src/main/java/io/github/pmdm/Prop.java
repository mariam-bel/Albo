package io.github.pmdm;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Prop extends Entidad {

    private Texture propTexture;

    public Prop(Texture texture, float x, float y) {
        super(x, y);
        this.propTexture = texture;

        // Creamos una animación de un solo frame para el prop
        TextureRegion region = new TextureRegion(texture);
        Array<TextureRegion> frames = new Array<>();
        frames.add(region);

        Animation<TextureRegion> staticAnim = new Animation<>(0.1f, frames);
        animations.put("IDLE", staticAnim);
    }

    @Override
    public void dispose() {
        if (propTexture != null) propTexture.dispose();
    }

    @Override
    public void update(float delta) {
        // Los props suelen ser estáticos
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Usamos el draw de Entidad que ya tiene Y-Sort y Escala Dinámica
        super.draw(batch);
    }
}
