repositories {
  mavenCentral()
  maven(url = "https://plugins.gradle.org/m2/")
}

plugins {
  id("java-library")
  id("org.jetbrains.kotlin.jvm") version "2.0.10"
  id("com.google.protobuf") version "0.9.4"
  id("com.ncorti.ktfmt.gradle") version "0.19.0"
  id("maven-publish")
}

group = "com.boltfortesla"
version = "3.1.0"

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
  implementation("com.google.protobuf:protobuf-kotlin:3.25.5")
  implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")
  implementation("com.squareup.retrofit2:retrofit:2.11.0")
  implementation("com.squareup.retrofit2:converter-gson:2.11.0")
  implementation("com.squareup.retrofit2:converter-scalars:2.11.0")
  implementation("com.google.code.gson:gson:2.11.0")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
  implementation("com.squareup.okio:okio:1.17.6")
  testImplementation("junit:junit:4.13.2")
  testImplementation("com.google.truth:truth:1.4.4")
  testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
  testImplementation("org.json:json:20240303")
  testImplementation(kotlin("test"))
}

protobuf {
  protoc { artifact = "com.google.protobuf:protoc:3.25.4" }

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
