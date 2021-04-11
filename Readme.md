# Readme

## 移动端开发方式

html5plus与原生安卓混合开发。

## 源码层次

### src/main/java

原生安卓代码，主要用于对本地资源的读取，以及对部署于移动端的Yolov5和AIBoost的调用。

### src/main/assets

资源目录

#### detect.tflite

Yolov5目标检测模型

#### labelmap.txt

模型所需要的标签

#### apps/H5B405ED9/www/

主要web代码