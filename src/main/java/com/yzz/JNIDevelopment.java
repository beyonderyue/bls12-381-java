package com.yzz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
public class JNIDevelopment {
    byte[] cache;
    List<String> sources;
    public JNIDevelopment(){
        cache = new byte[1024];
        sources = new LinkedList<String>();
        //这里加入本地库文件名，也可以稍加修改读取一个外部xml或properties
        sources.add("libmcljava.dylib");
       // sources.add("libmcljava.so");
    }
    private Boolean sourceExist(String sourceName){
        File f = new File("." + File.separator + sourceName);
        return f.exists();
    }
    public void doDefaultDevelopment(){
        for (String s:sources){
            doDevelopment(s);
        }
    }
    public Boolean doDevelopment(String sourceName){
        if(sourceExist(sourceName)){
            return true;
        } else{
            try{
                File f = new File("." + File.separator + sourceName);
                if(!f.exists()){
                    f.createNewFile();
                    System.out.println("[JNIDEV]:DEFAULT JNI INITION:"+sourceName);
                }
                FileOutputStream os = new FileOutputStream(f);

                InputStream is = getClass().getResourceAsStream(sourceName);
                if(is == null){
                    os.close();
                    return false;
                }
                Arrays.fill(cache,(byte)0);
                int realRead = is.read(cache);
                while(realRead != -1){
                    os.write(cache, 0, realRead);
                    realRead = is.read(cache);
                }
                os.close();
            }
            catch(Exception e){
                System.out.println("[JNIDEV]:ERROR IN COPY JNI LIB!");
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
