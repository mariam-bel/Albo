package io.github.pmdm;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
public class Controllers {
    Viewport viewport;
    Stage stage;
    private boolean avanzar;
    private boolean retroceder;
    private boolean atacar;
    private boolean saltar;
    OrthographicCamera camera;
    Table tableIzquierda;
    // Salto y ataque
    Table tableDerecha;
    Table tableVidas;
    Texture texturaVida;
    Image[] iconosVidas;
    int vidasMaximas = 3;
    Animation<TextureRegion> animacionCorazon;
    float tiempoAnimacion = 0;
    public Controllers() {
        this.texturaVida = new Texture("HeartPickup.png");
        iconosVidas = new Image[vidasMaximas];
        camera = new OrthographicCamera();
        viewport = new FitViewport(1000,480, camera);
        stage = new Stage(viewport, Main.batch);
        Gdx.input.setInputProcessor(stage);

        tableVidas = new Table();
        tableVidas.setFillParent(true);
        tableVidas.top().left();
        tableVidas.pad(20);

        // BUSCAR ASSET CORAZÓN
        TextureRegion[][] temp = TextureRegion.split(texturaVida,
            texturaVida.getWidth() / 3,
            texturaVida.getHeight() / 2);

        TextureRegion[] framesCorazon = new TextureRegion[6];
        int index = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                framesCorazon[index++] = temp[i][j];
            }
        }

        animacionCorazon = new Animation<TextureRegion>(0.1f, framesCorazon);
        animacionCorazon.setPlayMode(Animation.PlayMode.LOOP);

        for(int i = 0; i < vidasMaximas; i++) {
            iconosVidas[i] = new Image(texturaVida);
            tableVidas.add(iconosVidas[i]).size(40, 40).padRight(10);
        }

        stage.addActor(tableVidas);

        tableIzquierda = new Table();
        tableIzquierda.setFillParent(true);
        tableIzquierda.left().bottom();

        tableDerecha = new Table();
        tableDerecha.setFillParent(true);
        tableDerecha.right().bottom();

        Image btnSaltar = new Image(new Texture("T_S_Down_Alt.png"));
        btnSaltar.setSize(50,50);
        btnSaltar.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                setSaltar(true);
                return true;
            }
        });

        Image btnAtacar = new Image(new Texture("T_S_Square_Alt.png"));
        btnAtacar.setSize(50,50);
        btnAtacar.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                setAtacar(true);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                setAtacar(false);
            }
        });

        Image btnAvanzar = new Image(new Texture("T_S_Right_Alt.png"));
        btnAvanzar.setSize(50,50);
        btnAvanzar.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                setAvanzar(true);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                setAvanzar(false);
            }
        });

        Image btnRetroceder = new Image(new Texture("T_S_Left_Alt.png"));
        btnRetroceder.setSize(50,50);
        btnRetroceder.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                setRetroceder(true);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                setRetroceder(false);
            }
        });

        tableIzquierda.add();
        tableIzquierda.row().pad(5,5,5,5);
        tableIzquierda.add(btnRetroceder).size(btnRetroceder.getWidth(), btnRetroceder.getHeight());
        tableIzquierda.add();
        tableIzquierda.add(btnAvanzar).size(btnAvanzar.getWidth(), btnAvanzar.getHeight());
        stage.addActor(tableIzquierda);

        tableDerecha.add();
        tableDerecha.row().pad(5,5,5,5);
        tableDerecha.add(btnAtacar).size(btnAtacar.getWidth(), btnAtacar.getHeight());
        tableDerecha.add();
        tableDerecha.add(btnSaltar).size(btnSaltar.getWidth(), btnSaltar.getHeight());
        stage.addActor(tableDerecha);
    }
    public void actualizarVidas(int vidasActuales) {
        if(vidasActuales < 0) vidasActuales = 0;
        if(vidasActuales > vidasMaximas) vidasActuales = vidasMaximas;

        for (int i = 0; i < vidasMaximas; i++) {
            if (i < vidasActuales) {
                iconosVidas[i].setVisible(true);
            } else {
                iconosVidas[i].setVisible(false);
            }
        }
    }
    public void update(float delta) {
        tiempoAnimacion += delta;
        TextureRegion frameActual = animacionCorazon.getKeyFrame(tiempoAnimacion);

        for (int i = 0; i < vidasMaximas; i++) {
            ((TextureRegionDrawable) iconosVidas[i].getDrawable()).setRegion(frameActual);
        }

        stage.act(delta);
    }
    public void draw() { stage.draw(); }
    public boolean isAvanzar() { return avanzar; }
    public void setAvanzar(boolean avanzar) { this.avanzar = avanzar; }
    public boolean isRetroceder() { return retroceder; }
    public void setRetroceder(boolean retroceder) { this.retroceder = retroceder; }

    public boolean isSaltar() {
        if (saltar){
            saltar=false;
            return true;
        }
        return false;
    }
    public void setSaltar(boolean saltar) { this.saltar = saltar; }
    public void resize(int width, int height) { viewport.update(width, height); }
    public boolean isAtacar() { return atacar; }
    public void setAtacar(boolean atacar) { this.atacar = atacar; }
    public void dispose() {
        stage.dispose();
        texturaVida.dispose();
    }
}
