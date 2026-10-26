# 微鲸开机助手兼容补丁

基于 [ITVlab/Launch-On-Boot 1.1.3](https://github.com/ITVlab/Launch-On-Boot)（MIT）。此目录保存修改的 Smali、入口清单和上游接收器参考源码，便于审阅和重建兼容补丁；它不是独立的 Gradle 项目。上游 F-Droid 基础包为 `news.androidtv.launchonboot_12.apk`。

改动：独立包名 `news.androidtv.launchonboot.whaley`，增加微鲸设备就绪及原装主页可见广播，并按 Linux boot_id 限制每次开机只触发一次。没有修改系统默认 HOME，没有增加常驻启动服务。

重建需要从上游基础包反编译、应用这些对应文件、重新打包并使用自己的密钥签名。不要使用不同签名覆盖他人的已安装版本。启用后在助手界面选择要启动的桌面。实际可用性取决于厂商广播和系统限制，必须通过真实开机验证。

本目录按 MIT 许可证发布；不附带签名密钥、认证材料、设备配置或预构建包。
