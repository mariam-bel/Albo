package io.github.pmdm;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Prop extends Entidad {
    private Texture texture;
    private float width, height;

    public Prop(Texture texture, float x, float y, float width, float height) {
        super(x, y);
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    @Override
    public void update(float delta) {
        // Los props suelen ser estáticos, pero aquí podrías añadir
        // una pequeña oscilación si fuera un estandarte o fuego.
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Implementaremos el escalado dinámico aquí más adelante
        batch.draw(texture, position.x, position.y, width, height);
    }
}
