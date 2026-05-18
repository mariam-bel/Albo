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

import java.util.Comparator;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    enum Estado { INICIO, JUGANDO, APUESTA, PELEA_MOBS, SELECCION_NIVEL, FIN_JUEGO;}
    Estado estadoActual = Estado.INICIO;
    private int nivelActivo = -1;
    private Menu menu;
    private ShapeRenderer shapeRenderer;
    public static SpriteBatch batch;
    private Texture background;
    private ParallaxBackground parallax;

    // Sistema de capas y ordenado
    private Array<Prop> propsNivel = new Array<>();
    private Array<Entidad> entidadesRender = new Array<>();
    private final Comparator<Entidad> ySortComparator = (e1, e2) -> Float.compare(e2.getPosition().y, e1.getPosition().y);

    private Personaje prota;
    private Controllers controllers;
    private OrthographicCamera camara;
    private World world;
    private Array<Plataformas> plataformas;
    private Array<Mob> mobs;
    private boolean golpeRealizado = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        world = new World(new Vector2(0,-10), true);
        menu = new Menu();
        background = new Texture(Gdx.files.internal("nivel-1.png"));

        camara = new OrthographicCamera();
        camara.setToOrtho(false, 2500, 2000);

        mobs = new Array<>();
        plataformas = new Array<>();
        controllers = new Controllers();
        prota = new Personaje(100, 1650);

        nivelActivo = 1;
        cargarFondoNivel(nivelActivo);
        cargarEntidadesNivel();

        Gdx.input.setInputProcessor(menu.stage);
    }

    private void volverAlMenu() {
        estadoActual = Estado.INICIO;
        prota.setPosition(100, 1650);
        cargarEntidadesNivel();
        Gdx.input.setInputProcessor(menu.stage);
    }

    private void cargarEntidadesNivel() {
        mobs.clear();
        plataformas.clear();

        switch (nivelActivo){
            case 1:
                prota.setPosition(100, 1650);

                /*
                mobs.add(MobFactory.crearMob(MobFactory.TipoMob.SKELETON, 600, 20, Mob.Comportamiento.PATRULLA, 600,1000));
                ...
                */

                plataformas.add(new Plataformas(2340, 1, 70, 100 , false));
                plataformas.add(new Plataformas(2200, 1, 175, 60 , false));
                plataformas.add(new Plataformas(1500, 300, 30, 100, true));
                plataformas.add(new Plataformas(1535, 545, 50, 60, true));
                plataformas.add(new Plataformas(1510, 790, 50, 90, true));
                plataformas.add(new Plataformas(0, 0, 300, 740, false));
                plataformas.add(new Plataformas(300, 200, 280, 300, false));
                plataformas.add(new Plataformas(600, 200, 1400, 90, false));
                plataformas.add(new Plataformas(1820, 500, 1500, 100, false));
                plataformas.add(new Plataformas(1875, 600, 100, 38, true));
                plataformas.add(new Plataformas(0, 0, 2500, 1, false));
                break;
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        menu.resize(width, height);
        controllers.resize(width, height);
    }

    public void handleInput() {
        Vector2 velocidad = prota.getVelocidad();

        boolean avanzar = controllers.isAvanzar() || Gdx.input.isKeyPressed(Input.Keys.D)|| Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean retroceder = controllers.isRetroceder() || Gdx.input.isKeyPressed(Input.Keys.A)|| Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean arriba = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
        boolean abajo = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);
        boolean saltar = controllers.isSaltar() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE);
        boolean atacar = controllers.isAtacar() || Gdx.input.isKeyJustPressed(Input.Keys.INSERT);

        if (avanzar) {
            velocidad.x = 500;
        } else if (retroceder) {
            velocidad.x = -500;
        } else {
            velocidad.x = 0;
        }

        if (arriba) {
            velocidad.y = 500;
        } else if (abajo) {
            velocidad.y = -500;
        }

        if (saltar) prota.jump();
        if (atacar) prota.attack();

        prota.setVelocidad(velocidad);
    }

    public void checkAttack() {
        for (Mob m : mobs) {
            if (prota.isAttacking()) {
                if (!golpeRealizado && prota.getAttackBox().overlaps(m.getBounds())) {
                    m.recibirDanio(1);
                    golpeRealizado = true;
                }
            } else {
                golpeRealizado = false;
            }
        }
        for (int i = mobs.size - 1; i >= 0; i--) {
            if (mobs.get(i).shouldRemove()) {
                mobs.removeIndex(i);
            }
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        switch (estadoActual) {
            case INICIO:
                menu.draw();
                if (menu.isStartPressed()) {
                    estadoActual = Estado.JUGANDO;
                    Gdx.input.setInputProcessor(controllers.stage);
                }
                break;

            case JUGANDO:
                actualizarLogicaJuego();
                dibujarJuego();
                break;
        }
    }

    private void dibujarJuego() {
        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        // 1. Fondo con Parallax
        if (parallax != null) {
            parallax.render(batch, camara);
        } else {
            batch.draw(background, 0, 0, 2500, 2000);
        }

        // 2. Y-Sort Unificado
        entidadesRender.clear();
        if (!prota.shouldRemove()) entidadesRender.add(prota);
        for (Mob m : mobs) if (!m.shouldRemove()) entidadesRender.add(m);
        for (Prop p : propsNivel) entidadesRender.add(p);

        entidadesRender.sort(ySortComparator);

        for (Entidad e : entidadesRender) {
            e.draw(batch); // Aplica escala dinámica automáticamente
        }

        for (Plataformas p : plataformas) p.draw(batch);

        batch.end();

        // Debug Colliders
        shapeRenderer.setProjectionMatrix(camara.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 1, 0, 1);
        for (Plataformas p : plataformas) {
            shapeRenderer.rect(p.getBounds().x, p.getBounds().y, p.getBounds().width, p.getBounds().height);
        }
        if (!prota.shouldRemove()) {
            shapeRenderer.setColor(1, 0, 0, 1);
            shapeRenderer.rect(prota.getBounds().x, prota.getBounds().y, prota.getBounds().width, prota.getBounds().height);
        }
        shapeRenderer.end();

        controllers.draw();
    }

    private void cargarFondoNivel(int nivel) {
        if (background != null) background.dispose();
        if (parallax != null) parallax.dispose();
        for (Prop p : propsNivel) p.dispose();
        propsNivel.clear();

        if (nivel == 1) {
            background = new Texture(Gdx.files.internal("nivel-1.png"));

            parallax = new ParallaxBackground();
            // Capa muy lejana (se mueve lento)
            parallax.addLayer(new Texture("nivel-1.png"), 0.1f);

            propsNivel.add(new Prop(new Texture("arbol1-nivel-1.png"), 1600, 350));
            propsNivel.add(new Prop(new Texture("arbol2-nivel-1.png"), 335, 700));
            propsNivel.add(new Prop(new Texture("puesto.png"), 1085, 450));
            propsNivel.add(new Prop(new Texture("flor.png"), 1650, 190));
            propsNivel.add(new Prop(new Texture("escaleras.png"), 645, -85));
        }
    }

    private void actualizarLogicaJuego() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        handleInput();
        world.step(1/60f, 6, 2);

        Array<Rectangle> colisiones = new Array<>();
        for (Plataformas p : plataformas) colisiones.add(p.getBounds());

        for (Mob m: mobs) m.updateIA(deltaTime, prota.getPosition(), colisiones);
        prota.update(deltaTime, colisiones, plataformas);

        if (prota.shouldRemove()) {
            volverAlMenu();
            return;
        }

        for (Mob m: mobs){
            if (m.isAttacking() && m.getAttackBox().overlaps(prota.getBounds())) {
                if (!prota.isHurt() && !prota.isDead()) {
                    prota.quitarVida(1);
                    controllers.actualizarVidas(prota.getVidas());
                    if (prota.getVidas() <= 0) prota.setDead(true);
                }
            }
        }

        checkAttack();
        actualizarCamara();
        controllers.stage.act(deltaTime);
        controllers.update(deltaTime);
    }

    private void actualizarCamara() {
        camara.position.set(2500/2f, 2000/2f, 0);
        camara.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (background != null) background.dispose();
        for (Prop p : propsNivel) p.dispose();
        shapeRenderer.dispose();
        menu.dispose();
        controllers.dispose();
    }
}
