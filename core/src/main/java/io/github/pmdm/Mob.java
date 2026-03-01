package io.github.pmdm;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Mob extends Entidad {
    private Texture sheet;
    private Animation<TextureRegion> walkAnimation, attack1Animation, idleAnimation, deadAnimation;
    private final float FRAME_DURATION = 0.1f;
    private Rectangle attackBox;
    private boolean isDead = false;
    private boolean eliminar=false;
    private boolean isAttacking = false;

    public Mob(float x, float y, String path, int cols, int filas, int filaIdle, int framesIdle, int filaWalk, int framesWalk, int filaAttack, int framesAttack, int filaDead, int framesDead) {
        super(x, y);
        this.sheet = new Texture(path);
        this.attackBox = new Rectangle();

        // Configuramos las animaciones estándar de tus Mobs
        animations.put("IDLE", crearAnimacion(sheet, filaIdle, framesIdle, cols, filas, 0.1f, Animation.PlayMode.LOOP));
        animations.put("WALK", crearAnimacion(sheet, filaWalk, framesWalk, cols, filas, 0.1f, Animation.PlayMode.LOOP));
        animations.put("ATTACK", crearAnimacion(sheet, filaAttack, framesAttack, cols, filas, 0.1f, Animation.PlayMode.NORMAL));
        animations.put("DEAD", crearAnimacion(sheet, filaDead, framesDead, cols, filas, 0.1f, Animation.PlayMode.NORMAL));

    this.sprite.setSize(200, 200);
        this.bounds = new Rectangle(x, y, 50, 150);
    }
    private Array<TextureRegion> getFrames(TextureRegion[][] regions, int fila, int cantidad) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < cantidad; i++) {
            frames.add(regions[fila][i]);
        }
        return frames;
    }
    public void updateIA(float delta, Vector2 posProtagonista,  Array<Rectangle> superficies) {
        updateStateTime(delta);

        float dist = position.dst(posProtagonista);

        if (dist < 70f && !isAttacking) {
            isAttacking = true;
            estadoActual = Estado.ATTACK;
        }

        if (isAttacking) {
            estadoActual = Estado.ATTACK;
            attackBox.set(facingRight ? position.x + 80 : position.x - 40, position.y + 20, 60, 60);

            if (animations.get("ATTACK").isAnimationFinished(stateTime)) {
                isAttacking = false;
            }
        } else {
            estadoActual = Estado.WALK;
            float vel = 120 * delta;
            if (posProtagonista.x > position.x) { position.x += vel; facingRight = true; }
            else { position.x -= vel; facingRight = false; }
        }
        bounds.setPosition(position.x + 75, position.y);
    }

    @Override public void update(float delta) {}
    public Rectangle getAttackBox() { return attackBox; }
    public void setDead(boolean dead) { this.isDead = dead; }
    public boolean isDead() { return isDead; }
    public boolean isAttacking() { return isAttacking; }
    public boolean shouldRemove() {
        return eliminar;
    }

    @Override
    public void dispose() {
        sheet.dispose();
    }
}
