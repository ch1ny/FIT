package com.example.detectiondemo;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import org.tensorflow.lite.Interpreter;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class ObjectDetection {


    private int[] ddims = {1, 3, 320, 320};//根据自己的实际情况修改

    private Interpreter tflite;
    private List<String> labelList;

    private static final int NUM_DETECTIONS = 6300; //只展示前10个检测出来的////////////////////////////////////10
//    private float[][][] outputLocations = new float[1][NUM_DETECTIONS][23];//默认保留的前10个检测到的物体的坐标;////////////////////4
//    private float[][] outputClasses = new float[1][NUM_DETECTIONS];//10个目标属于的类别;
//    private float[][] outputScores = new float[1][NUM_DETECTIONS];//10个目标的概率值;
//    private float[] numDetections = new float[1];//实际检测出来的物体的个数
//    //目标检测模型的输出是多节点输出  根据tflite的runForMultipleInputsOutputs函数  声明并初始化 outputMap  用来接收输出数据
//    private Map<Integer, Object> outputMap = new HashMap(){
//        {
//            put(0,outputLocations);
//            put(1,outputClasses);
//            put(2,outputScores);
//            put(3,numDetections);
//        }
//    };


    private Recognition[] recognitions = null;

    private static final float IMAGE_MEAN = 0;//均值
    private static final float IMAGE_STD = 255.0f;//标准化
    private static final boolean isModelQuantized=false;
    private  int output_box=6300;//先验框的个数
    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.3f;
    private static final int[] OUTPUT_WIDTH = new int[]{40, 20, 10};
    int[][] masks = new int[][]{{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};

    private Map<Integer, Object> outputMap = new HashMap<>();
    private ByteBuffer outData;
    private ByteBuffer imgData = ByteBuffer.allocateDirect(ddims[0] * ddims[1] * ddims[2] * ddims[3]* 4);

    public ByteBuffer getImgData() {
        return imgData;
    }



//-------------------------------------------加载目标检测模型-----------------------------------------
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("detect.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
    public boolean load_model(Activity activity){
        try{
            Interpreter.Options tfliteOptions = new Interpreter.Options();
            tfliteOptions.setNumThreads(4);
            tflite = new Interpreter(loadModelFile(activity), tfliteOptions);
            labelList = loadLabelList(activity);//加载标签  可以单独作为一个方法去调用
            return true;
        }
        catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }
    //加载标签
    private  List<String> loadLabelList(Activity activity) throws IOException {
        List <String> labelList = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(activity.getAssets().open("labelmap.txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }
//-------------------------------------------加载目标检测模型-----------------------------------------

//-----------------------------------------输入数据预处理--------------------------------------------
    //与图像分类的预处理工作高度相似  把一张图片的三个通道每个通道的像素点数据存入ByteBuffer类型的 imgData
    public ByteBuffer getInputData(Bitmap bitmap) {
        imgData.order(ByteOrder.nativeOrder());
        int[] intValues = new int[ddims[2] * ddims[3]];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        imgData.rewind();
        for (int i=0; i<ddims[2];++i){
            for (int j = 0; j < ddims[3]; ++j){
                 int val = intValues[i * 320+ j];

                imgData.putFloat((((val >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                imgData.putFloat((((val >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                imgData.putFloat(((val & 0xFF) - IMAGE_MEAN) / IMAGE_STD);

//                imgData.putFloat(((val >> 16) & 0xFF)  * 2.0f / 255.0f - 1.0f );
//                imgData.putFloat(((val >> 8) & 0xFF) * 2.0f / 255.0f - 1.0f );
//                imgData.putFloat((val & 0xFF) * 2.0f / 255.0f - 1.0f );
            }
        }
        int[] shape = tflite.getOutputTensor(0).shape();
        int numClass = shape[shape.length - 1];
        outData = ByteBuffer.allocateDirect(output_box * (numClass + 5) * 4);
        outData.order(ByteOrder.nativeOrder());
        outData.rewind();
        outputMap.put(0, outData);
        return imgData;

    }
//-------------------------------------------输入数据预处理----------------------------------------

//-----------------------------------推理预测函数-------------------------------------------------------------
    public ByteBuffer predict_image(Bitmap oribitmap){
        Bitmap resizedbitmap = Bitmap.createScaledBitmap(oribitmap, ddims[2], ddims[3], false);//resize
        ByteBuffer imgData = getInputData(resizedbitmap);//输入数据预处理
        Object[] inputArray = {imgData};//runForMultipleInputsOutput函数的输入是一个Object类型的数组
        tflite.runForMultipleInputsOutputs(inputArray, outputMap);//运行模型
        recognize(resizedbitmap);//后处理
        ByteBuffer buffer = ByteBuffer.wrap(getClasses().getBytes());
        return buffer;
    }
//-----------------------------------推理预测函数-------------------------------------------------------------
//-------------------------------------------模型释放与关闭------------------------------------------------------
public void close(){

    tflite.close();

}
//-------------------------------------------模型释放与关闭------------------------------------------------------
//-----------------------------------输出数据后处理------------------------------------------------------------
    //此处后处理的目的是得到一张用有颜色的框框出检测到的物体的图片
    private Bitmap drawRect(Bitmap resizedbitmap,Bitmap oribitmap){
        Bitmap boxImage = null;
        int oriImageH = oribitmap.getHeight();//原始图片的高度
        int oriImageW = oribitmap.getWidth();//原始图片的宽度
        Bitmap mutableBitmap = oribitmap.copy(Bitmap.Config.ARGB_8888, true);//复制原始图片
        Canvas canvas = new Canvas(mutableBitmap);//新建画布对象
        Paint paint = new Paint();//新建画笔对象
        paint.setTextSize((int)(200 ));//字体大小
        paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);//阴影参数
        paint.setColor(Color.RED);paint.setStyle(Paint.Style.STROKE);//颜色设置
        //不填充
        paint.setStrokeWidth(10);  //空心线的宽度


        for(Recognition recognition:recognitions){
            if(recognition.getConfidence()>0.5){//当物体所属概率值大于0.4时 才做进一步处理
                float scaleH = (float)oriImageH / resizedbitmap.getHeight();//计算高度的缩放比例
                float scaleW = (float)oriImageW / resizedbitmap.getWidth(); //计算宽度的缩放比例
                int ori_left = (int) (scaleW * recognition.getLocation().left);//原始图片上对应的真实的left坐标
                int ori_top = (int) (scaleH * recognition.getLocation().top);//原始图片上对应的真实的top坐标
                int ori_right = (int) (scaleW * recognition.getLocation().right);//原始图片上对应的真实的right坐标
                int ori_bottom = (int) (scaleH * recognition.getLocation().bottom);//原始图片上对应的真实的bottom坐标


                Double id = Double.valueOf(recognition.getId());//被检测物体的类别ID
                int ID = (int)Math.ceil(id);
                String label = recognition.getTitle();//被检测物体的标签值
                System.out.println(label);

                Rect bounds = new Rect();//构造矩形框对象
                paint.getTextBounds(label, 0, label.length(), bounds);
                canvas.drawText(ID+":"+label, ori_left, ori_top-50, paint);
                canvas.drawRect(ori_left, ori_top, ori_right, ori_bottom, paint);
            }
        }
        boxImage = mutableBitmap;
        return boxImage; //返回写入文字和有颜色的检测矩形框的原始图片
    }
    private String getClasses() {
        String str = "";
//        String[] classes = new String[recognitions.length];
//        int i=0;
        for (Recognition recognition : recognitions) {
            if (recognition.getConfidence() > 0.5) {
//                Double id = Double.valueOf(recognition.getId());//被检测物体的类别ID
//                int ID = (int) Math.ceil(id);
                String title = recognition.getTitle();
//                classes[i++] = title;
                str += "/" + title;
            }
        }
        return str;
    }

    //对输出结果中检测到的物体作进一步的处理 将outputMap中的数据取出来 变换处理 存入recognitions数组中
    private void recognize(Bitmap bitmap){
//        float n = ((float []) outputMap.get(3) )[0];//检测出来的物体个数
//        //将所有的识别结果存入了Recognition类型的数组中
//        recognitions = new Recognition[(int) n];//数组的长度也就是检测出的物体的个数
//        for (int i =0; i<n;++i){
//            //RectF类  构造函数 left top right bottom  因此要将outputMap 中的坐标按照对应关系存入
//            //outputLocations 中每个检测框的坐标顺序是  0:top  1:left  2:bootm   3:right
//            RectF rectF = new RectF( ddims[2]*((float [][][]) outputMap.get(0))[0][i][1], ddims[3]*((float [][][]) outputMap.get(0) )[0][i][0], ddims[2]*((float [][][]) outputMap.get(0) )[0][i][3], ddims[3]*((float [][][]) outputMap.get(0) )[0][i][2]);
//            //Recognition 构造函数 四个参数分别 物体所属类别索引 类别标签 类别对应的概率  检测物体的左上角和右下角坐标值
//            recognitions[i] = new Recognition(""+ ((float [][]) outputMap.get(1))[0][i], labelList.get( (int) ((float [][]) outputMap.get(1) )[0][i]), ((float [][]) outputMap.get(2) )[0][i], rectF );
//        }
        ByteBuffer byteBuffer = (ByteBuffer) outputMap.get(0);
        byteBuffer.rewind();
        int numClass=labelList.size();

        ArrayList<Recognition> detections = new ArrayList<Recognition>();

        float[][][] out = new float[1][output_box][numClass + 5];
        Log.d("YoloV5Classifier", "out[0] detect start");
        for (int i = 0; i < output_box; ++i) {
            for (int j = 0; j < numClass + 5; ++j) {
                    out[0][i][j] = byteBuffer.getFloat();
            }
            // Denormalize xywh
            for (int j = 0; j < 4; ++j) {
                out[0][i][j] *= ddims[2];
            }
        }
        for (int i = 0; i < output_box; ++i){
            final int offset = 0;
            final float confidence = out[0][i][4];
            int detectedClass = -1;
            float maxClass = 0;

            final float[] classes = new float[labelList.size()];
            for (int c = 0; c < labelList.size(); ++c) {
                classes[c] = out[0][i][5 + c];
            }

            for (int c = 0; c < labelList.size(); ++c) {
                if (classes[c] > maxClass) {
                    detectedClass = c;
                    maxClass = classes[c];
                }
            }

            final float confidenceInClass = maxClass * confidence;
            if (confidenceInClass > getObjThresh()) {
                final float xPos = out[0][i][0];
                final float yPos = out[0][i][1];

                final float w = out[0][i][2];
                final float h = out[0][i][3];
                Log.d("YoloV5Classifier",
                        Float.toString(xPos) + ',' + yPos + ',' + w + ',' + h);

                final RectF rect =
                        new RectF(
                                Math.max(0, xPos - w / 2),
                                Math.max(0, yPos - h / 2),
                                Math.min(bitmap.getWidth() - 1, xPos + w / 2),
                                Math.min(bitmap.getHeight() - 1, yPos + h / 2));
                detections.add(new Recognition("" + offset, labelList.get(detectedClass),
                        confidenceInClass, rect, detectedClass));
            }
        }

           recognitions= nms(detections).toArray(new Recognition[0]);


    }
    protected ArrayList<Recognition> nms(ArrayList<Recognition> list) {
        ArrayList<Recognition> nmsList = new ArrayList<Recognition>();

        for (int k = 0; k < labelList.size(); k++) {
            //1.find max confidence per class
            PriorityQueue<Recognition> pq =
                    new PriorityQueue<Recognition>(
                            50,
                            new Comparator<Recognition>() {
                                @Override
                                public int compare(final Recognition lhs, final Recognition rhs) {
                                    // Intentionally reversed to put high confidence at the head of the queue.
                                    return Float.compare(rhs.getConfidence(), lhs.getConfidence());
                                }
                            });

            for (int i = 0; i < list.size(); ++i) {
                if (list.get(i).getDetectedClass() == k) {
                    pq.add(list.get(i));
                }
            }

            //2.do non maximum suppression
            while (pq.size() > 0) {
                //insert detection with max confidence
                Recognition[] a = new Recognition[pq.size()];
                Recognition[] detections = pq.toArray(a);
                Recognition max = detections[0];
                nmsList.add(max);
                pq.clear();

                for (int j = 1; j < detections.length; j++) {
                    Recognition detection = detections[j];
                    RectF b = detection.getLocation();
                    if (box_iou(max.getLocation(), b) < mNmsThresh) {
                        pq.add(detection);
                    }
                }
            }
        }
        return nmsList;
    }

    protected float mNmsThresh = 0.6f;
    ////////////////////////////////////////////////////////////////////////////////
    protected float box_iou(RectF a, RectF b) {
        return box_intersection(a, b) / box_union(a, b);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////
    protected float box_intersection(RectF a, RectF b) {
        float w = overlap((a.left + a.right) / 2, a.right - a.left,
                (b.left + b.right) / 2, b.right - b.left);
        float h = overlap((a.top + a.bottom) / 2, a.bottom - a.top,
                (b.top + b.bottom) / 2, b.bottom - b.top);
        if (w < 0 || h < 0) return 0;
        float area = w * h;
        return area;
    }
    //////////////////////////////////////////////////////////////
    protected float box_union(RectF a, RectF b) {
        float i = box_intersection(a, b);
        float u = (a.right - a.left) * (a.bottom - a.top) + (b.right - b.left) * (b.bottom - b.top) - i;
        return u;
    }
    ///////////////////////////////////////////////////////////////////////
    protected float overlap(float x1, float w1, float x2, float w2) {
        float l1 = x1 - w1 / 2;
        float l2 = x2 - w2 / 2;
        float left = l1 > l2 ? l1 : l2;
        float r1 = x1 + w1 / 2;
        float r2 = x2 + w2 / 2;
        float right = r1 < r2 ? r1 : r2;
        return right - left;
    }
    public float getObjThresh() {
        return MINIMUM_CONFIDENCE_TF_OD_API;
    }
    public Bitmap getMap(ByteBuffer buffer) {
        buffer.flip();
        //获取buffer中有效大小
        int len = buffer.limit() - buffer.position();

        byte[] bytes = new byte[len];

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = buffer.get();
        }

        return decodeBitmap(bytes);
    }
    private Bitmap decodeBitmap(byte[] bytes) {
        Bitmap retval = null;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = false;

        if (bytes != null) {
            try {
                retval = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opt);

            } catch (OutOfMemoryError oome) {

            } catch (Exception e) {


            }

        }

        return retval;
    }
    public static ByteBuffer readFileToByteBuffer(String filepath1)
    {
        try
        {

            File file1 = new File(filepath1);
            InputStream is= new FileInputStream(file1);
            ByteArrayOutputStream out= new ByteArrayOutputStream();

            int count = 0;
            byte[] b = new byte[ 8 * 1024];

            while( (count=is.read(b)) != -1 )
                out.write(b,0,count);

            is.close();

            return ByteBuffer.wrap(out.toByteArray());
        }
        catch(Exception e)
        {

            return null;
        }
    }
//    public byte[] getBytes(Bitmap bitmap)
//
//    {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//
//        return baos.toByteArray();
//
//    }
    public byte[] getBytes(ByteBuffer buffer){
        int len = buffer.limit() - buffer.position();
        byte[] bytes = new byte[len];
        buffer.get(bytes);
        return bytes;
}
    public ByteBuffer getBuffer(byte[] bytes){
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        return buf;
    }

//-----------------------------------输出数据后处理------------------------------------------------------------



}
