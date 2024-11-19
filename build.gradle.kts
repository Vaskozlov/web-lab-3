plugins {
    kotlin("multiplatform") version "2.0.21"
    id("war")
}

group = "org.vaskozlov.lab3"
version = "1.0"

val jsOutputDirectory = file("$projectDir/src/jvmMain/webapp/resources/js")

repositories {
    mavenCentral()
}

tasks.register<Exec>("compileIndexScss") {
    commandLine(
        "sass",
        "src/jvmMain/webapp/resources/css/index.scss",
        "src/jvmMain/webapp/resources/css/index.css"
    )
}

tasks.register<Exec>("compileMainScss") {
    commandLine(
        "sass",
        "src/jvmMain/webapp/resources/css/main.scss",
        "src/jvmMain/webapp/resources/css/main.css"
    )
}

tasks.register<Exec>("compileTypeScript") {
    doFirst {
        file("build/js/packages/web-lab3-js-main-page/kotlin").mkdirs()
    }
    
    commandLine("npx", "tsc", "--project", "tsconfig.json")
}

kotlin {
    jvm {
        withJava()
        
        tasks.war {
            dependsOn("compileIndexScss")
            dependsOn("compileMainScss")
            dependsOn("compileTypeScript")
            from("src/jvmMain/webapp")
        }
    }
    
    js("jsIntroPage", IR) {
        browser {
            commonWebpackConfig {
                outputPath = jsOutputDirectory
                outputFileName = "intro_page.js"
            }
        }
        binaries.executable()
        attributes {
            attribute(Attribute.of("jsIntroPage", String::class.java), "introPage")
        }
    }
    js("jsMainPage", IR) {
        browser {
            commonWebpackConfig {
                outputPath = jsOutputDirectory
                outputFileName = "main_page.js"
            }
        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.postgresql:postgresql:42.7.4")
                compileOnly("jakarta.faces:jakarta.faces-api:4.1.1")
                implementation("org.glassfish:jakarta.faces:4.1.1")
                compileOnly("jakarta.platform:jakarta.jakartaee-api:10.0.0")
                implementation("org.primefaces:primefaces:14.0.6:jakarta")
            }
        }
        val jvmTest by getting
        val jsIntroPageMain by getting
        val jsMainPageMain by getting
    }
}