package me.kvd.vcg.api;

/**
 * Marker interface implemented by all version-specific void chunk generators.
 * <p>
 * Allows version-agnostic code (e.g. listeners in {@code internal/}) to
 * recognize a void generator without importing any {@code versions.*} class.
 */
public interface VoidGenerator {
}