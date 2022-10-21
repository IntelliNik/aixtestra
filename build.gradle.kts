plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.6.2"
    id("com.google.cloud.tools.jib") version "2.8.0"
}

version = "0.1"
group = "com.aixtra"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation:3.7.1")
    annotationProcessor("io.micronaut.openapi:micronaut-openapi:4.5.1")
    implementation("io.micronaut:micronaut-http-client:3.7.1")
    implementation("io.micronaut:micronaut-jackson-databind:3.7.1")
    implementation("io.micronaut:micronaut-management:3.7.1")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.4")
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.0")
    implementation("ch.qos.logback:logback-classic:1.4.4")
    implementation("io.micronaut:micronaut-validation:3.7.1")
    implementation("org.jetbrains:annotations:23.0.0")

}


application {
    mainClass.set("com.aixtra.couchcode.Application")
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}

tasks {
    jib {
        to {
            image = "gcr.io/myapp/jib-image"
        }
    }
}
graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.aixtra.couchcode.*")
    }
}



