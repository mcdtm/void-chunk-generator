package me.kvdpxne.vcg.api;

/**
 * Wzorzec rozmieszczenia blokow borderBlock na platformie.
 */
public enum BorderPattern {

    /**
     * 4 bedrocki w kwadracie 2x2 wokol (0,0) – pozycje (±1, ±1).
     * Klasyczne zachowanie pluginu – sluzy jako spawn marker.
     */
    SPAWN_MARKER,

    /** Ring borderBlock po obwodzie calej platformy. */
    RING,

    /** Cala platforma z borderBlock (mainBlock nieuzywany). */
    FULL,

    /** Brak borderBlock – tylko mainBlock. */
    NONE
}