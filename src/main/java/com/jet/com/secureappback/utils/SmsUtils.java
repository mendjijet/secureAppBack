package com.jet.com.secureappback.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.concurrent.CompletableFuture;

import static com.twilio.rest.api.v2010.account.Message.creator;

/**
 * @project secureCapita
 * @version 1.0
 * @author MENDJIJET
 * @since 12/06/2024
 */
public class SmsUtils {
    private static final String FROM_NUMBER = "+14782105287";
    private static final String SID_KEY = "AC5f1f44ea48833e4cd5b0275b54130a48";
    private static final String TOKEN_KEY = "bf9641eb42186d0dbbce856c1e3f8775";

    public static void sendSMS(String phoneNumber, String messageBody) {
        CompletableFuture.runAsync(() -> {
            Twilio.init(SID_KEY, TOKEN_KEY);
            Message message = Message.creator(new PhoneNumber("+230" + phoneNumber), new PhoneNumber(FROM_NUMBER), messageBody).create();
        });
    }

//    public static void sendSMS(String phoneNumber, String messageBody) {
//        Twilio.init(SID_KEY, TOKEN_KEY);
//        Message message = Message.creator(new PhoneNumber("+230" + phoneNumber), new PhoneNumber(FROM_NUMBER), messageBody).create();
//        System.out.println("Message : " + message);
//    }
}
