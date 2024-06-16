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
    private static final String SID_KEY = "hjs";
    private static final String TOKEN_KEY = "asfsa";

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
