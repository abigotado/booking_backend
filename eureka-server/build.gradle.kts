dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server:${property("springCloudNetflixVersion")}")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

