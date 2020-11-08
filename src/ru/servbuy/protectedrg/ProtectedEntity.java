package ru.servbuy.protectedrg;

public interface ProtectedEntity {
    static void add(ProtectedEntity e) {
        if (e instanceof ProtectedRG) {
            ProtectedRG.add((ProtectedRG)e);
        }
        else if (e instanceof ProtectedMine) {
            ProtectedMine.add((ProtectedMine)e);
        }
    }

    static void remove() {

    }

    static  void clear() {

    }
}
