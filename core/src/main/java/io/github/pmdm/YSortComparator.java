package io.github.pmdm;

import java.util.Comparator;

public class YSortComparator implements Comparator<Entidad> {

    @Override
    public int compare(
        Entidad a,
        Entidad b
    ) {

        return Float.compare(
            b.getYSortPosition(),
            a.getYSortPosition()
        );
    }
}
