dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.spring.io/milestone")
        maven("https://repo.spring.io/snapshot")
    }
}

rootProject.name = "booking-backend"

include("shared")
include("gateway")
include("booking-service")
include("hotel-service")
include("eureka-server")

