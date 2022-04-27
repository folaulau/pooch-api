package com.pooch.api.webhook;

import java.io.Serializable;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pooch.api.utils.ObjectUtils;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class StripeEventDTO implements Serializable {

    /**
     * 
     */
    private static final long   serialVersionUID = 1L;

    /**
     * In the http header
     */
    private String              stripeSignature;

    /**
     * In the http header<br>
     * user agent from Stripe
     */
    private String              userAgent;

    private Map<String, Object> event;

    private boolean             stripeSigned;

    public String toJson() {
        return ObjectUtils.toJson(this);
    }

    public String getEventType() {
        return event.get("type").toString();
    }

    public String getEventId() {
        return event.get("id").toString();
    }

    public String getStripeModelId() {
        Map<String, Object> data = (Map<String, Object>) event.get("data");
        Map<String, Object> object = (Map<String, Object>) data.get("object");
        return object.get("id").toString();
    }

    public String getEventAsJson() {
        return ObjectUtils.toJson(event);
    }

    public static StripeEventDTO fromJson(String json) {
        try {
            return ObjectUtils.getObjectMapper().readValue(json, StripeEventDTO.class);
        } catch (Exception e) {
            System.out.println("JsonProcessingException, msg: " + e.getLocalizedMessage());
            return null;
        }
    }

}
