plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    java
    id("org.cadixdev.licenser") version "0.6.1"
}

group = "de.chojo"
version = "1.0"

repositories {
    maven("https://eldonexus.de/repository/maven-public")
    maven("https://eldonexus.de/repository/maven-proxies")
    maven("https://m2.dv8tion.net/releases")
}

license {
    header(rootProject.file("HEADER.txt"))
    include("**/*.java")
}

java{
    toolchain{
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation("org.xerial", "sqlite-jdbc", "3.46.0.0")

    // database
    implementation("de.chojo", "sql-util", "1.5.0")
    implementation("de.chojo", "cjda-util", "2.4.0+alpha.11")

    // utils
    implementation("com.google.guava", "guava", "33.2.0-jre")
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", "2.17.1")

    // Logging
    implementation("org.slf4j", "slf4j-api", "2.0.13")
    implementation("org.apache.logging.log4j", "log4j-core", "2.23.1")
    implementation("org.apache.logging.log4j", "log4j-slf4j-impl", "2.23.1")
    implementation("club.minnced", "discord-webhooks", "0.8.4")

    // unit testing
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter", "junit-jupiter")
}

tasks {
    processResources {
        from(sourceSets.main.get().resources.srcDirs) {
            filesMatching("version") {
                expand(
                    "version" to project.version
                )
            }
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    javadoc {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    shadowJar{
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "de.chojo.shinra.ShinraBot"))
        }
    }

    build {
        dependsOn(shadowJar)
    }
}
