package io.github.pmdm;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    enum Estado { INICIO, JUGANDO }
    Estado estadoActual = Estado.INICIO;
    Menu menu;
    Plataformas suelo;
    ShapeRenderer shapeRenderer;
    public static SpriteBatch batch;
    private Texture background;
    Personaje prota;
    Controllers controllers;
    OrthographicCamera camara;
    World world;
    Array<Plataformas> plataformas;
    Array<Mob> mobs;
    boolean golpeRealizado = false;

    @Override
    public void create() {

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        world = new World(new Vector2(0,-10), true);
        menu = new Menu();
        background = new Texture(Gdx.files.internal("fondoOpt2.jpeg"));

        mobs = new Array<>();
        mobs.add(MobFactory.crearMob(MobFactory.TipoMob.SKELETON, 600, 20, Mob.Comportamiento.PATRULLA, 600,1000));
        mobs.add(MobFactory.crearMob(MobFactory.TipoMob.RAT, 500, 50, Mob.Comportamiento.PERSECUCION,0,0));
        mobs.add(MobFactory.crearMob(MobFactory.TipoMob.SLIME, 550, 1200, Mob.Comportamiento.PATRULLA, 550,700));


        prota = new Personaje(100, 1650);

        plataformas = new Array<>();
        plataformas.add(new Plataformas(2100, 110, 60, 120, "plataforma2.png"));
        plataformas.add(new Plataformas(1600, 300, 60, 100, "plataforma2.png"));
        plataformas.add(new Plataformas(0, 0, 300, 740));
        plataformas.add(new Plataformas(300, 200, 280, 300));
        plataformas.add(new Plataformas(600, 200, 1500, 90));
        plataformas.add(new Plataformas(1800, 500, 1500, 100));
        plataformas.add(new Plataformas(0, 0, 2500, 1));
        controllers = new Controllers();

        Gdx.input.setInputProcessor(menu.stage);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        camara = new OrthographicCamera();
        camara.setToOrtho(false, 1000, 480);
        controllers.resize(width, height);
    }

    public void handleInput() {
        Vector2 velocidad = prota.getVelocidad();

        boolean avanzar = controllers.isAvanzar() || Gdx.input.isKeyPressed(Input.Keys.D)|| Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean retroceder = controllers.isRetroceder() || Gdx.input.isKeyPressed(Input.Keys.A)|| Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean saltar = controllers.isSaltar() || Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
        boolean atacar = controllers.isAtacar() || Gdx.input.isKeyJustPressed(Input.Keys.W);

        if (avanzar) {
            velocidad.x = 500;
        } else if (retroceder) {
            velocidad.x = -500;
        } else {
            velocidad.x = 0;
        }

        if (saltar) {
            prota.jump();
        }
        if (atacar) {
            prota.attack();
        }

        prota.setVelocidad(velocidad);
    }

    int vidaMob = 1;
    public void checkAttack() {
        for (Mob m: mobs){
            if (prota.isAttacking()) {
                if (!golpeRealizado && prota.getAttackBox().overlaps(m.getBounds())) {
                    vidaMob--;
                    golpeRealizado = true;
                    if (vidaMob <= 0) {
                        m.setDead(true);
                    }
                }
            } else {
                golpeRealizado = false;
            }
        }

    }

    @Override
    public void render() {
        if (estadoActual == Estado.INICIO) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            menu.draw();

            if (menu.isStartPressed()) {
                estadoActual = Estado.JUGANDO;
                Gdx.input.setInputProcessor(controllers.stage);
            }
        } else {
            float deltaTime = Gdx.graphics.getDeltaTime();
            handleInput();

            world.step(1/60f, 6, 2);

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            Array<Rectangle> colisiones = new Array<>();
            for (Plataformas p : plataformas) {
                colisiones.add(p.getBounds());
            }

            for (Mob m: mobs) {
                m.updateIA(deltaTime, prota.getPosition(), colisiones);
            }

            prota.update(deltaTime, colisiones);

            for (Mob m: mobs){
                if (m.isAttacking()) {
                    if (m.getAttackBox().overlaps(prota.getBounds())) {
                        if (!prota.isHurt() && !prota.isDead()) {
                            prota.quitarVida(1);
                            controllers.actualizarVidas(1);
                            if (prota.getVidas() <= 0) {
                                prota.setDead(true);
                            }
                        }
                        if (prota.isHurt()) {
                            prota.quitarVida(0);
                        }
                    }
                }
            }


            checkAttack();

            camara.position.x += (prota.getPosition().x - camara.position.x) * 0.1f;
            camara.position.y += (prota.getPosition().y - camara.position.y) * 0.1f;
            camara.position.x = MathUtils.clamp(prota.getPosition().x, camara.viewportWidth/2, Gdx.graphics.getWidth() - camara.viewportWidth/2);
            camara.position.y = MathUtils.clamp(prota.getPosition().y, camara.viewportHeight/2, Gdx.graphics.getHeight() - camara.viewportHeight/2);
            camara.update();

            batch.setProjectionMatrix(camara.combined);
            batch.begin();
            batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            for (Mob m: mobs) {
                m.draw(batch);
            }
            if (!prota.shouldRemove()) {
                prota.draw(batch);
            }
            for (Plataformas p : plataformas) {
                p.draw(batch);
            }
            batch.end();

            shapeRenderer.setProjectionMatrix(camara.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0, 1, 0, 1);
            for (Plataformas p : plataformas) {
                shapeRenderer.rect(p.getBounds().x, p.getBounds().y, p.getBounds().width, p.getBounds().height);
            }
            if (!prota.shouldRemove()) {
                shapeRenderer.rect(prota.getBounds().x, prota.getBounds().y, prota.getBounds().width, prota.getBounds().height);

                shapeRenderer.setColor(1, 0, 0, 1);
                shapeRenderer.rect(prota.getAttackBox().x, prota.getAttackBox().y, prota.getAttackBox().width, prota.getAttackBox().height);
            }
            shapeRenderer.setColor(0, 0, 1, 1);
            for (Mob m: mobs){
                if (!m.shouldRemove()) {
                    shapeRenderer.rect(m.getBounds().x, m.getBounds().y, m.getBounds().width, m.getBounds().height);
                    if(m.isAttacking()) {
                        shapeRenderer.rect(m.getAttackBox().x, m.getAttackBox().y, m.getAttackBox().width, m.getAttackBox().height);
                    }
                }
            }
            shapeRenderer.end();

            controllers.stage.act(deltaTime);
            controllers.update(deltaTime);
            controllers.draw();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        for (Mob m: mobs) {
            m.dispose();
        }
        for (Plataformas p : plataformas) {
            p.dispose();
        }
        shapeRenderer.dispose();
    }
}
