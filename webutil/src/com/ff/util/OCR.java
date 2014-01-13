package com.ff.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OCR {
    private final String LANG_OPTION = "-psm 7";  //英文字母小写l，并非数字1
    private final String EOL = System.getProperty("line.separator");
    private String tessPath = "C:\\Users\\QIYI\\AppData\\Local\\Tesseract-OCR"; //

    //private String tessPath = new File("tesseract").getAbsolutePath();
    
    public String recognizeText(File imageFile)throws Exception{
        File outputFile = new File(imageFile.getParentFile(),UUID.randomUUID().toString());
        StringBuffer strB = new StringBuffer();
        List<String> cmd = new ArrayList<String>();
        cmd.add(tessPath+"\\tesseract.exe");
        cmd.add("");
        cmd.add(outputFile.getAbsolutePath());
        cmd.add("-psm");
        cmd.add("7");
        cmd.add("char");
        
        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(imageFile.getParentFile());
        
        cmd.set(1, imageFile.getAbsolutePath());
        pb.command(cmd);
        pb.redirectErrorStream(true);
        
        Process process = pb.start();
        int w = process.waitFor();
        
        if(w==0){
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile.getAbsolutePath()+".txt"),"UTF-8"));
            
            String str;
            while((str = in.readLine())!=null){
                strB.append(str).append(EOL);
            }
            in.close();
        }else{
            String msg;
            switch(w){
                case 1:
                    msg = "Errors accessing files.There may be spaces in your image's filename.";
                    break;
                case 29:
                    msg = "Cannot recongnize the image or its selected region.";
                    break;
                case 31:
                    msg = "Unsupported image format.";
                    break;
                default:
                    msg = "Errors occurred.";
            }
            //tempImage.delete();
            throw new RuntimeException(msg);
        }
        new File(outputFile.getAbsolutePath()+".txt").delete();
        return strB.toString().replaceAll(" ", "").trim();
    }
}
