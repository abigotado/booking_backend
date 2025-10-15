plugins {
    id("io.spring.dependency-management") version "1.1.5"
    id("org.springframework.boot") version "3.5.0" apply false
}

ext["springCloudVersion"] = "2023.0.1"
ext["mapstructVersion"] = "1.5.5.Final"

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    extensions.configure<org.gradle.api.plugins.JavaPluginExtension> {
        toolchain.languageVersion.set(org.gradle.jvm.toolchain.JavaLanguageVersion.of(17))
    }

    group = "com.booking"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    dependencies {
        add("annotationProcessor", "org.projectlombok:lombok:1.18.32")
        add("annotationProcessor", "org.springframework.boot:spring-boot-configuration-processor")
        add("compileOnly", "org.projectlombok:lombok:1.18.32")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

configure(listOf(project(":gateway"), project(":booking-service"), project(":hotel-service"), project(":eureka-server"))) {
    apply(plugin = "org.springframework.boot")

    dependencies {
        add("developmentOnly", "org.springframework.boot:spring-boot-devtools")
    }

    tasks.named<Jar>("jar") {
        enabled = true
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.0")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

