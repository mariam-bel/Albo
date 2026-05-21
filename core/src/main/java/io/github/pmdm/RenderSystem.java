package io.github.pmdm;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import io.github.pmdm.YSortComparator;

public class RenderSystem {

    private final Array<Entidad> renderQueue;

    private final YSortComparator comparator;

    public RenderSystem() {

        renderQueue = new Array<>();

        comparator = new YSortComparator();
    }

    public void render(
        SpriteBatch batch,
        Array<Entidad> entidades
    ) {

        renderQueue.clear();

        renderQueue.addAll(entidades);

        renderQueue.sort(comparator);

        for (Entidad entidad : renderQueue) {

            entidad.draw(batch);
        }
    }
}
