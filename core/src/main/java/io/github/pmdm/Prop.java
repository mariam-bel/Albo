package io.github.pmdm;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Prop extends Entidad {
    private Texture texture;
    private float width, height;

    public Prop(Texture texture, float x, float y, float width, float height) {
        super(x, y);
        this.texture = texture;
        this.width = width;
        this.height = height;

        TextureRegion region = new TextureRegion(texture);
        Animation<TextureRegion> animStatic = new Animation<>(0.1f, region);
        animations.put("IDLE", animStatic); this.estadoActual = Estado.IDLE;

    }

    @Override
    public void update(float delta) {

    }

}
