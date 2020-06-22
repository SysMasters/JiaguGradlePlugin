package cn.sysmaster.jiaguplugin

/**
 * @author sysmaster
 * @date 2020/6/19
 * @describe  获取配置属性
 */
open class JiaguExtension {

    /** 用户名 */
    var user = ""

    /** 密码 */
    var pwd = ""

    /** 高级加固选项 默认配置支持x86架构 */
    var addConfig = "-x86"

    /** 按名称匹配apk */
    var matchName = "apk"

    /** apk文件所在文件夹路径 (默认为build\outputs\apk\release)*/
    var apkPath = ""

    /** 加固后输出路径 */
    var outApkPath = ""

    /** 是否开启多渠道打包 */
    var mulpkg = false

    /** 多渠道文件配置路径 */
    var mulPkgConfigPath = ""

    /** 是否自动加固 */
    var auto = false

    /** 360jiagu jar包路径 */
    var jarPath = ""

    /** 签名路径 */
    var keyStorePath = ""

    /** 签名密码 */
    var keyStorePwd = ""

    /** 别名 */
    var keyStoreAlias = ""

    /** 别名密码 */
    var keyStoreAliasPwd = ""


}