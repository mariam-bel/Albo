package io.github.pmdm;

public class MobFactory {

    public enum TipoMob { SKELETON, RAT, SLIME, FANTASMA, HONGO }

    public static Mob crearMob(TipoMob tipo, float x, float y, Mob.Comportamiento comp, float min, float max) {
        switch (tipo) {
            case SKELETON:
                // El esqueleto tiene 12 col, 6 filas
                return new Mob(x, y,comp,min,max,"skeletonBaseV2-Sheet.png",12, 6, 0, 11,4,2,1, 8,2, 10,5, 6,2, true);

            case RAT:
                // La rata tiene 10 col, 7 filas
                return new Mob(x, y,comp, min, max,"ratBaseV2-Sheet.png",10, 7,0,10,5,2,2,3,4,9,6,6,3, true);

            case SLIME:
                return new Mob(x, y, comp, min, max,"slimeBasicV2-Sheet.png", 12, 7, 0,10, 5,2,1,10, 3,12, 6,9, 1, true);
            case FANTASMA:
                return new Mob(x, y, comp, min, max,"nivel_1/fantasma.png", 6, 6, 0,6, 4,5,1,6, 2,6, 5,4, 1,false);
            case HONGO:
                return new Mob(x, y, comp, min, max,"hongo.png", 8, 7, 0,8, 4,5,1,7, 2,8, 5,8, 3, true);
            default:
                return null;
        }
    }
}
