package io.github.pmdm;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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

    protected void updateStateTime(float delta) {
        stateTime += delta;
        if (estadoActual != estadoAnterior) {
            stateTime = 0;
            estadoAnterior = estadoActual;
        }
    }

    public float getDynamicScale() {
        // FÓRMULA DE PROFUNDIDAD:
        // horizonY: el punto más lejano del mapa (ej. 2000)
        // escalaHorizonte: 0.6f (más pequeño), escalaCerca: 1.1f (más grande)
        float horizonY = 2000f;
        float baseScale = 0.7f;
        float scaleRange = 0.5f;

        // Cuanto mayor es position.y, menor es la escala
        float scale = baseScale + scaleRange * (1 - (position.y / horizonY));
        return MathUtils.clamp(scale, 0.5f, 1.2f);
    }

    public void draw(SpriteBatch batch) {
        Animation<TextureRegion> anim = animations.get(estadoActual.name(), animations.get("IDLE"));
        if (anim == null) return;

        TextureRegion currentFrame = anim.getKeyFrame(stateTime);
        float scale = getDynamicScale();

        float drawWidth = currentFrame.getRegionWidth() * scale;
        float drawHeight = currentFrame.getRegionHeight() * scale;

        // Dibujamos centrado en X y apoyado en la base Y (los pies)
        batch.draw(currentFrame,
            position.x - drawWidth / 2f, position.y,
            drawWidth, drawHeight);
    }

    public abstract void update(float delta);

    public Rectangle getBounds() { return bounds; }
    public Vector2 getPosition() { return position; }
    public void dispose() {
    }
}
