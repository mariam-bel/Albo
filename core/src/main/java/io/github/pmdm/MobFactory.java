package io.github.pmdm;

public class MobFactory {

    public enum TipoMob { SKELETON, RAT, SLIME }

    public static Mob crearMob(TipoMob tipo, float x, float y, Mob.Comportamiento comp, float min, float max) {
        switch (tipo) {
            case SKELETON:
                // El esqueleto tiene 12 col, 6 filas
                return new Mob(x, y,comp,min,max,"skeletonBaseOutlineV2-Sheet.png",12, 6, 0, 11,1, 8,2, 10,5, 6,2);

            case RAT:
                // La rata tiene 10 col, 7 filas
                return new Mob(x, y,comp, min, max,"ratBaseV2-Sheet.png",10, 7,0,10,2,3,4,9,6,6,3);

            case SLIME:
                return new Mob(x, y, comp, min, max,"slimeBasicV2-Sheet.png", 12, 7, 0,10, 1,10, 3,12, 6,9, 1);
            default:
                return null;
        }
    }
}
