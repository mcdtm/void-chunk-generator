package me.kvdpxne.vcg.api;

import java.util.Optional;
import java.util.Set;

import org.bukkit.World;

/**
 * Publiczne API dla DimensionManager.
 *
 * Plugin dziala samodzielnie bez tego API - wtedy konfiguracja pochodzi
 * z config.properties i jest wspolna dla wszystkich swiatow void.
 *
 * Per-swiatowe nadpisania sa nietrwale (trzymane w pamieci) - persystencja
 * nalezy do konsumenta API (DimensionManager).
 */
public interface VoidChunkGenApi {

  int API_VERSION = 3;

  /** Rejestruje per-swiatowe nadpisanie konfiguracji. Wywolac przed WorldCreator.createWorld(). */
  void registerWorldConfig(String worldName, VoidWorldConfig config);

  /** Usuwa per-swiatowe nadpisanie. */
  boolean unregisterWorldConfig(String worldName);

  Optional<VoidWorldConfig> findWorldConfig(String worldName);

  /** Zwraca konfiguracje per-swiatowa lub domyslna z config.properties. */
  VoidWorldConfig resolveConfig(String worldName);

  Set<String> getOverriddenWorlds();

  /** Zastosowuje ustawienia pogody, cyklu dnia i flag spawnu na swiecie. */
  void applyWorldSettings(World world);
}