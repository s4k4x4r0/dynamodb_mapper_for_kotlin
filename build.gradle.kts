plugins {
    kotlin("jvm") version "2.0.21"
    application

    // DynamoDB Mapper
    id("aws.sdk.kotlin.hll.dynamodbmapper.schema.generator") version "1.3.95-beta" // For the Developer Preview, use the beta version of the latest SDK.
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val awsSdkVersion: String = "1.3.95"

dependencies {
    // DynamoDB Mapper
    implementation("aws.sdk.kotlin:dynamodb-mapper:${awsSdkVersion}-beta")
    implementation("aws.sdk.kotlin:dynamodb-mapper-annotations:${awsSdkVersion}-beta")

    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("org.slf4j:slf4j-simple:2.0.16")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain{
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("MainKt")
} 