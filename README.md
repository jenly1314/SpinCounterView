# SpinCounterView
[![Download](https://img.shields.io/badge/download-App-blue.svg)](https://raw.githubusercontent.com/jenly1314/SpinCounterView/master/app/app-release.apk)
[![](https://jitpack.io/v/jenly1314/SpinCounterView.svg)](https://jitpack.io/#jenly1314/SpinCounterView)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/mit-license.php)
[![Blog](https://img.shields.io/badge/blog-Jenly-9933CC.svg)](http://blog.csdn.net/jenly121)

SpinCounterView for Android 一个类似码表变化的旋转计数器动画控件（仿2345贷款App金额进度变动）。

## Gif 展示
![Image](GIF.gif)

## 引入

### Maven：
```
<dependency>
  <groupId>com.king.view</groupId>
  <artifactId>SpinCounterView</artifactId>
  <version>1.0</version>
  <type>pom</type>
</dependency>
```
### Gradle:
```
compile 'com.king.view:SpinCounterView:1.0'
```
### Lvy:
```
<dependency org='com.king.view' name='SpinCounterView' rev='1.0'>
  <artifact name='$AID' ext='pom'></artifact>
</dependency>
```

## 示例

布局
```Xml
    <com.king.view.SpinCounterView
        android:id="@+id/scv"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:max="100"
        app:maxValue="1000"/>
```

核心动画代码
```Java
spinCounterView.showAnimation(80);
```

## 关于我
   Name: Jenly

   Email: jenly1314@gmail.com / jenly1314@vip.qq.com

   CSDN: http://www.csdn.net/jenly121

   Github: https://github.com/jenly1314

   微信公众号:

   ![公众号](http://olambmg9j.bkt.clouddn.com/jenly666.jpg)

