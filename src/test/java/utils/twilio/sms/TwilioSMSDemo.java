package utils.twilio.sms;

import com.pooch.api.config.LocalAppConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

public class TwilioSMSDemo {

    public static void main(String[] args) {

        Twilio.init("ACe7a124618def3fc7a3b286e58f327136", "7db658dc368c8511be0059dc072c6b55");
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber("+13109934731"),
                new com.twilio.type.PhoneNumber("+19704788694"),
                "This is the ship that made the Kessel Run in fourteen parsecs?")
            .create();

        System.out.println(message.getSid());
    }

}
