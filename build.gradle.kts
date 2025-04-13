import org.apache.tools.ant.filters.EscapeUnicode

plugins {
    kotlin("multiplatform") version "2.0.21"
    id("war")
    id("org.jetbrains.dokka") version "2.0.0"
}

group = "org.vaskozlov.lab3"
version = "1.0"

subprojects {
    apply(plugin = "org.jetbrains.dokka")
}

val jsOutputDirectory = file("$projectDir/src/jvmMain/webapp/resources/js")

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath("xerces:xercesImpl:2.12.2")
        classpath("org.apache.ant:ant:1.10.12")
    }
}

tasks.register<Exec>("compileCommonScss") {
    commandLine(
        "sass",
        "src/jvmMain/webapp/resources/css/common.scss",
        "src/jvmMain/webapp/resources/css/common.css"
    )
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

tasks.register<Exec>("doc") {
    dependsOn("dokkaHtml")
}

tasks.register("xml") {
    group = "verification"
    description = "Validates all XML files in the project"
    
    doLast {
        val xmlFiles = fileTree(projectDir) {
            include("**/*.xml")
            exclude("**/build/**", "**/.gradle/**")
        }
        
        if (xmlFiles.isEmpty) {
            logger.lifecycle("No XML files found to validate")
            return@doLast
        }
        
        val parserFactory = javax.xml.parsers.SAXParserFactory.newInstance()
        parserFactory.isNamespaceAware = true
        parserFactory.isValidating = false // We'll validate manually
        
        xmlFiles.forEach { xmlFile ->
            try {
                val reader = org.xml.sax.helpers.XMLReaderFactory.createXMLReader()
                reader.errorHandler = object : org.xml.sax.ErrorHandler {
                    override fun warning(exception: org.xml.sax.SAXParseException) {
                        logger.warn("XML validation warning in ${xmlFile.path}: ${exception.message}")
                    }
                    
                    override fun error(exception: org.xml.sax.SAXParseException) {
                        logger.error("XML validation error in ${xmlFile.path} (line ${exception.lineNumber}, col ${exception.columnNumber}): ${exception.message}")
                    }
                    
                    override fun fatalError(exception: org.xml.sax.SAXParseException) {
                        error(exception)
                        throw exception
                    }
                }
                
                val inputSource = org.xml.sax.InputSource(xmlFile.reader())
                reader.parse(inputSource)
                logger.lifecycle("✅ Valid XML: ${xmlFile.path}")
            } catch (e: Exception) {
                throw GradleException("XML validation failed for ${xmlFile.path}", e)
            }
        }
    }
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
        
        tasks.named<Jar>("jvmJar") {
            manifest {
                attributes(
                    "Main-Class" to "org.vaskozlov.lab3.MainKt",
                    "Implementation-Title" to project.name,
                    "Implementation-Version" to project.version,
                    "Created-By" to System.getProperty("java.version"),
                    "Built-By" to System.getProperty("user.name"),
                    "Build-Jdk" to System.getProperty("java.version"),
                )
            }
        }
        
        tasks.war {
            dependsOn("compileCommonScss")
            dependsOn("compileIndexScss")
            dependsOn("compileMainScss")
            dependsOn("compileTypeScript")
            dependsOn("jsIntroPageBrowserWebpack")
            dependsOn("jsMainPageBrowserWebpack")
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
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit")) // Add JUnit support for JVM tests
            }
        }
        
        val jsIntroPageMain by getting
        val jsMainPageMain by getting
    }
}

tasks.withType<Test> {
    reports {
        junitXml.required.set(true) // Enable XML test reports
        junitXml.outputLocation.set(file("${layout.buildDirectory}/test-results")) // Set the output directory
    }
}

tasks.register("alt") {
    group = "build"
    description =
        "Creates an alternative version of the program with renamed variables and classes and packages it into a JAR"
    
    // Define replacement patterns
    val replacements = mapOf(
        "org\\.vaskozlov\\.lab3" to "org\\.vaskozlov\\.lab3\\.alt",
        "MainKt" to "AltMainKt",
        "lab3" to "lab3alt",
        "Database" to "DatabaseAlt"
        // Add more replacement patterns as needed
    )
    
    // Declare inputs and outputs for proper incremental builds
    inputs.files(fileTree("src").filter { it.isFile })
    inputs.property("replacements", replacements)
    val outputJar = layout.buildDirectory.file("libs/${project.name}-${project.version}-alt.jar")
    outputs.file(outputJar)
    
    doLast {
        // 1. Create temporary directory structure
        val altDir = file("$buildDir/alt")
        altDir.deleteRecursively()
        altDir.mkdirs()
        
        // 2. Copy and transform source files
        copy {
            from("src")
            into("$altDir/src")
            filter { line ->
                var result = line
                replacements.forEach { (from, to) ->
                    result = result.replace(from.toRegex(), to)
                }
                result
            }
        }
        
        copy {
            from("tsconfig.json")
            into(altDir)
        }
        
        // 3. Create settings file for alternative build
        file("$altDir/settings.gradle.kts").writeText(
            """
            rootProject.name = "${project.name}-alt"
        """.trimIndent()
        )
        
        // 4. Create build file for alternative build
        file("$altDir/build.gradle.kts").writeText(
            buildFile.readText().replace(
                "org.vaskozlov.lab3", "org.vaskozlov.lab3.alt"
            )
        )
        
        // 5. Copy other necessary files (like gradle.properties if exists)
        if (file("gradle.properties").exists()) {
            copy {
                from("gradle.properties")
                into(altDir)
            }
        }
        
        // 6. Build the alternative version
        exec {
            workingDir = altDir
            commandLine = listOf(
                "${project.rootDir}/gradlew",
                "build",
                "--no-daemon"
            )
        }
        
        // 7. Copy the resulting JAR
        copy {
            from("$altDir/build/libs") {
                include("*.jar")
            }
            into("$buildDir/libs")
            rename { fileName ->
                fileName.replace(".jar", "-alt.jar")
            }
        }
        
        println("Alternative version built: ${outputJar.get().asFile}")
    }
    
    dependsOn("jar")
}

tasks.register("native2ascii") {
    group = "localization"
    description = "Converts Unicode to ASCII escapes (pure Kotlin)"
    
    val srcDir = file("src/jvmMain/resources")
    val destDir = file("$buildDir/native2ascii")
    
    inputs.dir(srcDir)
    outputs.dir(destDir)
    
    doLast {
        fun String.escapeUnicode(): String {
            return map { char ->
                if (char.code > 127) "\\u${char.code.toString(16).padStart(4, '0')}"
                else char.toString()
            }.joinToString("")
        }
        
        destDir.mkdirs()
        copy {
            from(srcDir)
            into(destDir)
            include("**/*.properties")
            filter { line -> line.escapeUnicode() }
            filteringCharset = "UTF-8"
        }
        copy {
            from(srcDir)
            into(destDir)
            exclude("**/*.properties")
        }
    }
}