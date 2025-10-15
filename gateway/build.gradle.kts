dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway:4.2.0")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:${property("springCloudNetflixVersion")}")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation(project(":shared"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

