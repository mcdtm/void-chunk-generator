import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
  alias(libs.plugins.shadow)
}

dependencies {
  "compileOnly"(files("../.local_libraries/bukkit-26.1.jar"))
  "implementation"(project(":api"))
  "implementation"(libs.bundles.configra)
  "implementation"(libs.bstats)
  arrayOf("common", "v1_7", "v26").forEach {
    "implementation"(project(":versions:$it"))
  }
}

tasks.withType<ProcessResources> {
  val properties = mapOf(
    "version" to rootProject.version,
    "description" to (rootProject.description ?: "")
  )
  inputs.properties(properties)
  filteringCharset = Charsets.UTF_8.name()
  filesMatching(listOf("plugin.yaml", "plugin.yml")) {
    expand(properties)
  }
}

tasks.withType<ShadowJar>().configureEach {
  archiveFileName = "${rootProject.name}-${rootProject.version}.jar"
  val main = "${rootProject.group}.vcg.libs"
  relocate("me.kvd.configra", "$main.me.kvd.configra")
  relocate("org.bstats", "$main.org.bstats")
  mergeServiceFiles()
  minimize()
}