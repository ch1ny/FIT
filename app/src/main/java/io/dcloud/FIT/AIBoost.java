package io.dcloud.FIT;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.util.Base64;
import android.util.Log;

import com.ai.aiboost.AiBoostInterpreter;
import com.example.detectiondemo.ObjectDetection;
import com.example.detectiondemo.Recognition;
import com.example.detectiondemo.Suggestion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class AIBoost extends Activity {

    private Activity activity;
    private AiBoostInterpreter aiboost;
    private AiBoostInterpreter.Options options;
    int DIM_BATCH_SIZE = 1;
    int DIM_PIXEL_SIZE = 3;
    int BYTE_NUM_PER_CHANNEL = 1;
    int IMAGE_SIZE_X = 320;
    int IMAGE_SIZE_Y = 320;
    private static final float IMAGE_MEAN = 0;//均值
    private static final float IMAGE_STD = 255.0f;//标准化
    private List<String> labelList;
    private ByteBuffer imgData = ByteBuffer.allocateDirect(DIM_BATCH_SIZE * DIM_PIXEL_SIZE * IMAGE_SIZE_X * IMAGE_SIZE_Y* 4);
    private Map<Integer, Object> outputMap = new HashMap<>();
    private ByteBuffer outData;
    private int output_box=6300;//先验框的个数
    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.3f;
    private Recognition[] recognitions = null;
    private Bitmap bitmap;


    public AIBoost(Activity activity){
        // 定义AiBoostInterpreter和AiBoostInterpreter.Option
        this.activity = activity;
        aiboost = null;
        options = null;
    }

    public void initOptions() {
        System.out.println("初始化Options参数");
        // 创建AiBoostInterpreter.Options参数
        options = new AiBoostInterpreter.Options();
        options.setNumThreads(4); // 设置线程数
        options.setDeviceType(AiBoostInterpreter.Device.QUALCOMM_NPU); // 设置运行设备类型
        options.setQComPowerLevel(AiBoostInterpreter.QCOMPowerLEVEL.QCOM_TURBO); // 设置高通设备性能级别
        options.setMTKPowerLevel(AiBoostInterpreter.MTKPowerLEVEL.FastSingleAnswer); // 设置MTK设备性能级别
        options.setNativeLibPath(activity.getApplicationInfo().nativeLibraryDir); // 设置存放libs的路径 note: 使用高通DSP/NPU时，必须要添加该路径
        System.out.println("初始化Options参数完成");
    }

    public void initAiBoost() throws IOException {
        System.out.println("创建AiBoostInterpreter参数");
        // 创建AiBoostInterpreter参数
        AssetManager assetManager = activity.getAssets();
        InputStream input =  assetManager.open("detect.tflite"); // fileName输入TFlite名称
        int length = input.available();
        byte[] buffer = new byte[length];
        input.read(buffer);
        ByteBuffer modelbuf = ByteBuffer.allocateDirect(length);
        modelbuf.order(ByteOrder.nativeOrder());
        modelbuf.put(buffer);
        int[][] input_shapes = new int[][]{{DIM_BATCH_SIZE, IMAGE_SIZE_Y, IMAGE_SIZE_X, DIM_PIXEL_SIZE}};
        aiboost = new AiBoostInterpreter(modelbuf, input_shapes, options);
        labelList = loadLabelList(activity);
        System.out.println("创建AiBoostInterpreter参数结束");
    }
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("detect.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
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

    public void input(String fileName) throws IOException {
        System.out.println("数据输入开始");
        /**
         *
         */
        try {
            InputStream img = new FileInputStream("/storage/emulated/0/DCIM/Camera/" + fileName);
            bitmap = BitmapFactory.decodeStream(img);
        } catch (Exception e) {
            e.printStackTrace();
//            AssetManager asset = activity.getAssets();
//            InputStream img = asset.open("Temp/1.jpg");
//            bitmap = BitmapFactory.decodeStream(img);
        }
        /**
         *
         */
        System.out.println("数据输入完成");
    }

    public String Run() {
        ByteBuffer predict = predict_image(bitmap);
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
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    //-----------------------------------------输入数据预处理--------------------------------------------
    //与图像分类的预处理工作高度相似  把一张图片的三个通道每个通道的像素点数据存入ByteBuffer类型的 imgData
    private ByteBuffer getInputData(Bitmap bitmap) {
        imgData.order(ByteOrder.nativeOrder());
        int[] intValues = new int[IMAGE_SIZE_X * IMAGE_SIZE_Y];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        imgData.rewind();
        for (int i=0; i<IMAGE_SIZE_X;++i){
            for (int j = 0; j < IMAGE_SIZE_Y; ++j){
                int val = intValues[i * 320+ j];
                imgData.putFloat((((val >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                imgData.putFloat((((val >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                imgData.putFloat(((val & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
//                imgData.putFloat(((val >> 16) & 0xFF)  * 2.0f / 255.0f - 1.0f );
//                imgData.putFloat(((val >> 8) & 0xFF) * 2.0f / 255.0f - 1.0f );
//                imgData.putFloat((val & 0xFF) * 2.0f / 255.0f - 1.0f );
            }
        }
        int[] shape = aiboost.getOutputTensorShape(0);
        int numClass = shape[shape.length - 1];
        outData = ByteBuffer.allocateDirect(output_box * (numClass + 5) * 4);
        outData.order(ByteOrder.nativeOrder());
        outData.rewind();
        outputMap.put(0, outData);
        return imgData;
    }
//-------------------------------------------输入数据预处理----------------------------------------
//-----------------------------------推理预测函数-------------------------------------------------------------
public ByteBuffer predict_image(Bitmap oribitmap) {
    Bitmap resizedbitmap = Bitmap.createScaledBitmap(oribitmap, IMAGE_SIZE_X, IMAGE_SIZE_Y, false);//resize
    ByteBuffer imgData = getInputData(resizedbitmap);//输入数据预处理
    Object[] inputArray = {imgData};//runForMultipleInputsOutput函数的输入是一个Object类型的数组
    aiboost.runForMultipleInputsOutputs(inputArray, outputMap);//运行模型
    recognize(resizedbitmap);//后处理
    ByteBuffer buffer = ByteBuffer.wrap(getClasses().getBytes());
    return buffer;
}
    private String getClasses() {
        String str = "";
//        String[] classes = new String[recognitions.length];
//        int i=0;
        for (Recognition recognition : recognitions) {
            System.out.println("可信度" + recognition.getConfidence());
            if (recognition.getConfidence() > 0.3) {
//                Double id = Double.valueOf(recognition.getId());//被检测物体的类别ID
//                int ID = (int) Math.ceil(id);
                String title = recognition.getTitle();
//                classes[i++] = title;
                System.out.println("Title：" + title);
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
                out[0][i][j] *= IMAGE_SIZE_X;
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
    public float getObjThresh() {
        return MINIMUM_CONFIDENCE_TF_OD_API;
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
//-----------------------------------推理预测函数-------------------------------------------------------------

    public void run() {
        ByteBuffer output = aiboost.getOutputTensor(0);
        byte[] result = new byte[output.remaining()];
        aiboost.runWithOutInputOutput();
        output.get(result, 0, result.length);
        System.out.println("看这里看这里：" + output);
    }

    public void destroy() {
        if (aiboost != null) {
            aiboost.close();
            aiboost = null;
        }
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
