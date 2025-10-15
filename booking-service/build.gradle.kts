dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.retry:spring-retry")
    implementation("io.github.resilience4j:resilience4j-spring-boot3")
    implementation(project(":shared"))
    runtimeOnly("com.h2database:h2")
    implementation("org.mapstruct:mapstruct:${property("mapstructVersion")}")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

