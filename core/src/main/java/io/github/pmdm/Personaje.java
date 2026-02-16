package io.github.pmdm;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Personaje{
    Texture protaImg;

    Sprite protaSprite;

    public Personaje(String imagen){
        protaImg=new Texture(imagen);
        protaSprite=new Sprite(protaImg);
        protaSprite.setSize(1,1);


    }
    public void draw(SpriteBatch batch) {
        protaSprite.draw(batch);

    }
}
