plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.21.3")
}

gradlePlugin {
    plugins {
        register("externalTestingConventions") {
            id = "technical-interview-demo.external-testing-conventions"
            implementationClass = "team.jit.technicalinterviewdemo.build.ExternalTestingConventionsPlugin"
        }
        register("jacocoCoverageConventions") {
            id = "technical-interview-demo.jacoco-conventions"
            implementationClass = "team.jit.technicalinterviewdemo.build.JacocoCoverageConventionsPlugin"
        }
    }
}
