pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

val localExtendedSlotCapacity = System.getenv("EXTENDED_SLOT_CAPACITY_DIR")?.let(::file) ?: file("../ExtendedSlotCapacity")
if (localExtendedSlotCapacity.isDirectory) {
    includeBuild(localExtendedSlotCapacity) {
        dependencySubstitution {
            substitute(module("mod.traister101:Extended-Slot-Capacity-1.21.1")).using(project(":"))
        }
    }
}
