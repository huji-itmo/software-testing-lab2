plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.mockk:mockk:1.14.9")

    implementation(libs.guava)
    implementation(libs.jcommander)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {
    mainClass = "org.example.AppKt"
}

tasks.named<Test>("test") {
    useJUnitPlatform()

    addTestListener(object : TestListener {
        override fun beforeSuite(suite: TestDescriptor) {}
        override fun afterSuite(suite: TestDescriptor, result: TestResult) {
            if (suite.parent == null) {
                println("")
                println("Total tests:   ${result.testCount}")
                println("Passed:        ${result.successfulTestCount}")
                println("Failed:        ${result.failedTestCount}")
                println("Skipped:       ${result.skippedTestCount}")
                if (result.failedTestCount > 0) {
                    println("Failures:")
                    result.exceptions.forEach { ex ->
                        println("  - ${ex.message?.take(100)}")
                    }
                }
                println("Duration:      ${result.endTime - result.startTime} ms")
            }
        }
        override fun beforeTest(testDescriptor: TestDescriptor) {}
        override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {}
    })

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}
