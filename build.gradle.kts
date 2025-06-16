plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.n1colasgtz"
version = "1.0-SNAPSHOT"

ext {
    set("archiveBaseName", "payment-lambda")
    set("archiveVersion", project.version.toString())
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.amazonaws:aws-lambda-java-core:1.3.0")
    implementation("com.amazonaws:aws-lambda-java-log4j2:1.6.0")
    implementation("com.amazonaws:aws-java-sdk-secretsmanager:1.12.787")
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.787")
    implementation("com.stripe:stripe-java:29.2.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.0")
    implementation("org.apache.logging.log4j:log4j-core:2.24.1")
    implementation("org.apache.logging.log4j:log4j-api:2.24.1")
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    archiveBaseName.set(project.extra.get("archiveBaseName") as String)
    archiveVersion.set(project.extra.get("archiveVersion") as String)
    archiveClassifier.set("")
    manifest {
        attributes["Main-Class"] = "com.n1colasgtz.handler.PaymentLambda"
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.test {
    useJUnitPlatform()
}