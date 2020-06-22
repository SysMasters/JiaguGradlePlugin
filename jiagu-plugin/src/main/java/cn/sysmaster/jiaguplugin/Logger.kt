package cn.sysmaster.jiaguplugin

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

fun DefaultTask.log(message: String) = project.log(message)
fun DefaultTask.log(e: Throwable, message: String) = project.log(e, message)

fun Project.log(message: String) = logger.log(LogLevel.ERROR, message)
fun Project.log(e: Throwable, message: String) = logger.log(LogLevel.ERROR, message, e)