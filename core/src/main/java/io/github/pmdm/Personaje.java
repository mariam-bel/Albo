package io.github.pmdm;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Personaje{
    Sprite buttonUp;
    Texture protaImg;
    Sprite protaSprite;
    Vector2 position=new Vector2();
    Vector2 velocidad = new Vector2();


    Table table;
    public Personaje(String imagen, float inicioX, float inicioY){
        protaImg=new Texture(imagen);
        protaSprite=new Sprite(protaImg);
        position.set(inicioX,inicioY);
        protaSprite.setPosition(inicioX,inicioY);

    }
    public void update(float delta){
        float speed = 500f;
        velocidad.x = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocidad.x = speed;
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocidad.x = -speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            velocidad.y = speed;
        }


        position.x += velocidad.x * delta;
        position.x = Math.max(0, Math.min(position.x, Gdx.graphics.getWidth() - protaSprite.getWidth()));
        position.y = Math.max(0, Math.min(position.y, Gdx.graphics.getHeight() - protaSprite.getHeight()));
        protaSprite.setPosition(position.x, position.y);
    }
    public void draw(SpriteBatch batch) {
        protaSprite.draw(batch);
    }
    public void dispose(){
        protaImg.dispose();
    }
}
