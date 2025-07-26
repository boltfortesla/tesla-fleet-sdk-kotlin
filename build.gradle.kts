import com.vanniktech.maven.publish.SonatypeHost

repositories {
  mavenCentral()
  maven(url = "https://plugins.gradle.org/m2/")
}

plugins {
  id("java-library")
  id("org.jetbrains.kotlin.jvm") version "2.0.10"
  id("com.google.protobuf") version "0.9.4"
  id("com.ncorti.ktfmt.gradle") version "0.19.0"
  id("com.vanniktech.maven.publish") version "0.30.0"
}

val VERSION = "3.1.5"
group = "com.boltfortesla"
version = VERSION
val ANDROID_BUILD = project.hasProperty("android")
val ARTIFACT_ID = "tesla-fleet-sdk-kotlin"

tasks.named("ktfmtCheckMain") {
    dependsOn("generateProto")
}

ktfmt {
  googleStyle()
}

dependencies {
  if(ANDROID_BUILD) {
    implementation("com.google.protobuf:protobuf-kotlin-lite:3.25.5")
  } else {
    implementation("com.google.protobuf:protobuf-kotlin:3.25.5")
  }
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
      if(ANDROID_BUILD) {
        task.builtins {
          named("java") {
            option("lite")
          }
        }
      }
    }
  }
}

mavenPublishing {
  publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)

  signAllPublications()

  val artifactId = if(ANDROID_BUILD) {
    "$ARTIFACT_ID-android"
  } else {
    ARTIFACT_ID
  }
  coordinates("com.boltfortesla", artifactId, VERSION)
  project.base.archivesName = artifactId

  pom {
    name.set("Kotlin Tesla Fleet SDK")
    description.set("An implementation of the Tesla Fleet API in Kotlin.")
    inceptionYear.set("2024")
    url.set("https://github.com/boltfortesla/tesla-fleet-sdk-kotlin")
    licenses {
      license {
        name.set("AGPL-3.0 license")
        url.set("https://www.gnu.org/licenses/agpl-3.0.en.html")
        distribution.set("https://www.gnu.org/licenses/agpl-3.0.en.html")
      }
    }
    developers {
      developer {
        id.set("jonahwh")
        name.set("Jonah Hirsch")
        url.set("https://github.com/jonahwh/")
      }
    }
    scm {
      url.set("https://github.com/boltfortesla/tesla-fleet-sdk-kotlin/")
      connection.set("scm:git:git://github.com/boltfortesla/tesla-fleet-sdk-kotlin.git")
      developerConnection.set("scm:git:ssh://git@github.com/boltfortesla/tesla-fleet-sdk-kotlin.git")
    }
  }
}
