package io.github.pmdm;

public class MobFactory {

    public enum TipoMob { SKELETON, RAT, SLIME }

    public static Mob crearMob(TipoMob tipo, float x, float y, Mob.Comportamiento comp, float min, float max) {
        switch (tipo) {
            case SKELETON:
                // El esqueleto tiene 12 col, 6 filas
                return new Mob(x, y, Mob.Comportamiento.PATRULLA,600,800,"skeletonBaseOutlineV2-Sheet.png",12, 6, 0, 11,1, 8,2, 10,5, 6);

            case RAT:
                // La rata tiene 10 col, 7 filas
                return new Mob(x, y,Mob.Comportamiento.PERSECUCION, 0, 0,"ratBaseV2-Sheet.png",10, 7,0,10,2,3,4,9,6,6);

            case SLIME:
                return new Mob(x, y,Mob.Comportamiento.PATRULLA, 550,900,"slimeBasicV2-Sheet.png",12, 7,0,10,1,10,3,12,6,9);

            default:
                return null;
        }
    }
}
