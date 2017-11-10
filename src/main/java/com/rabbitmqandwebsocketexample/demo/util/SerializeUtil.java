package com.rabbitmqandwebsocketexample.demo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtil {

    /**
     * 反序列化，把字节数组转换为object对象
     * @param bytes
     * @return
     */
    public static Object ByteToObject(byte[] bytes){
        Object obj = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            obj = objectInputStream.readObject();
            objectInputStream.close();
            byteArrayInputStream.close();
        }catch (Exception e){
            System.out.println("translation:"+e.getMessage());
            e.printStackTrace();
        }
        return obj;
    }


    /**
     * 序列化，把对象转换为字节数组
     * @param obj
     * @return
     */
    public static byte[] objectToBytes(Object obj){
        byte[] bytes = null;
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            bytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            objectOutputStream.close();
        }catch (Exception e){
            System.out.println("translation:"+e.getMessage());
            e.printStackTrace();
        }
        return bytes;
    }
}
