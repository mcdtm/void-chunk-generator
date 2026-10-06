package me.kvdpxne.vcg.api;

/**
 * Placement strategy for the border block on a void platform.
 */
public enum BorderPattern {

  /**
   * Four border blocks arranged as a 2x2 block centered on (0, 0).
   */
  SPAWN_MARKER,

  /**
   * Border blocks form a ring along the platform edge.
   */
  RING,

  /**
   * The entire platform is made of the border block.
   */
  FULL,

  /**
   * No border blocks; only the main block is used.
   */
  NONE
}