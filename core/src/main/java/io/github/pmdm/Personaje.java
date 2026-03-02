package io.github.pmdm;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Personaje extends Entidad {
    private Animation<TextureRegion> walkAnimation, jumpAnimation, attackAnimation, idleAnimation, hurtAnimation, deadAnimation;
    private Texture protaImg;
    private Sprite protaSprite;
    public Vector2 position, velocidad;
    private float stateTime, gravedad;
    private final float FRAME_DURATION = 0.1f;
    boolean suelo, isJumping;

    enum Estado {IDLE, WALK, JUMP, ATTACK, HURT, DEAD}
    private Estado estadoActual;
    private Estado estadoAnterior;

    private boolean isAttacking = false;
    private boolean isDead = false;
    private boolean isHurt = false;
    float attackTimer = 0;
    Rectangle bounds;
    private Rectangle hurtBox, attackBox;
    boolean facingRight = true;

    int saltos = 0;
    int numSaltos = 2;

    private float hurtTimer = 0;
    private final float HURT_DURATION = FRAME_DURATION * 8;
    private int vidas = 3;
    boolean eliminar=false;

    public Personaje(float inicioX, float inicioY) {
        super( 10, 0.2f);

        position = new Vector2(inicioX, inicioY);
        velocidad = new Vector2();

        hurtBox = new Rectangle(inicioX, inicioY, 120, 140);
        attackBox = new Rectangle();

        protaImg = new Texture("SPRITE_SHEET.png");
        int columnas = 10;
        int filas = 11;
        int frameWidth = protaImg.getWidth() / columnas;
        int frameHeight = protaImg.getHeight() / filas;
        TextureRegion[][] regions = TextureRegion.split(protaImg, frameWidth, frameHeight);

        // IDLE (fila 0)
        idleAnimation = new Animation<>(FRAME_DURATION, getFrames(regions, 0, 6), Animation.PlayMode.LOOP);
        // WALK (fila 1)
        walkAnimation = new Animation<>(FRAME_DURATION, getFrames(regions, 1, 8), Animation.PlayMode.LOOP);
        // JUMP (fila 4)
        jumpAnimation = new Animation<>(FRAME_DURATION, getFrames(regions, 4, 6), Animation.PlayMode.NORMAL);
        // ATTACK (fila 3)
        attackAnimation = new Animation<>(FRAME_DURATION, getFrames(regions, 3, 7), Animation.PlayMode.NORMAL);
        // HURT (fila 7)
        hurtAnimation = new Animation<>(FRAME_DURATION, getFrames(regions, 7, 4), Animation.PlayMode.NORMAL);
        // DEAD (fila 6)
        deadAnimation = new Animation<>(FRAME_DURATION, getFrames(regions, 6, 10), Animation.PlayMode.NORMAL);

        protaSprite = new Sprite(regions[0][0]);
        protaSprite.setSize(100, 100);

        estadoActual = Estado.IDLE;
        estadoAnterior = Estado.IDLE;
        suelo = true;
        gravedad = 1000f;

        bounds = new Rectangle(inicioX + 30, inicioY, 40, 70);
    }

    private Array<TextureRegion> getFrames(TextureRegion[][] regions, int fila, int cantidad) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < cantidad; i++) {
            frames.add(regions[fila][i]);
        }
        return frames;
    }

    public void jump() {
        if (saltos < numSaltos) {
            velocidad.y = 500f;
            isJumping = true;
            stateTime = 0;
            suelo = false;
            saltos++;
        }
    }

    public void attack() {
        if (!isAttacking) {
            isAttacking = true;
            attackTimer = FRAME_DURATION * 7;
            stateTime = 0;
        }
    }

    public void quitarVida(int cantidad) {
        if (!isHurt) {
            vidas -= cantidad;
            isHurt = true;
            hurtTimer = HURT_DURATION;
            stateTime = 0;
            if (facingRight) velocidad.x = -200; else velocidad.x = 200;
        }
    }

    public void update(float delta, Array<Rectangle> superficies) {
        stateTime += delta;
        if (!isDead) {
            velocidad.y -= gravedad * delta;
            position.x += velocidad.x * delta;
            bounds.setPosition(position.x, position.y);

            for (Rectangle rect : superficies) {
                if (bounds.overlaps(rect)) {
                    if (velocidad.x > 0) position.x = rect.x - bounds.width;
                    else if (velocidad.x < 0) position.x = rect.x + rect.width;
                    velocidad.x = 0;
                    bounds.setPosition(position.x, position.y);
                }
            }

            position.y += velocidad.y * delta;
            bounds.setPosition(position.x, position.y);
            suelo = false;

            for (Rectangle rect : superficies) {
                if (bounds.overlaps(rect)) {
                    if (velocidad.y > 0) {
                        position.y = rect.y - bounds.height;
                    } else if (velocidad.y < 0) {
                        position.y = rect.y + rect.height;
                        suelo = true;
                        saltos = 0;
                    }
                    velocidad.y = 0;
                    bounds.setPosition(position.x, position.y);
                }
            }

            position.x = MathUtils.clamp(position.x, 0, Gdx.graphics.getWidth() - protaSprite.getWidth());
            if (position.y <= 0) {
                position.y = 0;
                velocidad.y = 0;
                suelo = true;
                saltos = 0;
            }

            if (velocidad.x > 0) facingRight = true;
            else if (velocidad.x < 0) facingRight = false;

            if (isAttacking) {
                attackTimer -= delta;
                if (attackTimer <= 0) isAttacking = false;
            }
            if (isHurt) {
                hurtTimer -= delta;
                if (hurtTimer <= 0) isHurt = false;
            }

            if (isAttacking) {
                attackBox.set(facingRight ? position.x + protaSprite.getWidth() - 60 : position.x - 60, position.y, 60, 80);
            } else {
                attackBox.set(0, 0, 0, 0);
            }

            estadoAnterior = estadoActual;
            if (!isDead) {
                if (isAttacking) estadoActual = Estado.ATTACK;
                else if (Math.abs(velocidad.y) > 1f) estadoActual = Estado.JUMP;
                else if (Math.abs(velocidad.x) > 5f) estadoActual = Estado.WALK;
                else if (isHurt) estadoActual = Estado.HURT;
                else estadoActual = Estado.IDLE;
            } else {
                estadoActual = Estado.DEAD;
            }

            if (estadoActual != estadoAnterior) stateTime = 0;

            TextureRegion currentFrame = switch (estadoActual) {
                case WALK -> walkAnimation.getKeyFrame(stateTime);
                case JUMP -> jumpAnimation.getKeyFrame(stateTime);
                case ATTACK -> attackAnimation.getKeyFrame(stateTime);
                case HURT -> hurtAnimation.getKeyFrame(stateTime);
                default -> idleAnimation.getKeyFrame(stateTime);
            };

            protaSprite.setRegion(currentFrame);
            protaSprite.setFlip(!facingRight, false);

            if (!facingRight) protaSprite.setPosition(position.x - 25, position.y - 15);
            else protaSprite.setPosition(position.x - 35, position.y - 15);
            float hitboxOffsetY = 0;

            if (estadoActual == Estado.JUMP) {
                hitboxOffsetY = 10f;
            }

            bounds.setPosition(position.x, position.y + hitboxOffsetY);
            hurtBox.setPosition(position.x, position.y + hitboxOffsetY);
        }else {
            protaSprite.setRegion(deadAnimation.getKeyFrame(stateTime));
            if (deadAnimation.isAnimationFinished(stateTime)) {
                eliminar = true;
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        protaSprite.draw(batch);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void dispose() {
        if (protaImg != null) protaImg.dispose();
    }

    // Getters
    public Vector2 getPosition() { return position; }
    public Vector2 getVelocidad() { return velocidad; }
    public void setVelocidad(Vector2 v) { this.velocidad.set(v); }
    public Rectangle getAttackBox() { return attackBox; }
    public Rectangle getBounds() { return bounds; }
    public int getVidas() { return vidas; }
    public boolean isAttacking() { return isAttacking; }
    public boolean isHurt() { return isHurt; }
    public boolean isDead() { return isDead; }
    public void setDead(boolean dead) {
        if (!this.isDead && dead) {
            this.isDead = true;
            this.stateTime = 0;
            this.velocidad.set(0, 0);
        }
    }
    public boolean shouldRemove() {
        return eliminar;
    }
}
