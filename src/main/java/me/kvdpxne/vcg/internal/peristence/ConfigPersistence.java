package me.kvdpxne.vcg.internal.peristence;

import me.kvdpxne.vcg.api.VoidWorldConfig;

import java.util.Map;

/**
 * Abstrakcja persystencji konfiguracji.
 * Implementacje moga uzywac YAML, JSON, DB itp.
 */
public interface ConfigPersistence {

    /** Laduje wszystkie konfiguracje. Zwraca pusta mape jesli brak pliku. */
    Map<String, VoidWorldConfig> load();

    /** Zapisuje wszystkie konfiguracje (atomowo jesli mozliwe). */
    void save(Map<String, VoidWorldConfig> configs);
}