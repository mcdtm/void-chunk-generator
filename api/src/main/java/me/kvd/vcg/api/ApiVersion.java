package me.kvd.vcg.api;

/**
 * Holds the public API version of VoidChunkGenerator.
 * <p>
 * The version is incremented on every breaking change to the API contract.
 */
public final class ApiVersion {

  /**
   * Current API version.
   */
  public static final String VERSION = "0.1.0";

  private ApiVersion() {
    throw new UnsupportedOperationException("Utility class");
  }
}