package org.example.logging

import java.io.File
import java.io.FileWriter

object FunctionLogger {
    private val rootDir = File(System.getProperty("user.dir"))
    private val outputDir = if (rootDir.name == "app") {
        File(rootDir.parentFile, "output")
    } else {
        File(rootDir, "output")
    }

    fun log(functionName: String, input: Double, output: Double) {
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val file = File(outputDir, "${functionName}_results.csv")
        val isNewFile = !file.exists() || file.length() == 0L

        FileWriter(file, true).use { writer ->
            if (isNewFile) {
                writer.append("input,output\n")
            }
            writer.append("$input,$output\n")
        }
    }
}