package com.qzero.telegram.utils;

import java.io.*;

public class StreamUtils {

    public static byte[] readDataFromInputStream(InputStream inputStream) throws IOException {
        byte[] buf=new byte[1024];
        int len;
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        while((len=inputStream.read(buf))!=-1){
            outputStream.write(buf,0,len);
        }

        return outputStream.toByteArray();
    }

    public static void writeDataToOutputStream(OutputStream outputStream,byte[] data) throws IOException{
        outputStream.write(data);
    }

    public static byte[] readFile(File file) throws IOException {
        InputStream inputStream=new FileInputStream(file);
        byte[] result= readDataFromInputStream(inputStream);
        inputStream.close();
        return result;
    }

    public static void writeFile(File file,byte[] data) throws IOException{
        OutputStream outputStream=new FileOutputStream(file);
        writeDataToOutputStream(outputStream,data);
        outputStream.close();
    }

}
