plugins {
    `java-library`
}

dependencies {
    api("org.springframework.boot:spring-boot-starter:${property("springBootVersion")}")
    api("org.springframework.boot:spring-boot-starter-security:${property("springBootVersion")}")
    api("org.springframework.boot:spring-boot-starter-web:${property("springBootVersion")}")
    api("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    api("org.mapstruct:mapstruct:${property("mapstructVersion")}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${property("mapstructVersion")}")
}

