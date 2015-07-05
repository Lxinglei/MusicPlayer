package cn.meteor.ch08;

import java.io.*;
import java.text.DecimalFormat;

/**
 * Created by meteor on 15-6-24.
 */
public class ENDECODE {
    private File file;
    private String path;
    private String savePath;
    private long size = 0;



    public void encode(String srcPath){
        this.path = srcPath;
        this.file = new File(path);
        this.savePath = srcPath + ".bak";
        System.out.println(savePath);
        OutputStream ops = null;
        InputStream ins = null;

        size = file.length();
        try {
            System.out.println("encoding...");
            File saveFile = new File(savePath);
            if (!saveFile.exists())
                saveFile.createNewFile();
            ops = new FileOutputStream(new File(savePath));
            ins = new FileInputStream(file);
            int temp;
            long count = 0;
            while ((temp=ins.read() )!= -1){
                count ++;
                if (count % 1500 == 0) {
                    double i = (double) (count * 100) / (double) size;
                    System.out.println(new DecimalFormat("0.00").format(i) + "%");
                }
                ops.write(temp - 1);
            }
            ops.flush();
            file.delete();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
                try {
                    if (ins != null)
                        ins.close();
                    if (ops != null)
                        ops.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public void decode(String srcPath){
        this.path = srcPath;
        this.file = new File(path);
        System.out.println("src : " + path);
        this.savePath = srcPath.substring(0, srcPath.indexOf(".bak"));
        System.out.println("dest : " + savePath);
        OutputStream ops = null;
        InputStream ins = null;

        size = file.length();
        long count = 0;
        try {
            System.out.println("decoding...");
            File saveFile = new File(savePath);
            if (!saveFile.exists())
                saveFile.createNewFile();
            ops = new FileOutputStream(new File(savePath));
            ins = new FileInputStream(file);
            int temp;
            while ((temp=ins.read() )!= -1){
                count ++;
                if (count % 1500 == 0) {
                    double i = (double) (count * 100) / (double) size;
                    System.out.println(new DecimalFormat("0.00").format(i) + "%");
                }
                ops.write(temp + 1);
            }
            ops.flush();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ins != null)
                    ins.close();
                if (ops != null)
                    ops.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        String path = "/home/meteor/Downloads/30edea9981dfce81af43b61c9878c6e91423275306-640-360-602-h264.flv";
        String path1 = "/home/meteor/Downloads/05b2114b6440149394c957353c1ce7421422893409-640-360-1200-h264.flv.bak";
        ENDECODE endecode = new ENDECODE();
        endecode.encode(path);
//        endecode.decode(path1);
        //endecode.encode("/home/meteor/Desktop/胡彦斌 - 我的未来不是梦.mp3.bak");
        //endecode.decode("/home/meteor/Desktop/胡彦斌 - 我的未来不是梦.mp3.bak");
    }
}