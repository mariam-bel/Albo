package io.github.pmdm;

import java.util.Comparator;

public class ComparadorY {
    public class YSortComparator implements Comparator<Entidad> {

        @Override
        public int compare(Entidad a, Entidad b) {

        /*
            IMPORTANTE:

            Menor Y = MÁS CERCA DE CÁMARA
            => debe renderizarse delante

            Usamos bounds.y
            porque representa los PIES.
         */

            return Float.compare(
                b.getYSortPosition(),
                a.getYSortPosition()
            );
        }
    }
}
