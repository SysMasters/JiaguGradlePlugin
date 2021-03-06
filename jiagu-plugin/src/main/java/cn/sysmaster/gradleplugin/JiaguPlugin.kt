package cn.sysmaster.gradleplugin

import cn.sysmaster.gradleplugin.task.JiaguTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author sysmaster
 * 加固 Plugin
 */
class JiaguPlugin : Plugin<Project> {

    private val extensionName = "jiagu"
    private val taskName = "jiagu"

    override fun apply(target: Project) {
        // 检测是否是Android工程
        if (target.plugins.hasPlugin("com.android.application")) {
            // 创建扩展
            createExtensions(target)
            // 创建任务
            createTasks(target)

            target.afterEvaluate {
                val jiaguExtension = target.extensions.getByType(JiaguExtension::class.java)
                if (jiaguExtension.auto) {
                    applyTask(target)
                }
            }
        } else {
            throw IllegalArgumentException("requires the Android plugin to be configured")
        }
    }

    /**
     * 创建扩展
     * @param target Project
     */
    private fun createExtensions(target: Project) {
        target.extensions.create(extensionName, JiaguExtension::class.java)
    }


    /**
     * 创建任务
     * @param target Project
     */
    private fun createTasks(target: Project) {
        target.tasks.create(taskName, JiaguTask::class.java)
    }

    /**
     * 应用任务.
     * @param target Project
     */
    private fun applyTask(target: Project) {
        // 遍历任务
        target.tasks.filter { it.name.contains("assemble(.*)Release".toRegex()) }
            .forEach {
                it.finalizedBy(taskName)
            }
    }
}
