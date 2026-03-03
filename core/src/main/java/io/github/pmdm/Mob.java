package io.github.pmdm;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Objects;

public class Mob extends Entidad {
    private Texture sheet;
    private Animation<TextureRegion> walkAnimation, attack1Animation, idleAnimation, deadAnimation;
    private final float FRAME_DURATION = 0.1f;
    private Rectangle attackBox;
    private boolean isDead = false;
    private boolean eliminar=false;
    private boolean isAttacking = false;
    public enum Comportamiento { PATRULLA, PERSECUCION }
    private Comportamiento comportamiento;

    private float minX, maxX;
    private float velocidad = 120f;
    private Vector2 velocityV = new Vector2();

    private Rectangle attackArea = new Rectangle();
    private float attackRangeX = 80;
    private float attackRangeY = 60;
    private int vida;

    public Mob(float x, float y, Comportamiento tipo, float minX, float maxX, String path, int cols, int filas, int filaIdle, int framesIdle, int filaWalk, int framesWalk, int filaAttack, int framesAttack, int filaDead, int framesDead, int vidas) {
        super(x, y);
        this.sheet = new Texture(path);
        this.attackBox = new Rectangle();
        this.comportamiento = tipo;
        this.minX = minX;
        this.maxX = maxX;
        this.facingRight = false;
        this.vida=vidas;

        // Configuración de las animaciones estándar de los Mobs
        animations.put("IDLE", crearAnimacion(sheet, filaIdle, framesIdle, cols, filas, 0.1f, Animation.PlayMode.LOOP));
        animations.put("WALK", crearAnimacion(sheet, filaWalk, framesWalk, cols, filas, 0.1f, Animation.PlayMode.LOOP));
        animations.put("ATTACK", crearAnimacion(sheet, filaAttack, framesAttack, cols, filas, 0.1f, Animation.PlayMode.NORMAL));
        animations.put("DEAD", crearAnimacion(sheet, filaDead, framesDead, cols, filas, 0.1f, Animation.PlayMode.NORMAL));

        this.sprite.setSize(200, 200);
        float height=0;
        if (Objects.equals(path, "skeletonBaseOutlineV2-Sheet.png")) {
            height = 100;
        } else if (Objects.equals(path, "ratBaseV2-Sheet.png")) {
            height = 100;
        }else if (Objects.equals(path, "slimeBasicV2-Sheet.png")) {
            height=50;
        }
        this.bounds = new Rectangle(sprite.getX(), sprite.getY(), 50, height);
    }
    private Array<TextureRegion> getFrames(TextureRegion[][] regions, int fila, int cantidad) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < cantidad; i++) {
            frames.add(regions[fila][i]);
        }
        return frames;
    }
    public void updateIA(float delta, Vector2 posProtagonista, Array<Rectangle> superficies) {

        stateTime += delta;

        if (isDead) {
            estadoActual = Estado.DEAD;
            velocityV.set(0,0);

            if (animations.get("DEAD").isAnimationFinished(stateTime)) {
                eliminar = true;
            }

            sprite.setRegion(animations.get("DEAD").getKeyFrame(stateTime));
            sprite.setPosition(position.x, position.y);
            return;
        }

        if (!isAttacking) {
            if (comportamiento == Comportamiento.PERSECUCION) {
                if (Math.abs(posProtagonista.x - position.x) > 5f) {
                    if (posProtagonista.x > position.x) {
                        velocityV.x = velocidad;
                        facingRight = true;
                    } else {
                        velocityV.x = -velocidad;
                        facingRight = false;
                    }
                } else {
                    velocityV.x = 0;
                }
            } else {
                if (facingRight) {
                    velocityV.x = velocidad;
                    if (position.x >= maxX) facingRight = false;
                } else {
                    velocityV.x = -velocidad;
                    if (position.x <= minX) facingRight = true;
                }
            }
            if (velocityV.x != 0) {
                estadoActual = Estado.WALK;
            } else {
                estadoActual = Estado.IDLE;
            }
        }
        Rectangle playerRect = new Rectangle(posProtagonista.x, posProtagonista.y, 50, 100); // ajusta tamaño del jugador
        if(facingRight){
            attackArea.set(position.x +75, position.y+50, attackRangeX, attackRangeY);
        } else {
            attackArea.set(position.x+125 - attackRangeX, position.y+50, attackRangeX, attackRangeY);
        }
        if (!isAttacking&& attackArea.overlaps(playerRect)) {
            isAttacking = true;
            stateTime = 0;
            velocityV.x = 0;
        }

        if (isAttacking) {
            estadoActual = Estado.ATTACK;
            attackBox.set(attackArea);
            if (animations.get("ATTACK").isAnimationFinished(stateTime)) {
                isAttacking = false;
            }
        }


        position.x += velocityV.x * delta;
        bounds.setPosition(position.x + 75, position.y+50);
        for (Rectangle rect : superficies) {
            if (bounds.overlaps(rect)) {
                if (velocityV.x > 0) position.x = rect.x - bounds.width - 75;
                else if (velocityV.x < 0) position.x = rect.x + rect.width - 75;
                if(comportamiento == Comportamiento.PATRULLA) facingRight = !facingRight;
            }
        }
        boolean grounded = false;

        position.y += velocityV.y * delta;
        bounds.setPosition(position.x + 75, position.y+50);
        for (Rectangle rect : superficies) {
            if (bounds.overlaps(rect)) {
                if (velocityV.y < 0) {
                    position.y = rect.y+ rect.height-50;
                    velocityV.y = 0;
                    grounded=true;
                }
            }
        }
        if (!grounded) {
            velocityV.y -= 1000f * delta;
        }

        sprite.setPosition(position.x, position.y);
        sprite.setRegion(animations.get(estadoActual.name()).getKeyFrame(stateTime));
        sprite.setFlip(!facingRight, false);
    }

    @Override public void update(float delta) {}
    public Rectangle getAttackBox() { return attackBox; }
    public void setDead(boolean dead) { this.isDead = dead; }
    public boolean isDead() { return isDead; }
    public boolean isAttacking() { return isAttacking; }
    public boolean shouldRemove() {
        return eliminar;
    }
    public void recibirDanio(int cantidad) {
        if (isDead) return;

        vida -= cantidad;

        if (vida <= 0) {
            isDead = true;
            stateTime = 0;
        }
    }
    @Override
    public void dispose() {
        sheet.dispose();
    }
}
