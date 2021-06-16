# Android 图片选择库

  [![](https://jitpack.io/v/quickmob/QuickPictureSelector.svg)](https://jitpack.io/#quickmob/QuickPictureSelector)

#### 集成步骤

* 在项目根目录下的 `build.gradle` 文件中加入

```
buildscript {
    ......
}
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

* 在项目 app 模块下的 `build.gradle` 文件中加入

```
android {
    compileOptions {
        targetCompatibility JavaVersion.VERSION_1_8
        sourceCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    //框架依赖：其中latest.release指代最新版本号，也可以指定明确的版本号
    implementation 'com.github.quickmob:QuickPictureSelector:latest.release'

    implementation 'com.google.android.material:material:1.1.0'
    implementation 'androidx.appcompat:appcompat:1.1.0'
    //photoView
    implementation 'com.github.chrisbanes:PhotoView:2.3.0'
    //LongImgView
    implementation 'com.davemorrissey.labs:subsampling-scale-image-view-androidx:3.10.0'
    //eventbus
    implementation 'org.greenrobot:eventbus:3.2.0'
    //utilcode
    implementation('com.blankj:utilcodex:1.30.6') {
        exclude group: 'com.google.code.gson'
    }
    //immersionbar
    implementation 'com.gyf.immersionbar:immersionbar:3.0.0'
    //glide
    implementation 'com.github.bumptech.glide:glide:4.12.0'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.12.0'
}
```

#### 使用方式
  具体看demo

#### 混淆配置
  此框架库自带混淆规则，并且会自动导入，正常情况下无需手动导入
