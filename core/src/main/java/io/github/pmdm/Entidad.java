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
    protected float alturaZ = 0;
    protected boolean visible = true;
    protected float stateTime;
    protected Rectangle bounds;
    protected boolean facingRight = true;
    protected ObjectMap<String, Animation<TextureRegion>> animations;
    protected float renderScale = 1f;

    protected static final Escala DEPTH_SCALER =
        new Escala(
            0.82f,
            1.08f,
            2000f
        );

    public float getAlturaZ() {
        return alturaZ;
    }

    public void setAlturaZ(float alturaZ) {
        this.alturaZ = alturaZ;
    }

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

        // Asegurar que no nos salimos de las dimensiones reales del array
        int filasReales = temp.length;
        int columnasReales = (filasReales > 0) ? temp[0].length : 0;

        int filaUso = Math.min(fila, filasReales - 1);
        int cantidadUso = Math.min(cantidad, columnasReales);

        for (int i = 0; i < cantidadUso; i++) {
            frames.add(temp[filaUso][i]);
        }
        return new Animation<>(frameDuration, frames, mode);
    }
    protected Animation<TextureRegion> crearAnimacionMuchasFilas(Texture sheet, int[] filas, int[] cantidadesPorFila, int colTotales, int filTotales, float frameDuration, Animation.PlayMode mode) {

        int fw = sheet.getWidth() / colTotales;
        int fh = sheet.getHeight() / filTotales;
        TextureRegion[][] temp = TextureRegion.split(sheet, fw, fh);
        Array<TextureRegion> frames = new Array<>();

        int filasReales = temp.length;
        int columnasReales = (filasReales > 0) ? temp[0].length : 0;

        for (int f = 0; f < filas.length; f++) {
            int fila = Math.min(filas[f], filasReales - 1);
            int cantidad = Math.min(cantidadesPorFila[f], columnasReales);

            for (int i = 0; i < cantidad; i++) {
                frames.add(temp[fila][i]);
            }
        }

        return new Animation<>(frameDuration, frames, mode);
    }

    // MÉTODO QUE DEVUELVE LA ESCALA DE LA ENTIDAD SEGÚN SU POSICIÓN EN Y
    public float getEscalaProfundidad() {
        float maxY = 2000;
        float minScale = 0.5f;
        float maxScale = 1.2f;

        float escala = minScale + (maxScale - minScale) * (1 - (position.y / maxY));

        return Math.max(minScale, Math.min(maxScale,escala));
    }

    protected void updateStateTime(float delta) {
        stateTime += delta;
        if (estadoActual != estadoAnterior) {
            stateTime = 0;
            estadoAnterior = estadoActual;
        }
    }

    public void draw(SpriteBatch batch) {
        if (!visible) return;

        Animation<TextureRegion> anim = animations.get(estadoActual.name(), animations.get("IDLE"));
        TextureRegion currentFrame = anim.getKeyFrame(stateTime);

        sprite.setRegion(currentFrame);
        sprite.setFlip(!facingRight, false);
        sprite.setPosition(position.x, position.y - sprite.getHeight()/2);

        float escala = getEscalaProfundidad();
        float anchoFinal = currentFrame.getRegionWidth()*escala;
        float altoFinal = currentFrame.getRegionHeight()*escala;

        batch.draw(currentFrame, position.x - anchoFinal/2f, position.y + alturaZ, anchoFinal* (facingRight ? 1 : -1), altoFinal);

    }

    public abstract void update(float delta);

    public Rectangle getBounds() { return bounds; }
    public Vector2 getPosition() { return position; }
    public void dispose() {
    }
}
