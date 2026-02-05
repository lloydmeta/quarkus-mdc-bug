import com.adarshr.gradle.testlogger.TestLoggerExtension

plugins {
    java
    id("io.quarkus").version("3.31.1") // Inlined for Dependabot version management
    id("com.adarshr.test-logger").version("4.0.0")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformArtifactId: String by project

dependencies {
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:3.31.2")) // Inlined for Dependabot version management
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-security")
    implementation("io.quarkus:quarkus-opentelemetry")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")

}

group = "com.beachape"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}


configure<TestLoggerExtension> {
    showStandardStreams = true
}