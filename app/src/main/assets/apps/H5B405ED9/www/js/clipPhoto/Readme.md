# Readme
一款手势驱动的裁图插件，**移动端照片裁剪的简洁化解决方案！**

- 在移动设备上双指捏合为缩放，~~双指转动为旋转~~，**已改为通过页面按钮实现旋转**
- 在PC设备上鼠标滚轮为缩放，每次双击则顺时针旋转90度



## 依赖插件

[**iscroll-zoom.js**](https://github.com/cubiq/iscroll)   
[**hammer.js**](https://github.com/hammerjs/hammer.js)  
[**lrz.all.bundle.js**](https://github.com/think2011/localResizeIMG) 



## 使用方法

```html
<div id="clipArea"></div>
<input type="file" id="file" />
...
<script src="js/iscroll-zoom.js"></script>
<script src="js/hammer.min.js"></script>
<script src="js/lrz.all.bundle.js"></script>
<script src="js/PhotoClip.js"></script>
<script>
var pc = new PhotoClip('#clipArea');
file.addEventListener('change', function() {
    pc.load(this.files[0]);
});
</script>
```



## PhotoClip 构造函数

new PhotoClip( **container** [, **options**] )

构造函数有两个主要参数：

### container

表示图片裁剪容器的选择器或者DOM对象。

### options

配置选项，详细配置如下：

- **options.size**

  type: Number|Array

  截取框大小。  
  当值为数字时，表示截取框为宽高都等于该值的正方形。  
  当值为数组时，数组中索引`[0]`和`[1]`所对应的值分别表示宽和高。  
  默认值为 `[100,100]`。

- **options.adaptive**

  type: String|Array

  截取框自适应。设置该选项后，`size` 选项将会失效，此时 `size` 进用于计算截取框的宽高比例。  
  当值为一个百分数字符串时，表示截取框的宽度百分比。  
  当值为数组时，数组中索引 `[0]` 和 `[1]` 所对应的值分别表示宽和高的百分比。  
  当宽或高有一项值未设置或值无效时，则该项会根据 `size` 选项中定义的宽高比例自适应。  
  默认为 ` ''`。

- **options.outputSize**

  type: Number|Array

  输出图像大小。  
  当值为数字时，表示输出宽度，此时高度根据截取框比例自适应。  
  当值为数组时，数组中索引 `[0]` 和 `[1]` 所对应的值分别表示宽和高，若宽或高有一项值无效，则会根据另一项等比自适应。  
  默认值为`[0,0]`，表示输出图像原始大小。

- **options.outputType**

  type: String

  指定输出图片的类型，可选 'jpg' 和 'png' 两种种类型，默认为 'jpg'。


- **options.outputQuality**

  type: Number

  图片输出质量，仅对 jpeg 格式的图片有效，取值 0 - 1，默认为0.8。（这个质量并不是图片的最终质量，而是在经过 lrz 插件压缩后的基础上输出的质量。相当于 `outputQuality` * `lrzOption.quality`）  

- **options.maxZoom**

  type: Number

  图片的最大缩放比，默认为 1。

- **options.rotateFree**

  type: Boolean

  是否启用图片自由旋转。由于安卓浏览器上存在性能问题，因此在安卓设备上默认关闭。


- **options.view**

  type: String|HTMLElement

  显示截取后图像的容器的选择器或者DOM对象。如果有多个，可使用英文逗号隔开的选择器字符串，或者DOM对象数组。


- **options.file**

  type: String|HTMLElement

  上传图片的 `<input type="file">` 控件的选择器或者DOM对象。如果有多个，可使用英文逗号隔开的选择器字符串，或者DOM对象数组。


- **options.ok**

  type: String|HTMLElement

  确认截图按钮的选择器或者DOM对象。如果有多个，可使用英文逗号隔开的选择器字符串，或者DOM对象数组。


- **options.img**

  type: String

  需要裁剪图片的url地址。该参数表示当前立即开始读取图片，不需要使用 file 控件获取。注意，加载的图片必须要与本程序同源，如果图片跨域，则无法截图。


- **options.loadStart**

  type: Function

  图片开始加载的回调函数。`this` 指向当前 `PhotoClip` 的实例对象，并将正在加载的 file 对象作为参数传入。（如果是使用非 file 的方式加载图片，则该参数为图片的 url）

- **options.loadComplete**

  type: Function

  图片加载完成的回调函数。`this` 指向当前 `PhotoClip` 的实例对象，并将图片的 \<img\> 对象作为参数传入。


- **options.loadError**

  type: Function

  图片加载失败的回调函数。`this` 指向当前 `PhotoClip` 的实例对象，并将错误信息作为第一个参数传入，如果还有其它错误对象或者信息会作为第二个参数传入。


- **options.done**

  type: Function

  裁剪完成的回调函数。`this` 指向当前 `PhotoClip` 的实例对象，会将裁剪出的图像数据DataURL作为参数传入。


- **options.fail**

  type: Function

  裁剪失败的回调函数。`this` 指向当前 `PhotoClip` 的实例对象，会将失败信息作为参数传入。

- **options.lrzOption**

  type: Object

  lrz 压缩插件的配置参数。该压缩插件作用于图片从相册输出到移动设备浏览器过程中的压缩，配置的高低将直接关系到图片在移动设备上操作的流畅度。以下为子属性：

  - **options.lrzOption.width**：

    type: Number

    图片最大不超过的宽度，默认为原图宽度，高度不设时会适应宽度。（由于安卓浏览器存在性能问题，所以默认值为 1000）

  - **options.lrzOption.height**

    type: Number

    图片最大不超过的高度，默认为原图高度，宽度不设时会适应高度。（由于安卓浏览器存在性能问题，所以默认值为 1000）

  - **options.lrzOption.quality**

    type: Number

    图片压缩质量，仅对 jpeg 格式的图片有效，取值 0 - 1，默认为0.7。（这个质量不是最终输出的质量，与 `options.outputQuality` 是相乘关系）

- **options.style**

  type: Object

  样式配置。以下为子属性：

  - **options.style.maskColor**

    type: String

    遮罩层的颜色。默认为 `'rgba(0,0,0,.5)'`。

  - **options.style.maskBorder**

    type: String

    遮罩层的 border 样式。默认为 `'2px dashed #ddd'`。

  - **options.style.jpgFillColor**

    type: String

    当输出 jpg 格式时透明区域的填充色。默认为 `'#fff'`。

- **options.errorMsg**

  type: Object

  错误信息对象，包含各个阶段出错时的文字说明。以下为子属性：

  - **options.errorMsg.noSupport**

    type: String

    浏览器无法支持本插件。将会使用 `alert` 弹出该信息，若不希望弹出，可将该值置空。

  - **options.errorMsg.imgError**

    type: String

    使用 file 控件读取图片格式错误时的错误信息。将会在 `loadError` 回调的错误信息中输出。

  - **options.errorMsg.imgHandleError**

    type: String

    lrz 压缩插件处理图片失败时的错误信息。将会在 `loadError` 回调的错误信息中输出。

  - **options.errorMsg.imgLoadError**

    type: String

    图片加载出错时的错误信息。将会在 `loadError` 回调的错误信息中输出。

  - **options.errorMsg.noImg**

    type: String

    没有加载完成的图片时，执行截图操作时的错误信息。将会在 `fail` 回调的失败信息中输出。

  - **options.errorMsg.clipError**

    type: String

    截图出错时的错误信息。将会在 `fail` 回调的失败信息中输出。



## PhotoClip 对象实例方法

```js
/**
 * 设置截取框的宽高
 * 如果设置了 adaptive 选项，则该方法仅用于修改截取框的宽高比例
 * @param  {Number} width  截取框的宽度
 * @param  {Number} height 截取框的高度
 * @return {PhotoClip}     返回 PhotoClip 的实例对象
 */
pc.size(width, height);

/**
 * 加载一张图片
 * @param  {String|Object} src 图片的 url，或者图片的 file 文件对象
 * @return {PhotoClip}         返回 PhotoClip 的实例对象
 */
pc.load(src);

/**
 * 清除当前图片
 * @return {PhotoClip}  返回 PhotoClip 的实例对象
 */
pc.clear();

/**
 * 图片旋转到指定角度
 * @param  {Number} angle      可选。旋转的角度
 * @param  {Number} duration   可选。旋转动画的时长，如果为 0 或 false，则表示没有过渡动画
 * @return {PhotoClip|Number}  返回 PhotoClip 的实例对象。如果参数为空，则返回当前的旋转角度
 */
pc.rotate(angle, duration);

/**
 * 图片缩放到指定比例，如果超出缩放范围，则会被缩放到可缩放极限
 * @param  {Number} zoom       可选。缩放比例，取值在 0 - 1 之间
 * @param  {Number} duration   可选。缩放动画的时长，如果为 0 或 false，则表示没有过渡动画
 * @return {PhotoClip|Number}  返回 PhotoClip 的实例对象。如果参数为空，则返回当前的缩放比例
 */
pc.scale(zoom, duration);

/**
 * 截图
 * @return {String}  返回截取后图片的 Base64 字符串
 */
pc.clip();

/**
 * 销毁
 * @return {Undefined}  无返回值
 */
pc.destroy();
```


