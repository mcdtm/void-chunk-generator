package me.kvdpxne.vcg.api;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Wolf;

public enum MobCategory {
  PASSIVE,
  HOSTILE,
  NEUTRAL;

  private static final ConcurrentHashMap<Class<?>, MobCategory> CACHE = new ConcurrentHashMap<>();

  public static MobCategory classify(final Entity entity) {
    return CACHE.computeIfAbsent(entity.getClass(), MobCategory::compute);
  }

  private static MobCategory compute(final Class<?> clazz) {
    if (Monster.class.isAssignableFrom(clazz)) {
      return HOSTILE;
    }
    if (Wolf.class.isAssignableFrom(clazz) || Ocelot.class.isAssignableFrom(clazz)) {
      return NEUTRAL;
    }
    if (Animals.class.isAssignableFrom(clazz)) {
      return PASSIVE;
    }
    return NEUTRAL;
  }
}