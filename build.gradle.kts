plugins {
	kotlin("jvm") version "2.3.10"
	id("java-library")
	id("maven-publish")
	id("nebula.release") version "19.0.10"
	jacoco
}

group = "org.shypl.tool"

kotlin {
	jvmToolchain(21)
}

repositories {
	mavenCentral()
	maven("https://nexus.capjack.ru/repository/maven")
}

dependencies {
	implementation("org.shypl.tool:tool-lang:1.1.0")
	testImplementation(kotlin("test"))
}

java {
	withSourcesJar()
}

jacoco {
	toolVersion = "0.8.14"
}

tasks.test {
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required = true
		html.required = true
	}
}

publishing {
	publications.create<MavenPublication>("Library") {
		from(components["java"])
	}
	if (project.hasProperty("shypl.maven.url")) {
		repositories.maven(project.property("shypl.maven.url") as String).credentials {
			username = project.property("shypl.maven.username") as String
			password = project.property("shypl.maven.password") as String
		}
	}
}

tasks.release {
	finalizedBy(tasks.publish)
}