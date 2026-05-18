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
    private Texture protaImg;
    private final float FRAME_DURATION = 0.1f;
    boolean suelo, isJumping;

    private boolean isAttacking = false;
    private boolean isDead = false;
    private boolean isHurt = false;
    float attackTimer = 0;
    private Rectangle hurtBox, attackBox;

    int saltos = 0;
    int numSaltos = 2;

    private float hurtTimer = 0;
    private int vidas = 3;
    boolean eliminar=false;
    private boolean isInvulnerable = false;
    private float invulnerableTimer = 0f;
    private final float INVULNERABLE_DURATION = 1.5f;
    private float gravedad;
    public Personaje(float inicioX, float inicioY) {
        super(inicioX, inicioY);

        hurtBox = new Rectangle(inicioX, inicioY, 120, 140);
        attackBox = new Rectangle();

        protaImg = new Texture("SPRITE_SHEET.png");
        int columnas = 10;
        int filas = 11;
        int frameWidth = protaImg.getWidth() / columnas;
        int frameHeight = protaImg.getHeight() / filas;
        TextureRegion[][] regions = TextureRegion.split(protaImg, frameWidth, frameHeight);

        // Cargamos todas las animaciones en el mapa heredado de Entidad
        animations.put("IDLE", new Animation<>(FRAME_DURATION, getFrames(regions, 0, 0, 6), Animation.PlayMode.LOOP));
        animations.put("WALK", new Animation<>(FRAME_DURATION, getFrames(regions, 1, 1, 8), Animation.PlayMode.LOOP));
        animations.put("JUMP", new Animation<>(FRAME_DURATION, getFrames(regions, 4, 4, 6), Animation.PlayMode.NORMAL));
        animations.put("ATTACK", new Animation<>(FRAME_DURATION, getFrames(regions, 9, 10, 10), Animation.PlayMode.NORMAL));
        animations.put("HURT", new Animation<>(FRAME_DURATION, getFrames(regions, 7, 7, 4), Animation.PlayMode.LOOP));
        animations.put("DEAD", new Animation<>(FRAME_DURATION, getFrames(regions, 6, 6, 10), Animation.PlayMode.NORMAL));

        this.estadoActual = Entidad.Estado.IDLE;
        this.estadoAnterior = Entidad.Estado.IDLE;
        suelo = true;
        gravedad = 1000f;

        bounds = new Rectangle(inicioX + 30, inicioY, 40, 70);
    }

    private Array<TextureRegion> getFrames(TextureRegion[][] regions, int filaInicio, int filaFin,int columnasPorFila) {

        Array<TextureRegion> frames = new Array<>();

        for (int fila = filaInicio; fila <= filaFin; fila++) {
            for (int col = 0; col < columnasPorFila; col++) {
                frames.add(regions[fila][col]);
            }
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
            Animation<TextureRegion> attackAnim = animations.get("ATTACK");
            if (attackAnim != null) attackTimer = attackAnim.getAnimationDuration();
            stateTime = 0;
        }
    }

    public void quitarVida(int cantidad) {
        if (!isInvulnerable && !isDead) {

            vidas -= cantidad;

            isHurt = true;
            isInvulnerable = true;

            hurtTimer = 0.5f;
            invulnerableTimer = INVULNERABLE_DURATION;

            stateTime = 0;

            if (facingRight) velocidad.x = -200;
            else velocidad.x = 200;
        }
    }

    public void update(float delta, Array<Rectangle> superficies, Array<Plataformas> plataformasOriginales) {
        stateTime += delta;
        if (!isDead) {
            velocidad.y -= gravedad * delta;
            position.x += velocidad.x * delta;
            bounds.setPosition(position.x, position.y);

            for (Plataformas p : plataformasOriginales) {
                if (p.isAtravesable()) continue;
                Rectangle rect = p.getBounds();

                if (bounds.overlaps(rect)) {

                    if (velocidad.x > 0) {
                        position.x = rect.x - bounds.width;
                    } else if (velocidad.x < 0) {
                        position.x = rect.x + rect.width;
                    }

                    velocidad.x = 0;
                    bounds.setPosition(position.x, position.y);
                }
            }

            position.y += velocidad.y * delta;
            bounds.setPosition(position.x, position.y);
            suelo = false;

            for (Plataformas p : plataformasOriginales) {
                Rectangle rect = p.getBounds();

                if (bounds.overlaps(rect)) {
                    if (p.isAtravesable()) {

                        if (velocidad.y <= 0) {

                            float personajeBottom = bounds.y;
                            float plataformaTop = rect.y + rect.height;

                            if (personajeBottom >= plataformaTop - 10) {
                                position.y = plataformaTop;
                                velocidad.y = 0;
                                suelo = true;
                                saltos = 0;
                            }
                        }

                    } else {
                        if (velocidad.y > 0) {
                            position.y = rect.y - bounds.height;
                        } else if (velocidad.y < 0) {
                            position.y = rect.y + rect.height;
                            suelo = true;
                            saltos = 0;
                        }

                        velocidad.y = 0;
                    }

                    bounds.setPosition(position.x, position.y);
                }
            }

            position.x = MathUtils.clamp(position.x, 0, 2500);
            position.y = MathUtils.clamp(position.y, 0, 2000);
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
                if (hurtTimer <= 0) {
                    isHurt = false;
                }
            }

            if (isInvulnerable) {
                invulnerableTimer -= delta;
                if (invulnerableTimer <= 0) {
                    isInvulnerable = false;
                }
            }

            if (isAttacking) {
                attackBox.set(facingRight ? position.x + 40 : position.x - 60, position.y, 60, 80);
            } else {
                attackBox.set(0, 0, 0, 0);
            }

            estadoAnterior = estadoActual;
            if (isDead) estadoActual = Estado.DEAD;
            else if (isHurt) estadoActual = Estado.HURT;
            else if (isAttacking) estadoActual = Estado.ATTACK;
            else if (Math.abs(velocidad.y) > 1f) estadoActual = Estado.JUMP;
            else if (Math.abs(velocidad.x) > 5f) estadoActual = Estado.WALK;
            else estadoActual = Estado.IDLE;

            if (estadoActual != estadoAnterior) stateTime = 0;

            // La lógica de dibujo ahora la gestiona Entidad.draw(batch)
            // Aquí solo actualizamos la posición de las hitboxes
            bounds.setPosition(position.x, position.y);
            hurtBox.setPosition(position.x, position.y);
        } else {
            if (animations.get("DEAD").isAnimationFinished(stateTime)) {
                eliminar = true;
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (isInvulnerable && (int)(invulnerableTimer * 10) % 2 == 0) return;
        super.draw(batch); // Usa Y-Sort y Escala dinámica
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
    public void setPosition(float x, float y) {
        this.position.set(x, y);
        this.bounds.setPosition(x, y);
    }
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
