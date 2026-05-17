package io.github.pmdm;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {

    enum Estado { INICIO, JUGANDO, APUESTA, PELEA_MOBS, SELECCION_NIVEL, FIN_JUEGO;}
    Estado estadoActual = Estado.INICIO;
    //private Levels menuNiveles;
    private int nivelActivo = -1;
    private Menu menu;
    private ShapeRenderer shapeRenderer;
    public static SpriteBatch batch;
    private Texture background;
    private Texture textureArbol, textureFlor, textureArbol2;
    private Array<Entidad> entidadesRender = new Array<>();
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
        //menuNiveles = new Levels();

        background = new Texture(Gdx.files.internal("fondoOpt2.jpeg"));

        camara = new OrthographicCamera();
        // camara.setToOrtho(false, 1000, 480);
        camara.setToOrtho(false, 2500, 2000); // Vista completa del fondo

        mobs = new Array<>();
        plataformas = new Array<>();
        cargarEntidadesNivel();

        controllers = new Controllers();
        prota = new Personaje(100, 1650);

        Gdx.input.setInputProcessor(menu.stage);

    }

    private void volverAlMenu() {
        estadoActual = Estado.INICIO;
        prota = new Personaje(100, 1650);
        cargarEntidadesNivel();

        //menuNiveles.reset();
        Gdx.input.setInputProcessor(menu.stage);
    }

    private void cargarEntidadesNivel() {
        mobs.clear();
        plataformas.clear();

        switch (nivelActivo){
            case 1:
                prota.setPosition(100,1650);

                /*
                mobs.add(MobFactory.crearMob(MobFactory.TipoMob.SKELETON, 600, 20, Mob.Comportamiento.PATRULLA, 600,1000));
                mobs.add(MobFactory.crearMob(MobFactory.TipoMob.SKELETON, 600, 1200, Mob.Comportamiento.PERSECUCION, 0,0));
                mobs.add(MobFactory.crearMob(MobFactory.TipoMob.RAT, 500, 50, Mob.Comportamiento.PERSECUCION,0,0));
                mobs.add(MobFactory.crearMob(MobFactory.TipoMob.SLIME, 550, 1200, Mob.Comportamiento.PATRULLA, 550,700));
                mobs.add(MobFactory.crearMob(MobFactory.TipoMob.SLIME, 1750, 1200, Mob.Comportamiento.PATRULLA, 1750,2280));
                mobs.add(MobFactory.crearMob(MobFactory.TipoMob.FANTASMA, 1750, 1200, Mob.Comportamiento.PATRULLA, 560,600));
                //mobs.add(MobFactory.crearMob(MobFactory.TipoMob.HONGO, 550, 1200, Mob.Comportamiento.ESTATICO, 550,550));
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
            case 2:
                //Aquí añadimos los bloques y mobs que irán en el nivel 2
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        menu.resize(width, height);
        controllers.resize(width, height);
        //menuNiveles.resize(width, height);
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
                    nivelActivo = 1;
                    cargarFondoNivel(nivelActivo);
                    cargarEntidadesNivel();
                    estadoActual = Estado.JUGANDO;
                    Gdx.input.setInputProcessor(controllers.stage);
                }
                break;

            case JUGANDO:
                actualizarLogicaJuego();
                dibujarJuego();
                break;
                /*case INICIO:
                menu.draw();
                if (menu.isStartPressed()) {
                    estadoActual = Estado.SELECCION_NIVEL;
                    Gdx.input.setInputProcessor(menuNiveles.stage);
                    menuNiveles.iniciarTrayecto(1); // Iniciamos la animación al primer nivel
                }
                break;

            case SELECCION_NIVEL:
                menuNiveles.draw();

                if (menuNiveles.isLevelPressed()) {
                    nivelActivo = menuNiveles.getSelectedLevel();
                    cargarFondoNivel(nivelActivo); // <-- CAMBIO DE FONDO AQUÍ
                    estadoActual = Estado.JUGANDO;
                    Gdx.input.setInputProcessor(controllers.stage);
                    cargarEntidadesNivel();
                }
                break;

            case JUGANDO:
                actualizarLogicaJuego();
                dibujarJuego();
                break;*/
        }

    }
    private void dibujarJuego() {
        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        // 1. DIBUJAMOS EL FONDO (Siempre detrás de todo)
        batch.draw(background, 0, 0, 2500, 2000);

        // 2. PREPARAMOS EL Y-SORT
        entidadesRender.clear();
        if (!prota.shouldRemove()) entidadesRender.add(prota);
        for (Mob m : mobs) if (!m.shouldRemove()) entidadesRender.add(m);

        // Ordenamos: Mayor Y primero (se dibuja antes = queda detrás)
        // Menor Y al final (se dibuja después = queda delante)
        entidadesRender.sort((e1, e2) -> Float.compare(e2.getPosition().y, e1.getPosition().y));

        // 3. RENDERIZADO CON PROFUNDIDAD DINÁMICA
        // Definimos a qué altura (Y) están las bases del árbol y las flores
        // He puesto 1650 porque es donde empieza tu prota, ajústalo según el suelo del árbol
        float yBaseArbol = 1660f;
        float yBaseArbol2 = 1660f;
        float yBaseFlores = 1640f;

        boolean arbolDibujado = false;
        boolean arbol2Dibujado = false;
        boolean floresDibujadas = false;

        if (nivelActivo == 1) {
            for (Entidad e : entidadesRender) {
                // Dibujamos el árbol cuando lleguemos a su altura
                if (!arbolDibujado && e.getPosition().y < yBaseArbol) {
                    if (textureArbol != null) batch.draw(textureArbol, 950, 350, 1300, 1900);
                    arbolDibujado = true;
                }
                // Dibujamos el segundo árbol (izquierda)
                if (!arbol2Dibujado && e.getPosition().y < yBaseArbol2) {
                    if (textureArbol2 != null) batch.draw(textureArbol2, -100, 500, 900, 1400);
                    arbol2Dibujado = true;
                }
                // Dibujamos las flores cuando lleguemos a su altura
                if (!floresDibujadas && e.getPosition().y < yBaseFlores) {
                    if (textureFlor != null) batch.draw(textureFlor, 1400, 190, 500, 1000);
                    floresDibujadas = true;
                }
                // Dibujamos la entidad (prota o mob)
                e.draw(batch);
            }
            // Si no hay entidades debajo de los objetos, los dibujamos al final
            if (!arbolDibujado && textureArbol != null) batch.draw(textureArbol, 950, 350, 1300, 1900);
            if (!arbol2Dibujado && textureArbol2 != null) batch.draw(textureArbol2, 0, 450, 1100, 1600);
            if (!floresDibujadas) {
                if (textureFlor != null) batch.draw(textureFlor, 1400, 190, 500, 1000);
            }
        } else {
            // En otros niveles dibujamos normal
            for (Entidad e : entidadesRender) e.draw(batch);
        }

        for (Plataformas p : plataformas) p.draw(batch);

        batch.end();
        controllers.draw();
    }
    private void cargarFondoNivel(int nivel) {
        if (background != null) background.dispose(); // Liberar memoria del fondo anterior
        if (textureArbol != null) textureArbol.dispose();
        if (textureFlor != null) textureFlor.dispose();
        if (textureArbol2 != null) textureArbol2.dispose();

        textureArbol = null;
        textureFlor = null;
        textureArbol2 = null;

        if (nivel == 1) {
            background = new Texture(Gdx.files.internal("nivel-1.png"));
            textureArbol = new Texture(Gdx.files.internal("Copilot_20260517_174043.png"));
            textureFlor = new Texture(Gdx.files.internal("Copilot_20260517_175850.png"));
            textureArbol2 = new Texture(Gdx.files.internal("Captura de pantalla 2026-05-17 181557.png"));
        } else if (nivel == 2) {
            background = new Texture(Gdx.files.internal("fondoOpt2.jpeg")); // O el que corresponda
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

        if (mobs.size == 0 && prota.getPosition().x > 2200) {
            //avanzarAlSiguienteNivel();
        }

        checkAttack();
        actualizarCamara();
        controllers.stage.act(deltaTime);
        controllers.update(deltaTime);
    }
    /*private void avanzarAlSiguienteNivel() {
        estadoActual = Estado.SELECCION_NIVEL;
        Gdx.input.setInputProcessor(menuNiveles.stage);

        if (nivelActivo == 1) {
            menuNiveles.iniciarTrayecto(2);
        } else {
            volverAlMenu();
        }
    }*/

    private void dibujarNivel(int nivelActivo) {
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

        prota.update(deltaTime, colisiones, plataformas);
        if (prota.shouldRemove()) {
            volverAlMenu();
            return;
        }

        for (Mob m: mobs){
            if (m.isAttacking()) {
                if (m.getAttackBox().overlaps(prota.getBounds())) {
                    if (!prota.isHurt() && !prota.isDead()) {
                        prota.quitarVida(1);
                        controllers.actualizarVidas(prota.getVidas());
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

        actualizarCamara();

        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        for (Mob m: mobs) { if (!m.shouldRemove()) m.draw(batch); }
        if (!prota.shouldRemove()) prota.draw(batch);
        for (Plataformas p : plataformas) p.draw(batch);

        batch.end();

        controllers.stage.act(deltaTime);
        controllers.update(deltaTime);
        controllers.draw();


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
            if (!m.shouldRemove()) {
                m.draw(batch);
            }
        }
        if (!prota.shouldRemove()) {
            prota.draw(batch);
        } else {

        }
        for (Plataformas p : plataformas) {
            p.draw(batch);
        }
        batch.end();

//            shapeRenderer.setProjectionMatrix(camara.combined);
//            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//            shapeRenderer.setColor(0, 1, 0, 1);
//            for (Plataformas p : plataformas) {
//                shapeRenderer.rect(p.getBounds().x, p.getBounds().y, p.getBounds().width, p.getBounds().height);
//            }
//            if (!prota.shouldRemove()) {
//                shapeRenderer.rect(prota.getBounds().x, prota.getBounds().y, prota.getBounds().width, prota.getBounds().height);
//
//                shapeRenderer.setColor(1, 0, 0, 1);
//                shapeRenderer.rect(prota.getAttackBox().x, prota.getAttackBox().y, prota.getAttackBox().width, prota.getAttackBox().height);
//            }
//            shapeRenderer.setColor(0, 0, 1, 1);
//            for (Mob m: mobs){
//                if (!m.shouldRemove()) {
//                    shapeRenderer.rect(m.getBounds().x, m.getBounds().y, m.getBounds().width, m.getBounds().height);
//                    if(m.isAttacking()) {
//                        shapeRenderer.rect(m.getAttackBox().x, m.getAttackBox().y, m.getAttackBox().width, m.getAttackBox().height);
//                    }
//                }
//            }
//            shapeRenderer.end();

        controllers.stage.act(deltaTime);
        controllers.update(deltaTime);
        controllers.draw();
    }

    private void actualizarCamara() {
        /*
        camara.position.x += (prota.getPosition().x - camara.position.x) * 0.1f;
        camara.position.y += (prota.getPosition().y - camara.position.y) * 0.1f;

        camara.position.x = MathUtils.clamp(camara.position.x, camara.viewportWidth/2, 2500 - camara.viewportWidth/2);
        camara.position.y = MathUtils.clamp(camara.position.y, camara.viewportHeight/2, 2000 - camara.viewportHeight/2);
        */
        camara.position.set(2500/2f, 2000/2f, 0);
        camara.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (background != null) background.dispose();
        if (textureArbol != null) textureArbol.dispose();
        if (textureFlor != null) textureFlor.dispose();
        if (textureArbol2 != null) textureArbol2.dispose();
        shapeRenderer.dispose();
        menu.dispose();
        //menuNiveles.dispose();
        for (Mob m: mobs) m.dispose();
        for (Plataformas p : plataformas) p.dispose();
    }
}
