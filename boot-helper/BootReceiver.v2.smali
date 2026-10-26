.class public Lnews/androidtv/launchonboot/BootReceiver;
.super Landroid/content/BroadcastReceiver;
.source "BootReceiver.java"


# static fields
.field private static final DEBUG:Z = true

.field private static final TAG:Ljava/lang/String;


# instance fields
.field private mContext:Landroid/content/Context;

.field private mScreenOnListener:Z


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .prologue
    .line 19
    const-class v0, Lnews/androidtv/launchonboot/BootReceiver;

    invoke-virtual {v0}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    move-result-object v0

    sput-object v0, Lnews/androidtv/launchonboot/BootReceiver;->TAG:Ljava/lang/String;

    return-void
.end method

.method public constructor <init>()V
    .locals 1

    .prologue
    .line 18
    invoke-direct {p0}, Landroid/content/BroadcastReceiver;-><init>()V

    .line 22
    const/4 v0, 0x0

    iput-boolean v0, p0, Lnews/androidtv/launchonboot/BootReceiver;->mScreenOnListener:Z

    return-void
.end method

.method public static processEvent(Landroid/content/Context;Landroid/content/Intent;)V
    .locals 8
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "intent"    # Landroid/content/Intent;

    invoke-static {p0, p1}, Lnews/androidtv/launchonboot/BootGate;->allow(Landroid/content/Context;Landroid/content/Intent;)Z
    move-result v0
    if-nez v0, :gate_accepted
    return-void
    :gate_accepted
    .prologue
    const v7, 0x7f090058

    const/high16 v6, 0x10000000

    const/4 v5, 0x0

    .line 37
    sget-object v3, Lnews/androidtv/launchonboot/BootReceiver;->TAG:Ljava/lang/String;

    const-string v4, "Received intent"

    invoke-static {v3, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 38
    sget-object v3, Lnews/androidtv/launchonboot/BootReceiver;->TAG:Ljava/lang/String;

    invoke-virtual {p1}, Landroid/content/Intent;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-static {v3, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 45
    new-instance v2, Lcom/felkertech/settingsmanager/SettingsManager;

    invoke-direct {v2, p0}, Lcom/felkertech/settingsmanager/SettingsManager;-><init>(Landroid/content/Context;)V

    .line 46
    .local v2, "settingsManager":Lcom/felkertech/settingsmanager/SettingsManager;
    const-string v3, "BOOT_APP_ENABLED"

    invoke-virtual {v2, v3}, Lcom/felkertech/settingsmanager/SettingsManager;->getBoolean(Ljava/lang/String;)Z

    move-result v3

    if-nez v3, :cond_1

    .line 91
    :cond_0
    :goto_0
    return-void

    .line 49
    :cond_1
    invoke-virtual {p1}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object v3

    if-eqz v3, :cond_2

    .line 50
    invoke-virtual {p1}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object v3

    const-string v4, "android.intent.action.USER_PRESENT"

    invoke-virtual {v3, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_2

    const-string v3, "ON_WAKEUP"

    .line 51
    invoke-virtual {v2, v3}, Lcom/felkertech/settingsmanager/SettingsManager;->getBoolean(Ljava/lang/String;)Z

    move-result v3

    if-eqz v3, :cond_0

    .line 54
    :cond_2
    invoke-virtual {p1}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object v3

    if-eqz v3, :cond_3

    .line 55
    invoke-virtual {p1}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object v3

    const-string v4, "android.intent.action.SCREEN_ON"

    invoke-virtual {v3, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_3

    const-string v3, "ON_WAKEUP"

    .line 56
    invoke-virtual {v2, v3}, Lcom/felkertech/settingsmanager/SettingsManager;->getBoolean(Ljava/lang/String;)Z

    move-result v3

    if-eqz v3, :cond_0

    .line 59
    :cond_3
    invoke-virtual {p1}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object v3

    if-eqz v3, :cond_4

    .line 60
    invoke-virtual {p1}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object v3

    const-string v4, "android.intent.action.DREAMING_STOPPED"

    invoke-virtual {v3, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v3

    if-eqz v3, :cond_4

    const-string v3, "ON_WAKEUP"

    .line 61
    invoke-virtual {v2, v3}, Lcom/felkertech/settingsmanager/SettingsManager;->getBoolean(Ljava/lang/String;)Z

    move-result v3

    if-eqz v3, :cond_0

    .line 64
    :cond_4
    const-string v3, "LAUNCH_LIVE_CHANNELS"

    invoke-virtual {v2, v3}, Lcom/felkertech/settingsmanager/SettingsManager;->getBoolean(Ljava/lang/String;)Z

    move-result v3

    if-eqz v3, :cond_5

    .line 65
    invoke-virtual {p0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v3

    const v4, 0x7f0d0002

    invoke-virtual {v3, v4}, Landroid/content/res/Resources;->getBoolean(I)Z

    move-result v3

    if-eqz v3, :cond_5

    sget v3, Landroid/os/Build$VERSION;->SDK_INT:I

    const/16 v4, 0x15

    if-lt v3, v4, :cond_5

    .line 67
    new-instance v1, Landroid/content/Intent;

    const-string v3, "android.intent.action.VIEW"

    sget-object v4, Landroid/media/tv/TvContract$Channels;->CONTENT_URI:Landroid/net/Uri;

    invoke-direct {v1, v3, v4}, Landroid/content/Intent;-><init>(Ljava/lang/String;Landroid/net/Uri;)V

    .line 68
    .local v1, "i":Landroid/content/Intent;
    invoke-virtual {v1, v6}, Landroid/content/Intent;->addFlags(I)Landroid/content/Intent;

    .line 69
    invoke-virtual {p0, v1}, Landroid/content/Context;->startActivity(Landroid/content/Intent;)V

    goto :goto_0

    .line 70
    .end local v1    # "i":Landroid/content/Intent;
    :cond_5
    const-string v3, "LAUNCH_ACTIVITY"

    invoke-virtual {v2, v3}, Lcom/felkertech/settingsmanager/SettingsManager;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/String;->isEmpty()Z

    move-result v3

    if-nez v3, :cond_0

    .line 72
    invoke-virtual {p0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v3

    const v4, 0x7f0d0001

    invoke-virtual {v3, v4}, Landroid/content/res/Resources;->getBoolean(I)Z

    move-result v3

    if-eqz v3, :cond_6

    .line 73
    invoke-virtual {p0}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v3

    const-string v4, "LAUNCH_ACTIVITY"

    .line 74
    invoke-virtual {v2, v4}, Lcom/felkertech/settingsmanager/SettingsManager;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v4

    .line 73
    invoke-virtual {v3, v4}, Landroid/content/pm/PackageManager;->getLeanbackLaunchIntentForPackage(Ljava/lang/String;)Landroid/content/Intent;

    move-result-object v1

    .line 80
    .restart local v1    # "i":Landroid/content/Intent;
    :goto_1
    if-nez v1, :cond_7

    .line 81
    invoke-static {p0, v7, v5}, Landroid/widget/Toast;->makeText(Landroid/content/Context;II)Landroid/widget/Toast;

    move-result-object v3

    invoke-virtual {v3}, Landroid/widget/Toast;->show()V

    goto/16 :goto_0

    .line 76
    .end local v1    # "i":Landroid/content/Intent;
    :cond_6
    invoke-virtual {p0}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v3

    const-string v4, "LAUNCH_ACTIVITY"

    .line 77
    invoke-virtual {v2, v4}, Lcom/felkertech/settingsmanager/SettingsManager;->getString(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v4

    .line 76
    invoke-virtual {v3, v4}, Landroid/content/pm/PackageManager;->getLaunchIntentForPackage(Ljava/lang/String;)Landroid/content/Intent;

    move-result-object v1

    .restart local v1    # "i":Landroid/content/Intent;
    goto :goto_1

    .line 84
    :cond_7
    invoke-virtual {v1, v6}, Landroid/content/Intent;->addFlags(I)Landroid/content/Intent;

    .line 86
    :try_start_0
    invoke-virtual {p0, v1}, Landroid/content/Context;->startActivity(Landroid/content/Intent;)V
    :try_end_0
    .catch Landroid/content/ActivityNotFoundException; {:try_start_0 .. :try_end_0} :catch_0

    goto/16 :goto_0

    .line 87
    :catch_0
    move-exception v0

    .line 88
    .local v0, "e":Landroid/content/ActivityNotFoundException;
    invoke-static {p0, v7, v5}, Landroid/widget/Toast;->makeText(Landroid/content/Context;II)Landroid/widget/Toast;

    move-result-object v3

    invoke-virtual {v3}, Landroid/widget/Toast;->show()V

    goto/16 :goto_0
.end method

.method private startForegroundService()V
    .locals 3

    .prologue
    .line 94
    new-instance v0, Landroid/content/Intent;

    iget-object v1, p0, Lnews/androidtv/launchonboot/BootReceiver;->mContext:Landroid/content/Context;

    const-class v2, Lnews/androidtv/launchonboot/DreamListenerService;

    invoke-direct {v0, v1, v2}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    .line 95
    .local v0, "i":Landroid/content/Intent;
    iget-object v1, p0, Lnews/androidtv/launchonboot/BootReceiver;->mContext:Landroid/content/Context;

    invoke-virtual {v1, v0}, Landroid/content/Context;->startService(Landroid/content/Intent;)Landroid/content/ComponentName;

    .line 96
    return-void
.end method


# virtual methods
.method public onReceive(Landroid/content/Context;Landroid/content/Intent;)V
    .locals 2
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "intent"    # Landroid/content/Intent;

    .prologue
    .line 27
    iput-object p1, p0, Lnews/androidtv/launchonboot/BootReceiver;->mContext:Landroid/content/Context;

    .line 28
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object v0

    if-eqz v0, :cond_0

    .line 29
    invoke-virtual {p2}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    move-result-object v0

    const-string v1, "android.intent.action.BOOT_COMPLETED"

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_0

    .line 30
    invoke-direct {p0}, Lnews/androidtv/launchonboot/BootReceiver;->startForegroundService()V

    .line 32
    :cond_0
    invoke-static {p1, p2}, Lnews/androidtv/launchonboot/BootReceiver;->processEvent(Landroid/content/Context;Landroid/content/Intent;)V

    .line 33
    return-void
.end method
