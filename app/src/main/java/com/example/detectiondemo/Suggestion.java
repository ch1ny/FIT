package com.example.detectiondemo;

import android.app.Activity;
import android.graphics.Bitmap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import io.dcloud.FIT.MainActivity;

public class Suggestion {
    Activity activity;
    ObjectDetection test = new ObjectDetection();
    private int[] ddims = {1, 3, 320, 320};//根据自己的实际情况修改
    private ByteBuffer imgData = ByteBuffer.allocateDirect(ddims[0] * ddims[1] * ddims[2] * ddims[3]* 4);
    private static final float IMAGE_MEAN = 0;//均值
    private static final float IMAGE_STD = 255.0f;//标准化
    String[] color=new String[]{"#7D6C5C","#761B22","#BD464C","#615B5B","#CF9BA3","#505050"};
    String[] styleColor =new String[]{"卡其色","复古红","中国红","运动灰","少女粉","清冷灰"};//风格颜色
    String[] styleFeature =new String[]{"除了黑白灰之外，职场风最流行看上去就很有质感的卡其色、驼色等颜色，精干又得体。\n",
    "港风穿搭整体色调偏灰、暗黄，也有复古红等浓烈色彩,带有浓郁的复古风情\n",
    "国潮风指拥有中国特色元素、标志，融合时尚潮流设计的穿搭，元素表现为盘扣、龙图案、中式印花等等\n",
    "运动风的服装往往具有有活力的色彩元素，运动服元素，醒目的字母印花\n",
    "少女风穿搭表现在色调温柔娴静，服装线条柔美\n",
    "简约风格的穿搭，服装通常都是低调的经典款，廓形也比较合身，以饱和度较低的颜色为主\n"};//风格特点


    String[] sceneColor =new String[8];//风格颜色
    String[] sceneFeature =new String[]{"对于职场，穿搭原则是简介、知性、得体\n",
            "约会的场合，穿搭最好吸睛又好看，粉红色会很有初恋的感觉\n",
            "运动场合，最重要的是轻松自在\n",
            "见朋友可以穿的时尚个性，冷色系会让人气场十足\n",
            "逛街，风格多变，可以时尚休闲，也可以慵懒随意\n",
            "见导师，舒适不拘谨，乖巧又大方，暖色系的搭配会是不错的选择\n",
            "游乐场，可以尝试减龄穿搭，少年感十足\n",
            "看电影，以自然舒适为主，风格多变\n"};//场合特点

    String[] styleName=new String[]{"work","HongKong","street","sports","girl","simple"};
//                                     0        1         2         3      4        5
    String[] styleChine=new String[]{"职场","港风","街头","运动","女生","简约","国潮","上班"};
    String[] sceneName=new String[]{"上班","约会","运动","见朋友","购物","见导师","游乐场","看电影"};
//                                     0      1     2          3         4      5       6        7
    int[][] styleToScene=new int[][]{{0,5,6},{1,4,5,6},{3,5},{1,2,3,4,5,6},{1,2,3,4,5,6},{0,4,5},{1,2,3,4,5},{1,2,3,4,5}};

    public Suggestion(Activity activity) {
        this.activity = activity;
    }

    public int[] getStyle(Bitmap image) {
//        ByteBuffer buffer = test.readFileToByteBuffer(image_path);
        test.load_model(activity);
        ByteBuffer bufferout = test.predict_image(image);
        byte[] classes=test.getBytes(bufferout);
        int[] detectClass=new int[classes.length];
        for(int i=0;i<classes.length;i++){
            detectClass[i]=(int)classes[i];
        }
        return detectClass;
    }

    public String getSuggs(String targetStyle,String targetScene,Bitmap image){
        int[] detectClass=getStyle(image);
        String s1 = "",s2="",s3="";
        boolean[] fit=new boolean[]{false,false};//上身和下身
        int styleNum=transStyle(targetStyle);
        int sceneNum=transScene(targetScene);


        for(int i=0;i<detectClass.length;i++){
            if(detectClass[i]==styleNum*3+2){//满足套装
                fit[0]=true;
                fit[1]=true;
            }
            else if(detectClass[i]==styleNum*3){//满足上装
                fit[0]=true;
                }
            else if(detectClass[i]==styleNum*3+1){//满足下装
                fit[1]=true;
            }
        }


        if(fit[0]==false){
            s1+="风格："+styleFeature[styleNum] + "\n";
            s3+="将上衣改为"+styleColor[styleNum] + "\n";

        }else if(fit[1]==false){
            s1+="风格："+styleFeature[styleNum] + "\n";
            s3+="将下身改为"+styleColor[styleNum] + "\n";

        }else{
            s1+="风格："+targetStyle+" 适合\n";
        }

        boolean sceneFit=false;
        for(int i=0;i<styleToScene[sceneNum].length;i++){
            if(styleToScene[sceneNum][i]==styleNum)
                sceneFit=true;
        }

        if(sceneFit==false){
            s2+="场合："+sceneFeature[sceneNum] + "\n";
        }else{
            s2+="场合："+targetScene+" 适合" + "\n";
        }

        String s=s1+s2+s3;
        return s;

    }

    public int transStyle(String name){
        for(int i=0;i<styleName.length;i++){
            if(name.equals(styleName[i])){
                return i;
            }
        }
        for(int i=0;i<styleChine.length;i++){
            if(name.equals(styleChine[i])){
                return i;
            }
        }
        return -1;//没找到
    }
    public int transScene(String name){
        for(int i=0;i<sceneName.length;i++){
            if(name.equals(sceneName[i])){
                return i;
            }
        }
        return -1;//没找到
    }
}
