<div align="center">
<img width="172" height="61" src="http://www.amoad.com/images/logo.png">
</div>

# AMoAd InfeedAd Demo for Android ver4.10.0

<img height="300" src="docs/res/ScreenShot01.png">
<img height="300" src="docs/res/ScreenShot02.png">

## Introduction

InfeedAd広告は表示位置などを管理画面から設定ができます。
広告レイアウトのデザインができます。

## Requirements

Android 2.3 or later

## Installing

[ZIPをダウンロード](https://github.com/amoad/amoad-nativelist-android-sdk/archive/master.zip)

## Usage

管理画面から取得したsidをMainActivity.javaのSIDに設定する。

```java
// TODO [SDK] 管理画面から取得したsidを入力してください
private static final String SID1 = "62056d310111552c000000000000000000000000000000000000000000000000";
private static final String SID2 = "62056d310111552c000000000000000000000000000000000000000000000000";
```

## API

[AMoAd InfeedAd API](https://cdn.rawgit.com/amoad/amoad-nativelist-android-sdk/master/docs/javadoc/index.html)

## Project Settings

1. AndroidStudioで「../AMoAdInfeedAdDemo/」をFile>Openする。

2. AndroidプロジェックトでGooglePlayerServiceを設定する。
   ([GooglePlayerService設定方法](https://developers.google.com/android/guides/setup#add_google_play_services_to_your_project))

<img width="640" src="docs/res/ScreenShot03.png">
