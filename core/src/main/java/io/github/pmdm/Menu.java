package io.github.pmdm;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Menu {
    public Stage stage;
    private boolean startPressed = false;
    private Texture spriteSheet;

    public Menu() {
        stage = new Stage(new FitViewport(1000, 480, new OrthographicCamera()), Main.batch);

        spriteSheet = new Texture(Gdx.files.internal("6buttons.png"));

        TextureRegion[][] regiones = TextureRegion.split(spriteSheet,
            spriteSheet.getWidth() / 3,
            spriteSheet.getHeight() / 3);

        // regiones[0][0] es START (estilo 1)
        // regiones[0][2] es EXIT (estilo 1)
        // regiones[1][0] es START (estilo 2, con borde rosa) -> Usaremos estos

        TextureRegion regionStart = regiones[1][0];
        TextureRegion regionExit = regiones[1][2];

        Image btnStart = new Image(new TextureRegionDrawable(regionStart));
        Image btnExit = new Image(new TextureRegionDrawable(regionExit));

        btnStart.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                startPressed = true;
                return true;
            }
        });

        btnExit.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
                return true;
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(btnStart).size(300, 120).pad(10);
        table.row();
        table.add(btnExit).size(300, 120).pad(10);

        stage.addActor(table);
    }

    public boolean isStartPressed() { return startPressed; }
    public void draw() { stage.act(); stage.draw(); }
    public void resize(int w, int h) { stage.getViewport().update(w, h, true); }

    public void dispose() {
        stage.dispose();
        spriteSheet.dispose();
    }
}
