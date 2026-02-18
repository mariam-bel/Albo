package io.github.pmdm;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
public class Personaje{
    Texture protaImg;
    Sprite protaSprite;
    Vector2 position=new Vector2();
    Vector2 velocidad = new Vector2();
    Vector2 touchPos;
    boolean movimientoIzquierda=false;
    boolean movimientoDerecha=false;
    public Personaje(String imagen, float inicioX, float inicioY){
        protaImg=new Texture(imagen);
        protaSprite=new Sprite(protaImg);
        position.set(inicioX,inicioY);
        protaSprite.setPosition(inicioX,inicioY);
        touchPos = new Vector2();
    }
    public void update(float delta){
        float speed = 1000f;
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

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            protaSprite.setCenterX(touchPos.x);
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
