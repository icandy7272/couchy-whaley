.class public final Lnews/androidtv/launchonboot/BootGate;
.super Ljava/lang/Object;

.method public static declared-synchronized allow(Landroid/content/Context;Landroid/content/Intent;)Z
    .locals 7
    const-class v6, Lnews/androidtv/launchonboot/BootGate;
    monitor-enter v6
    :try_start
    invoke-virtual {p1}, Landroid/content/Intent;->getAction()Ljava/lang/String;
    move-result-object v0
    const-string v1, "com.helios.launcher.action.IS_VISIBILITY"
    invoke-virtual {v1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
    move-result v1
    if-eqz v1, :vendor
    const-string v1, "state"
    const/4 v2, -0x1
    invoke-virtual {p1, v1, v2}, Landroid/content/Intent;->getIntExtra(Ljava/lang/String;I)I
    move-result v1
    if-nez v1, :deny
    goto :check
    :vendor
    const-string v1, "whaley.intent.action.DEVID_SERVICE_READY"
    invoke-virtual {v1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
    move-result v1
    if-eqz v1, :allow
    :check
    new-instance v0, Ljava/io/BufferedReader;
    new-instance v1, Ljava/io/FileReader;
    const-string v2, "/proc/sys/kernel/random/boot_id"
    invoke-direct {v1, v2}, Ljava/io/FileReader;-><init>(Ljava/lang/String;)V
    invoke-direct {v0, v1}, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V
    invoke-virtual {v0}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;
    move-result-object v1
    invoke-virtual {v0}, Ljava/io/BufferedReader;->close()V
    if-eqz v1, :deny
    const-string v0, "whaley_boot_gate"
    const/4 v2, 0x0
    invoke-virtual {p0, v0, v2}, Landroid/content/Context;->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    move-result-object v0
    const-string v2, "last_boot_id"
    const-string v3, ""
    invoke-interface {v0, v2, v3}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    move-result-object v3
    invoke-virtual {v1, v3}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
    move-result v3
    if-nez v3, :deny
    invoke-interface {v0}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;
    move-result-object v0
    invoke-interface {v0, v2, v1}, Landroid/content/SharedPreferences$Editor;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;
    move-result-object v0
    invoke-interface {v0}, Landroid/content/SharedPreferences$Editor;->commit()Z
    move-result v0
    if-eqz v0, :deny
    const-string v0, "CouchyBootGate"
    const-string v1, "First launcher-ready event for this boot: launching Couchy"
    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I
    :allow
    const/4 v0, 0x1
    monitor-exit v6
    return v0
    :deny
    const/4 v0, 0x0
    monitor-exit v6
    return v0
    :try_end
    .catch Ljava/lang/Exception; {:try_start .. :try_end} :failed
    .catchall {:try_start .. :try_end} :all
    :failed
    move-exception v0
    const-string v1, "CouchyBootGate"
    const-string v2, "Cannot verify boot identity; ignoring event"
    invoke-static {v1, v2, v0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    const/4 v0, 0x0
    monitor-exit v6
    return v0
    :all
    move-exception v0
    monitor-exit v6
    throw v0
.end method
