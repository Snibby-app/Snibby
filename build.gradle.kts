plugins {
    java
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("java-test-fixtures")
    id("jacoco")
    id("io.freefair.lombok") version "8.6"
}

group = "com.seol"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

sourceSets {
    create("intTest") {
        java {
            setSrcDirs(listOf("src/intTest/java"))
        }
        compileClasspath += sourceSets["main"].output + sourceSets["test"].output
        runtimeClasspath += output + compileClasspath
    }
}

configurations["intTestImplementation"].extendsFrom(configurations["testImplementation"])
configurations["intTestRuntimeOnly"].extendsFrom(configurations["testRuntimeOnly"])


dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    runtimeOnly("mysql:mysql-connector-java:8.0.32")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("com.h2database:h2")
    testRuntimeOnly("de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring3x:4.16.1")

    add("intTestImplementation", "com.github.codemonstur:embedded-redis:1.4.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<Test>("intTest") {
    description = "Runs integration tests"
    group = "verification"
    testClassesDirs = sourceSets["intTest"].output.classesDirs
    classpath = sourceSets["intTest"].runtimeClasspath
    shouldRunAfter(tasks.test)
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    afterEvaluate {
        classDirectories.setFrom(
            files(classDirectories.files.map {
                fileTree(it).matching {
                    exclude("**/config/*")
                }
            })
        )
    }
}
