import org.gradle.kotlin.dsl.jar
import org.gradle.kotlin.dsl.java
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.JarOutputStream
import kotlin.collections.set
import java.util.jar.Attributes
import java.util.jar.Manifest
import java.util.jar.JarEntry

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

tasks.test {
    useJUnitPlatform()
}

dependencies {
    // Other dependencies.
    testImplementation(kotlin("test"))
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

//tasks.register<Exec>("doc") {
//    dependsOn("dokkaHtml")
//}

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
        junitXml.outputLocation.set(file("${buildDir}/reports/xml"))
        junitXml.required.set(true) // Enable XML test reports
        junitXml.isOutputPerTestCase = false
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

tasks.register("team") {
    group = "build"
    description = "Gets 2 previous revisions from git, builds them and packages the jars into a zip"
    
    val workDir = file("$buildDir/teamTask")
    val revisionsDir = file("$buildDir/previousRevisions")
    val outputZip = file("$buildDir/previousRevisions.zip")
    
    doLast {
        workDir.deleteRecursively()
        workDir.mkdirs()
        
        exec {
            commandLine("git", "clone", projectDir.absolutePath, workDir.absolutePath)
        }
        
        val revisions = ByteArrayOutputStream().use { output ->
            exec {
                commandLine("git", "rev-list", "--max-count=3", "HEAD")
                workingDir = workDir
                standardOutput = output
            }
            output.toString().trim().lines()
        }
        
        if (revisions.size < 3) {
            throw GradleException("Not enough revisions in git history (need at least 3)")
        }
        
        // Skip the first (current) revision and take next two
        val previousRevisions = revisions.drop(1).take(2)
        
        previousRevisions.forEachIndexed { index, revision ->
            exec {
                commandLine("git", "checkout", revision)
                workingDir = workDir
            }
            
            println("WORKING DIR $workDir")
            
            exec {
                commandLine("./gradlew", "--no-daemon", "build")
                workingDir = workDir
            }
            
            val currentRevisionDir = file("${revisionsDir}_${index + 1}")
            currentRevisionDir.mkdirs()
            
            copy {
                from("$workDir/build/libs")
                into(currentRevisionDir)
            }
            
            exec {
                commandLine("zip", "-r", "$buildDir/$revision.zip", ".")
                workingDir = currentRevisionDir
            }
        }
        
        println("Previous revisions packaged to: $outputZip")
    }
}

tasks.register("env") {
    group = "application"
    description = "Builds and runs the program in alternative environments specified in env.properties"
    
    // Default environment file location
    val envFile = file("$projectDir/env.properties")
    
    doLast {
        // 1. Check if environment file exists
        if (!envFile.exists()) {
            throw GradleException("Environment file not found: ${envFile.absolutePath}")
        }
        
        // 2. Load environment properties
        val properties = Properties().apply {
            envFile.inputStream().use { load(it) }
        }
        
        // 3. Validate required properties
        val javaHome = properties.getProperty("java.home")
            ?: throw GradleException("'java.home' property not specified in env.properties")
        val jvmArgs = properties.getProperty("jvm.args", "").split(" ").filter { it.isNotBlank() }
        val envName = properties.getProperty("env.name", "custom")
        
        // 4. Build the project first
        exec {
            commandLine("./gradlew", "build")
        }
        
        // 5. Prepare Java executable path
        val javaExec = File(javaHome).resolve("bin/java").absolutePath
        if (!File(javaExec).exists()) {
            throw GradleException("Java executable not found at: $javaExec")
        }
        
        // 6. Run the application with specified environment
        println("Running in environment: $envName")
        println("Using Java: $javaHome")
        println("JVM arguments: ${jvmArgs.joinToString(" ")}")
        
        exec {
            commandLine = listOf(javaExec) +
                    jvmArgs +
                    listOf(
                        "-jar",
                        layout.buildDirectory.file("libs/${project.name}-jvm-${project.version}.jar")
                            .get().asFile.absolutePath
                    )
        }
    }
}

tasks.register("report") {
    group = "reporting"
    description = "Saves JUnit test reports to Git repository and creates a commit"
    
    // Only execute if tests pass
    dependsOn("test")
    mustRunAfter("test")
    
    doLast {
        exec {
            commandLine("zip", "-o", "-r", "${project.projectDir.absolutePath}/test-result.zip", ".")
            workingDir = file("${buildDir}/reports/xml")
        }
        
        exec {
            commandLine("git", "add", "${project.projectDir.absolutePath}/test-result.zip")
        }
        
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
        val timestamp = dateFormat.format(Date())
        
        exec {
            commandLine("git", "commit", "--allow-empty", "-m", "Test report $timestamp")
        }
        
        println("Test reports committed to Git repository")
    }
}
tasks.register("scp")
{
    
    dependsOn("build")
    mustRunAfter("build")
    
    doLast {
        
        val scanner = Scanner(System.`in`)
        val servers = (project.findProperty("servers") as? String)
            ?.split(',')
            ?.map { it.trim() }
            ?: listOf("default.server.com")
        
        servers.forEach { server ->
            val host_and_port = server.split(":")
            val host = host_and_port.first()
            val port = host_and_port.getOrElse(1) { "22" }
            val dest = host_and_port.getOrElse(2) { "/" }
            
            exec {
                commandLine("scp", "-P", port, "-r", "$buildDir/libs", "$host:$dest")
            }
        }
    }
}

tasks.register("doc") {
    group = "documentation"
    description = "Generates KDoc using Dokka, calculates MD5 and SHA-1 for project files, and updates MANIFEST.MF"
    
    dependsOn("dokkaHtml")
    mustRunAfter("dokkaHtml")
    
    val dokkaOutputDir = file("$buildDir/dokka")
    val jarFile = file("$buildDir/libs/${project.name}-${project.version}-kdoc.jar")
    
    doLast {
        
        // 2. Calculate MD5 and SHA-1 for project files
        val files = fileTree(projectDir) {
            include("**/*")
            exclude("**/build/**", "**/.gradle/**")
        }
        
        val md5Digest = MessageDigest.getInstance("MD5")
        val sha1Digest = MessageDigest.getInstance("SHA-1")
        
        files.forEach { file ->
            if (file.isFile) {
                val bytes = file.readBytes()
                md5Digest.update(bytes)
                sha1Digest.update(bytes)
            }
        }
        
        val md5Hash = md5Digest.digest().joinToString("") { "%02x".format(it) }
        val sha1Hash = sha1Digest.digest().joinToString("") { "%02x".format(it) }
        
        // 3. Update MANIFEST.MF
        val manifest = Manifest()
        manifest.mainAttributes[Attributes.Name.MANIFEST_VERSION] = "1.0"
        manifest.mainAttributes[Attributes.Name("MD5-Hash")] = md5Hash
        manifest.mainAttributes[Attributes.Name("SHA1-Hash")] = sha1Hash
        
        // 4. Create KDoc JAR
        JarOutputStream(jarFile.outputStream(), manifest).use { jar ->
            dokkaOutputDir.walkTopDown().forEach { file ->
                if (file.isFile) {
                    val entryName = dokkaOutputDir.toPath().relativize(file.toPath()).toString()
                    jar.putNextEntry(JarEntry(entryName))
                    jar.write(file.readBytes())
                    jar.closeEntry()
                }
            }
        }
        
        println("KDoc JAR created: ${jarFile.absolutePath}")
        println("MD5: $md5Hash")
        println("SHA-1: $sha1Hash")
    }
}