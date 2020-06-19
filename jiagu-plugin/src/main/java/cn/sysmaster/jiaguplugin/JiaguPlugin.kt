package cn.sysmaster.jiaguplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author sysmaster
 * 加固 Plugin
 */
class JiaguPlugin : Plugin<Project> {

    private val extensionName = "jiagu"

    override fun apply(target: Project) {
        // 检测是否是Android工程
        if (target.plugins.hasPlugin("com.android.application")) {
            // 创建扩展
            createExtensions(target)

        } else {
            throw IllegalArgumentException("requires the Android plugin to be configured")
        }
    }

    /**
     * 创建扩展
     */
    private fun createExtensions(target: Project) {
        target.extensions.create(extensionName, JiaguExtension::class.java)
    }
}
