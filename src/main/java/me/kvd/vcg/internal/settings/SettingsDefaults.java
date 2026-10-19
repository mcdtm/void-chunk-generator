package me.kvd.vcg.internal.settings;

import me.kvd.configra.CommentChars;
import me.kvd.configra.core.ConfigBuilder;
import me.kvd.configra.core.ConfigFile;
import me.kvd.configra.format.ini.IniFormat;
import me.kvd.configra.schema.EnumDefaults;

/**
 * Package-private factory for the default {@code settings.ini} skeleton.
 * <p>
 * Layout (sections, ordering, comments) lives here; the individual option
 * declarations live in {@link SettingsKeys}. Adding a new option means:
 * <ol>
 *     <li>declare it in {@link SettingsKeys},</li>
 *     <li>reference it in the matching {@code section(...)} call below.</li>
 * </ol>
 * <p>
 * The value written to the file is taken from
 * {@code PropertyKey#defaultValue()}, so the generated file can never drift
 * from the defaults declared in {@link SettingsKeys}.
 */
final class SettingsDefaults {

  private SettingsDefaults() {
  }

  static ConfigFile build() {
    return EnumDefaults.of(SettingsKeys.class)
      .configure(true, new ConfigBuilder()
        .commentChar(CommentChars.SEMICOLON_AND_HASH)
        .blankAfterValues(true)
        .comment(
          """
            ============================================================
            VoidChunkGenerator - Default Settings

            File format: INI (https://en.wikipedia.org/wiki/INI_file)
            All keys use snake_case (lower_case_with_underscores).
            Every value below is applied to every void world.

            NOTE: When DimensionManager registers a per-world override
            through the API, that override takes precedence for the
            specific world.
            ============================================================
            """
        )
        .blank()
      )
      .build(IniFormat.INSTANCE);
  }
}