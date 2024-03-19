repositories {
  mavenCentral()
  maven(url = "https://plugins.gradle.org/m2/")
}

plugins {
  id("java-library")
  id("org.jetbrains.kotlin.jvm") version "1.9.22"
  id("com.google.protobuf") version "0.9.4"
  id("com.ncorti.ktfmt.gradle") version "0.17.0"
  id("maven-publish")
}

group = "com.boltfortesla"
version = "1.2.3"

java {
	withSourcesJar()
	withJavadocJar()
}

tasks.named("ktfmtCheckMain") {
    dependsOn("generateProto")
}

ktfmt {
  googleStyle()
}

dependencies {
  implementation("com.google.protobuf:protobuf-kotlin:3.25.2")
  implementation("org.bouncycastle:bcprov-jdk18on:1.77")
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.retrofit2:converter-gson:2.9.0")
  implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
  implementation("com.google.code.gson:gson:2.10.1")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC2")
  implementation("com.squareup.okio:okio:1.17.6")
  testImplementation("junit:junit:4.13.2")
  testImplementation("com.google.truth:truth:1.3.0")
  testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0-RC2")
  testImplementation("org.json:json:20231013")
  testImplementation(kotlin("test"))
}

protobuf {
  protoc { artifact = "com.google.protobuf:protoc:3.25.2" }

  generateProtoTasks {
    all().forEach { task ->
      task.plugins {
        create("kotlin")
      }
    }
  }
}

publishing {
  publications {
    create<MavenPublication>("teslafleetsdk") {
      from(components["java"])
    }
  }
}