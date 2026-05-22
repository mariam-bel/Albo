package io.github.pmdm;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Objects;

public class Mob extends Entidad {
    private int vidas = 3;
    private int fuerza = 1;
    public boolean golpeAplicado = false;
    private String nombre;
    private Texture sheet;
    private Animation<TextureRegion> walkAnimation, attack1Animation, idleAnimation, deadAnimation;
    private final float FRAME_DURATION = 0.1f;
    private Rectangle attackBox;
    private boolean isDead = false;
    private boolean isHurt = false;
    private boolean eliminar=false;
    private boolean isAttacking = false;

    public boolean isHurt() {
        return isHurt;
    }

    public void setHurt(boolean hurt) {
        isHurt = hurt;
    }

    public boolean isTerrestre() {
        return terrestre;
    }

    public void setTerrestre(boolean terrestre) {
        this.terrestre = terrestre;
    }

    //Necesitamos crear un nuevo comportamiento: 'ESTÁTICO'
    public enum Comportamiento { PATRULLA, PERSECUCION, ESTATICO }
    private Comportamiento comportamiento;

    private float minX, maxX;
    private float velocidad = 120f;
    private Vector2 velocityV = new Vector2();

    private Rectangle attackArea = new Rectangle();
    private float attackRangeX = 80;
    private float attackRangeY = 60;
    private int vida;
    private boolean terrestre;

    private float hurtTimer = 0f;

    public Mob(float x, float y, Comportamiento tipo, float minX, float maxX, String path, int cols, int filas, int filaIdle, int framesIdle, int filaHurt, int framesHurt, int filaWalk, int framesWalk, int filaAttack, int framesAttack, int filaDead, int framesDead, int vidas, boolean terricola) {
        super(x, y);
        this.sheet = new Texture(path);
        this.attackBox = new Rectangle();
        this.comportamiento = tipo;
        this.minX = minX;
        this.maxX = maxX;
        this.facingRight = false;
        this.vida=vidas;
        terrestre = terricola;

        // Configuración de las animaciones estándar de los Mobs
        animations.put("IDLE", crearAnimacion(sheet, filaIdle, framesIdle, cols, filas, 0.1f, Animation.PlayMode.LOOP));
        animations.put("WALK", crearAnimacion(sheet, filaWalk, framesWalk, cols, filas, 0.1f, Animation.PlayMode.LOOP));
        animations.put("HURT", crearAnimacion(sheet, filaHurt, framesHurt, cols, filas, 0.1f, Animation.PlayMode.NORMAL));
        animations.put("ATTACK", crearAnimacion(sheet, filaAttack, framesAttack, cols, filas, 0.1f, Animation.PlayMode.NORMAL));
        animations.put("DEAD", crearAnimacion(sheet, filaDead, framesDead, cols, filas, 0.1f, Animation.PlayMode.NORMAL));

        this.sprite.setSize(200, 200);
        this.sprite.setPosition(x,y);
        float height;
        switch (path) {
            case "skeletonBaseV2-Sheet.png":
            case "skeletonBaseOutlineV2-Sheet.png":
                height = 100; break;
            case "ratBaseV2-Sheet.png":
                height = 60; break;
            case "slimeBasicV2-Sheet.png":
                height = 50; break;
            case "nivel_1/fantasma.png":
                height = 100; break;
            case "hongo.png":
                height = 80; break;
            default:
                height = 80; break;
        }
        this.bounds = new Rectangle(x, y, 50, height);
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
            sprite.setSize(200, 200);
            sprite.setPosition(position.x, position.y );
            return;
        }
        if (hurtTimer > 0) {
            hurtTimer -= delta;
            estadoActual = Estado.HURT;
            velocityV.x = 0;
            sprite.setRegion(animations.get("HURT").getKeyFrame(stateTime));
            sprite.setSize(200, 200);
            sprite.setPosition(position.x , position.y );
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
            } else if (comportamiento == Comportamiento.PATRULLA) { // <-- Cambiado a else if
                if (facingRight) {
                    velocityV.x = velocidad;
                    if (position.x >= maxX) facingRight = false;
                } else {
                    velocityV.x = -velocidad;
                    if (position.x <= minX) facingRight = true;
                }
            } else if (comportamiento == Comportamiento.ESTATICO) { // <-- Nueva lógica limpia
                velocityV.x = 0;
                facingRight = (posProtagonista.x > position.x);
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
                golpeAplicado = false;
            }
        }


        position.x += velocityV.x * delta;
        bounds.setPosition(position.x , position.y );
        for (Rectangle rect : superficies) {
            if (bounds.overlaps(rect)) {
                if (velocityV.x > 0) position.x = rect.x - bounds.width;
                else if (velocityV.x < 0) position.x = rect.x + rect.width;
                bounds.setPosition(position.x , position.y );
                if(comportamiento == Comportamiento.PATRULLA) facingRight = !facingRight;
            }
        }

        position.y += velocityV.y * delta;
        bounds.setPosition(position.x , position.y );

        boolean grounded = false;
        for (Rectangle rect : superficies) {
            if (bounds.overlaps(rect)) {
                if (velocityV.y < 0) {
                    position.y = rect.y+ rect.height;
                    velocityV.y = 0;
                    grounded=true;
                    bounds.setPosition(position.x , position.y );

                }else if (velocityV.y > 0) {
                    position.y = rect.y - bounds.height;
                    velocityV.y = 0;
                    bounds.setPosition(position.x , position.y );
                }
            }
        }
        if (comportamiento != Comportamiento.ESTATICO || isTerrestre()) {
            if (!grounded) {
                velocityV.y -= 1000f * delta;
            }
        } else {
            velocityV.y = 0;
        }

        sprite.setPosition(position.x, position.y);
        bounds.setPosition(position.x , position.y );
        sprite.setRegion(animations.get(estadoActual.name()).getKeyFrame(stateTime));
        sprite.setSize(200, 200);
    }
    public void setComportamiento(Comportamiento c) { this.comportamiento = c; }
    public int getVidas() { return vidas; }
    public String getNombre() { return nombre; }

    public void quitarVida(int cantidad) {
        this.vidas -= cantidad;
        if (this.vidas <= 0) {
            this.isDead = true;
        }
    }

    public void aplicarMejorasAleatorias() {
        this.velocidad += MathUtils.random(20f, 100f);
        this.fuerza += MathUtils.random(1, 3);
        this.vidas += MathUtils.random(5, 10);
    }

    @Override public void update(float delta) {}
    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion region = new TextureRegion(sprite);
        float x = position.x - (200 - bounds.width) / 2f;
        float y = position.y - (200 - bounds.height)/2f;
        float width = 200;
        float height = 200;

        if (!facingRight) {
            batch.draw(region, x + width, y, -width, height);
        } else {
            batch.draw(region, x, y, width, height);
        }
    }
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
        }else {
            hurtTimer = 0.4f;
            stateTime = 0;
        }
        estadoActual = Estado.HURT;
    }
    @Override
    public void dispose() {
        sheet.dispose();
    }
}
