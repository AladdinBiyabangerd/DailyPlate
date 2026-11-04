pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // Reuse an exact, already cached JVM Android fixture; other artifacts use official repos.
        mavenLocal { content { includeModule("org.robolectric", "android-all-instrumented") } }
        google()
        mavenCentral()
    }
}
rootProject.name = "Qida"
include(":app")
