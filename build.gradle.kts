plugins {
    id("io.spring.dependency-management") version "1.1.5"
    id("org.springframework.boot") version "3.5.0" apply false
    id("java-library") apply false
}

ext["springCloudVersion"] = "2023.0.1"
ext["mapstructVersion"] = "1.5.5.Final"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    group = "com.booking"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    dependencies {
        add("annotationProcessor", "org.projectlombok:lombok:1.18.32")
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

