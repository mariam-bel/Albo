package io.github.pmdm;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Array;

public abstract class Entidad {
    protected Sprite sprite;
    protected Vector2 position, velocidad;
    protected float stateTime;
    protected Rectangle bounds;
    protected boolean facingRight = true;
    protected ObjectMap<String, Animation<TextureRegion>> animations;

    public enum Estado { IDLE, WALK, JUMP, ATTACK, HURT, DEAD }
    protected Estado estadoActual = Estado.IDLE;
    protected Estado estadoAnterior = Estado.IDLE;

    public Entidad(float x, float y) {
        this.position = new Vector2(x, y);
        this.velocidad = new Vector2(0, 0);
        this.animations = new ObjectMap<>();
        this.stateTime = 0f;
        this.sprite = new Sprite();
    }

    protected Animation<TextureRegion> crearAnimacion(Texture sheet, int fila, int cantidad, int colTotales, int filTotales, float frameDuration, Animation.PlayMode mode) {
        int fw = sheet.getWidth() / colTotales;
        int fh = sheet.getHeight() / filTotales;
        TextureRegion[][] temp = TextureRegion.split(sheet, fw, fh);
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < cantidad; i++) {
            frames.add(temp[fila][i]);
        }
        return new Animation<>(frameDuration, frames, mode);
    }
    protected Animation<TextureRegion> crearAnimacionMuchasFilas(Texture sheet, int[] filas, int[] cantidadesPorFila, int colTotales, int filTotales, float frameDuration, Animation.PlayMode mode) {

        int fw = sheet.getWidth() / colTotales;
        int fh = sheet.getHeight() / filTotales;
        TextureRegion[][] temp = TextureRegion.split(sheet, fw, fh);

        Array<TextureRegion> frames = new Array<>();

        for (int f = 0; f < filas.length; f++) {
            int fila = filas[f];
            int cantidad = cantidadesPorFila[f];

            for (int i = 0; i < cantidad; i++) {
                frames.add(temp[fila][i]);
            }
        }

        return new Animation<>(frameDuration, frames, mode);
    }

    protected void updateStateTime(float delta) {
        stateTime += delta;
        if (estadoActual != estadoAnterior) {
            stateTime = 0;
            estadoAnterior = estadoActual;
        }
    }

    public void draw(SpriteBatch batch) {
        Animation<TextureRegion> anim = animations.get(estadoActual.name(), animations.get("IDLE"));
        TextureRegion currentFrame = anim.getKeyFrame(stateTime);

        sprite.setRegion(currentFrame);
        sprite.setFlip(!facingRight, false);
        sprite.setPosition(position.x, position.y);
        sprite.draw(batch);
    }

    public abstract void update(float delta);

    public Rectangle getBounds() { return bounds; }
    public Vector2 getPosition() { return position; }
    public void dispose() {
    }
}
