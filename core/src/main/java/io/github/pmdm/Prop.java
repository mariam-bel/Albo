package io.github.pmdm;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Prop extends Entidad {

    private Texture texture;

    private float width;
    private float height;

    public Prop( Texture texture, float x, float y, float width, float height) {
        super(x, y);

        this.texture = texture;
        this.width = width;
        this.height = height;
        feetOffsetY = 10f;

        TextureRegion region = new TextureRegion(texture);

        Animation<TextureRegion> animStatic = new Animation<>(0.1f, region);

        animations.put("IDLE", animStatic);

        this.estadoActual = Estado.IDLE;
        this.bounds = new Rectangle(x + width * 0.35f,y, width * 0.3f,40);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void draw(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {

        if (!visible) return;

        TextureRegion region = animations.get("IDLE").getKeyFrame(0);

        renderScale = DEPTH_SCALER.getScale(position.y);

        float finalWidth = width * renderScale;

        float finalHeight = height * renderScale;

        batch.draw(region, position.x, position.y, finalWidth, finalHeight);
    }
}
