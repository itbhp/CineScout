plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin)
    `java-gradle-plugin`
}

gradlePlugin {
    plugins {
        create("plugin") {
            id = "cinescout.detekt"
            displayName = "CineScout Detekt"
            description = "Configure Detekt"
            implementationClass = "CineScoutDetektPlugin"
        }
    }
}

dependencies {

    implementation(libs.gradle.detekt)
}
