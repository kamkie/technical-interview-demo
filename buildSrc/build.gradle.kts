plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

gradlePlugin {
    plugins {
        register("jacocoCoverageConventions") {
            id = "technical-interview-demo.jacoco-conventions"
            implementationClass = "team.jit.technicalinterviewdemo.build.JacocoCoverageConventionsPlugin"
        }
    }
}
