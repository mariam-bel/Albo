package io.github.pmdm;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Plataformas {

    private Rectangle bounds;
    private Texture texture;
    private boolean atravesable;
    private boolean libre;

    public Plataformas(float x, float y, float width, float height, String imagen) {
        texture = new Texture(imagen);
        bounds = new Rectangle(x, y, width, height);
    }

    public boolean isAtravesable() {
        return atravesable;
    }
    public boolean isLibre() {
        return libre;
    }

    public Plataformas(float x, float y, float width, float height, boolean atravesar, boolean libre) {
        texture=null;
        bounds = new Rectangle(x, y, width, height);
        this.atravesable=atravesar;
        this.libre=libre;
    }

    public void draw(SpriteBatch batch) {
        if (texture!=null) {
            batch.draw(texture,
                bounds.x,
                bounds.y,
                bounds.width,
                bounds.height);
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
