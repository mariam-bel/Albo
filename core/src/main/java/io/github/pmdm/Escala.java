package io.github.pmdm;

public class Escala {
        private final float minScale;
        private final float maxScale;
        private final float worldHeight;

        public Escala(float minScale,
                           float maxScale,
                           float worldHeight) {

            this.minScale = minScale;
            this.maxScale = maxScale;
            this.worldHeight = worldHeight;
        }

        public float getScale(float y) {

            // 0 arriba -> pequeño
            // abajo -> grande

            float t = 1f - (y / worldHeight);

            float scale =
                minScale +
                    (maxScale - minScale) * t;

            return Math.max(
                minScale,
                Math.min(maxScale, scale)
            );
        }
    }
