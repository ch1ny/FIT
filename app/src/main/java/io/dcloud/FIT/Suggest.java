package io.dcloud.FIT;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.example.detectiondemo.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class Suggest {

    private Activity activity;
    private ObjectDetection od;
    private String imgStr;
    private Bitmap bitmap;

    public Suggest(Activity activity) throws IOException {
        this.activity = activity;
        imgStr = "";
    }

    public void loadModel() {
        od = new ObjectDetection();
        boolean loadModel = od.load_model(activity);
        if (loadModel) {
            System.out.println("模型加载成功");
        } else {
            System.out.println("模型加载失败");
        }
    }

    public void inputData(Bitmap bitmap) {
//        imgStr += base64;
////        System.out.println("这次加上的是：" + base64);
//        System.out.println("base64字符串长为：" + imgStr.length());
//        System.out.println("字符串长为：" + imgStr.length());
//        bitmap = base64ToBitmap(imgStr);
        System.out.println("输入数据");
        System.out.println("传进来的Bitmap的宽是" + bitmap.getWidth());
    }

    public String Run() throws IOException {
        AssetManager asset = activity.getAssets();
        InputStream img = asset.open("apps/H5B405ED9/www/temp.jpg"); // 前端生成的临时文件，用于移动端AI读取并分析
//        InputStream img = asset.open("Temp/1.jpg");
        bitmap = BitmapFactory.decodeStream(img);
//        System.out.println("base64的尾码是：" + imgStr.substring(imgStr.lastIndexOf("/")));
//        Bitmap bitmap = base64ToBitmap(imgStr);
        ByteBuffer predict = od.predict_image(bitmap);
        Charset charset = null;
        CharsetDecoder decoder = null;
        CharBuffer charBuffer = null;
        try {
            charset = Charset.forName("UTF-8");
            decoder = charset.newDecoder();
            //用这个的话，只能输出来一次结果，第二次显示为空
            // charBuffer = decoder.decode(buffer);
            charBuffer = decoder.decode(predict.asReadOnlyBuffer());
            String result = charBuffer.toString();
            System.out.println("预测结果是：" + result);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public void bitmapTest(Bitmap bitmap) {
        System.out.println("Bitmap测试开始");
        System.out.println("Bitmap的宽为：" + bitmap.getWidth());
    }

    private Bitmap base64ToBitmap(String base64) {
        byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public String getSuggest(String style, String scene) throws IOException {
        Suggestion suggest = new Suggestion(activity);
        String res = suggest.getSuggs(style,scene, bitmap);
        System.out.println("给出的评价是：" + res);
        return res;
    }

}
