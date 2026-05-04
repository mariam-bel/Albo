package io.github.pmdm;

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

public class Levels {
    public Stage stage;
    private Texture spriteSheet;

    private boolean levelPressed = false;
    private int selectedLevel = -1; // -1 significa ningún nivel seleccionado

    public Levels() {
        stage = new Stage(new FitViewport(1000, 480, new OrthographicCamera()), Main.batch);

        spriteSheet = new Texture("nivelesAlbo.png");

        TextureRegion [][] regions = TextureRegion.split(spriteSheet,
            spriteSheet.getWidth() / 3,
            spriteSheet.getHeight() / 3);

        TextureRegion [][] levels = new TextureRegion[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                levels[i][j] = regions[i][j];
            }
        }

        Image btnLevel1 = new Image(new TextureRegionDrawable(levels[0][0]));
        btnLevel1.setSize(50,50);
        btnLevel1.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                selectedLevel = 1;
                levelPressed = true;
                return true;
            }
        });

        Image btnLevel2 = new Image(new TextureRegionDrawable(levels[0][1]));
        btnLevel2.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                selectedLevel = 2;
                levelPressed = true;
                return true;
            }
        });


        Image btnLevel3 = new Image(new TextureRegionDrawable(levels[0][2]));
        btnLevel3.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                selectedLevel = 3;
                levelPressed = true;
                return true;
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(btnLevel1).size(300, 120).pad(10);
        table.add(btnLevel2).size(300, 120).pad(10);
        table.add(btnLevel3).size(300, 120).pad(10);

        stage.addActor(table);

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
    }
    public void draw() { stage.act(); stage.draw(); }
    public void resize(int w, int h) { stage.getViewport().update(w, h, true); }

    public void dispose() {
        stage.dispose();
        spriteSheet.dispose();
    }
}
