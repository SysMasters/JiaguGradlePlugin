package cn.sysmaster.jiaguplugin.task

import cn.sysmaster.jiaguplugin.JiaguExtension
import cn.sysmaster.jiaguplugin.log
import com.android.build.gradle.AndroidConfig
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.ApkOutputFile
import org.apache.http.util.TextUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * 加固任务
 */
open class JiaguTask : DefaultTask() {

    init {
        group = "jiagu"
        description = "gradle360加固任务"
    }

    /**
     * 在 gradle 执行阶段执行
     */
    @TaskAction
    fun doAction() {
        // 获取配置的属性
        val jiaguExtension = project.extensions.getByType(JiaguExtension::class.java)

        do360JiaguExecute(jiaguExtension)
    }

    /**
     * 执行360加固
     */
    private fun do360JiaguExecute(jiaguExtension: JiaguExtension) {
        println("------------------检查加固配置信息开始------------------")
        // 用户名
        val user = jiaguExtension.user
        if (TextUtils.isEmpty(user)) {
            throw AssertionError("缺少360平台账户")
        }
        // 密码
        val pwd = jiaguExtension.pwd
        if (TextUtils.isEmpty(pwd)) {
            throw AssertionError("缺少360平台账户密码")
        }
        // 是否开启多渠道打包
        val mulpkg = jiaguExtension.mulpkg
        // 多渠道文件
        val mulPkgConfigPath = jiaguExtension.mulPkgConfigPath
        if (mulpkg) {
            if (TextUtils.isEmpty(mulPkgConfigPath)) {
                throw AssertionError("缺少多渠道配置文件路径")
            }
        }
        val android = project.extensions.getByType(BaseExtension::class.java)
        val signingConfigs = android.signingConfigs.getByName("config")

        // 签名
        val keyStorePath =
            if (TextUtils.isEmpty(jiaguExtension.keyStorePath)) signingConfigs.storeFile.absolutePath else jiaguExtension.keyStorePath
        val keyStorePwd =
            if (TextUtils.isEmpty(jiaguExtension.keyStorePwd)) signingConfigs.storePassword else jiaguExtension.keyStorePwd
        val keyStoreAlias =
            if (TextUtils.isEmpty(jiaguExtension.keyStoreAlias)) signingConfigs.keyAlias else jiaguExtension.keyStoreAlias
        val keyStoreAliasPwd =
            if (TextUtils.isEmpty(jiaguExtension.keyStoreAliasPwd)) signingConfigs.keyPassword else jiaguExtension.keyStoreAliasPwd

        if (TextUtils.isEmpty(keyStorePath)) throw AssertionError("签名路径不能为空")
        if (TextUtils.isEmpty(keyStorePwd)) throw AssertionError("签名密码不能为空")
        if (TextUtils.isEmpty(keyStoreAlias)) throw AssertionError("别名不能为空")
        if (TextUtils.isEmpty(keyStoreAliasPwd)) throw AssertionError("别名密码不能为空")
        // 360加固 jar包路径
        val jarPath = jiaguExtension.jarPath
        if (TextUtils.isEmpty(jarPath)) {
            throw AssertionError("缺少360加固jiagu.jar路径")
        }

        // 加固选项
        val config = jiaguExtension.addConfig
        // 匹配apk名称
        val matchName = jiaguExtension.matchName
        // 是否自动加固
        val auto = jiaguExtension.auto
        // apk路径
        val defaultOutPutPath =
            project.buildDir.absolutePath + File.separator + "outputs" + File.separator + "apk" + File.separator + "release"
        println("apk路径：${defaultOutPutPath}")
        val apkPath =
            if (TextUtils.isEmpty(jiaguExtension.apkPath)) defaultOutPutPath else jiaguExtension.apkPath

        // 输出路径
        val outApkPath =
            if (TextUtils.isEmpty(jiaguExtension.outApkPath)) defaultOutPutPath else jiaguExtension.outApkPath
        println("apk输出路径：${outApkPath}")

        println("------------------检查加固配置信息结束------------------")

        println("------------------360加固开始------------------")

        println("360加固jar包路径：${jarPath}")
        var apkFile: File? = null
        File(apkPath).listFiles()?.forEach { file ->
            if (file.name.contains(matchName)) {
                apkFile = file
            }
        }
        if (apkFile == null) {
            throw AssertionError("找不到要加固的apk，请检查")
        }
        // 匹配对应名称apk
        "java -Dfile.encoding=utf-8 -jar $jarPath -version".doCommand()
        "java -Dfile.encoding=utf-8 -jar $jarPath -login $user $pwd".doCommand()
        "java -Dfile.encoding=utf-8 -jar $jarPath -config $config".doCommand()
        "java -Dfile.encoding=utf-8 -jar $jarPath -importsign $keyStorePath $keyStorePwd $keyStoreAlias $keyStoreAliasPwd".doCommand()
        "java -Dfile.encoding=utf-8 -jar $jarPath -showsign".doCommand()
        if (mulpkg) {
            "java -Dfile.encoding=utf-8 -jar $jarPath -importmulpkg $mulPkgConfigPath".doCommand()
            "java -Dfile.encoding=utf-8 -jar $jarPath -showmulpkg".doCommand()
        }
        "java -Dfile.encoding=utf-8 -jar $jarPath -jiagu ${apkFile?.absolutePath} $outApkPath -autosign ${if (mulpkg) " -automulpkg" else ""}".doCommand()

        println("------------------360加固结束------------------")

        println("输出路径$outApkPath")
    }
}

/**
 * 执行命令.
 * @receiver String
 */
fun String.doCommand() {
    println("run command: $this")
    val process = Runtime.getRuntime().exec(this)
    val read = BufferedReader(InputStreamReader(process.inputStream))
    var line: String? = read.readLine()
    while (line != null) {
        println(line)
        line = read.readLine()
    }
    read.close()
    val error = BufferedReader(InputStreamReader(process.errorStream))
    line = error.readLine()
    var isError = false
    while (line != null) {
        println(line)
        line = error.readLine()
        isError = true
    }
    process.destroy()
    // 如果出现错误直接抛出异常 终止执行
    if (isError) throw Exception("cmd命令执行失败，$this")
}