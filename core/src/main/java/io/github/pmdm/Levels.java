package io.github.pmdm;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Levels {
    public Stage stage;
    private Texture fondoMapa, puntoTextura;
    private Image iconoMapa;
    private boolean levelPressed = false;
    private int selectedLevel = -1;

    public Levels() {
        stage = new Stage(new FitViewport(1000, 480, new OrthographicCamera()), Main.batch);

        // 1. Cargamos el mapa completo como fondo
        fondoMapa = new Texture("nivelesAlbo.png");
        Image fondo = new Image(fondoMapa);
        fondo.setFillParent(true);
        stage.addActor(fondo);

        // 2. Icono que representa al jugador en el mapa
        puntoTextura = new Texture(Gdx.files.internal("punto-mapa.png"));
        iconoMapa = new Image(puntoTextura);
        iconoMapa.setSize(40, 40);
        iconoMapa.setOrigin(20, 20);
        // Posición inicial: esquina inferior izquierda
        iconoMapa.setPosition(80, 50);
        stage.addActor(iconoMapa);
    }

    public void iniciarTrayecto(final int nivel) {
        levelPressed = false;
        selectedLevel = -1;

        if (nivel == 1) {
            // Animación hacia el Pueblo Fantasma (NIVEL 1)
            iconoMapa.setPosition(140, 50); // Reset a la entrada
            iconoMapa.addAction(Actions.sequence(
                Actions.delay(0.7f),
                // Seguimos el camino curvo
                Actions.moveTo(180, 80, 1.0f, Interpolation.linear),
                Actions.moveTo(215, 100, 1.0f, Interpolation.linear),
                Actions.moveTo(180, 150, 1.0f, Interpolation.pow2Out),
                Actions.delay(0.5f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        selectedLevel = 1;
                        levelPressed = true;
                    }
                })
            ));
        }
        // Aquí añadiremos más niveles en el futuro (nivel == 2, etc.)
    }

    public int getSelectedLevel() {
        return selectedLevel;
    }

    public boolean isLevelPressed() {
        return levelPressed;
    }

    public void reset() {
        levelPressed = false;
        selectedLevel = -1;
        iconoMapa.setPosition(80, 50);
        iconoMapa.clearActions();
    }

    public void draw() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int w, int h) {
        stage.getViewport().update(w, h, true);
    }

    public void dispose() {
        stage.dispose();
        fondoMapa.dispose();
        puntoTextura.dispose();
    }
}
