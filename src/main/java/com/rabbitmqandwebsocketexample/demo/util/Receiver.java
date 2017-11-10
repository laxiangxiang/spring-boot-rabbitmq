package com.rabbitmqandwebsocketexample.demo.util;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lxx
 */
public class Receiver {
    private final Logger log = LoggerFactory.getLogger(Receiver.class);

    public void receiveMessage(byte[] message) {
        log.info("Received byte[]<" + Hex.encodeHexString(message) + ">");
    }

    public  void receiveMessage(String message){
        log.info("Received string<" + message + ">");
    }

    public void receiveMessage(Object message){
        log.info("Received object<"+message+">");
    }
}
