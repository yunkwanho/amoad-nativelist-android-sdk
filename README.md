<div align="center">
<img width="172" height="61" src="http://www.amoad.com/images/logo.png">
</div>

# AMoAd NativeList Demo for Android

<img width="160" height="284" src="docs/res/ScreenShot01.png">
<img width="320" src="docs/res/ScreenShot03.png">

## Introduction

ネイティブリスト広告のスタイルを管理画面よりHTML+CSSでデザインできます（HTMLコードの画面キャプチャは例です。実際にはアプリのデザインに合わせたコードを書きます）。

## Requirements

Android 2.3 or later

## Installing

[ZIPをダウンロード](https://github.com/amoad/amoad-nativelist-android-sdk/archive/master.zip)

## Usage

管理画面から取得したsidをMainActivity.javaのSIDに設定する。tagは複数の広告を区別するための任意文字列です。

```java
// TODO [SDK] 管理画面から取得したsidを入力してください
private static final String SID1 = "62056d310111552c000000000000000000000000000000000000000000000000";
private static final String TAG1 = "Ad01";
```

## API

[AMoAd NativeList API](https://cdn.rawgit.com/amoad/amoad-nativelist-android-sdk/master/docs/javadoc/index.html)

## Project Settings

1. AndroidStudioで「../AMoAdNativeListDemo/」をFile>Openする。

2. AndroidプロジェックトでGooglePlayerServiceを設定する。
   ([GooglePlayerService設定方法](https://developers.google.com/android/guides/setup#add_google_play_services_to_your_project))

<img width="640" src="docs/res/AndroidStudio.png">