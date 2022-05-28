package com.pooch.api.entity.notification.email;

import java.io.IOException;
import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeliveryStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String DELIVERED = "delivered";
	public static final String NOT_DELIVERED = "not_delivered";
	
	private Boolean delivered;
	private String message;
	
	public DeliveryStatus() {
		this(null,null);
	}
	
	
	public DeliveryStatus(Boolean delivered, String message) {
		this.setMessage(message);
		this.setDelivered(delivered);
	}
	
	public Boolean isDelivered() {
		return delivered;
	}

	public Boolean getDeliveryStatus(){
		return delivered;
	}

	public void setDelivered(Boolean delivered) {
		this.delivered = delivered;
	}

	
	public String getMessage() {
		return message;
	}


	public String getDeliveryMsg(){
		if(delivered)
			return DELIVERED;
		else
			return NOT_DELIVERED;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public static DeliveryStatus fromJson(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, DeliveryStatus.class);
	}
	
	public String toJson() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object other) {
		return EqualsBuilder.reflectionEquals(this, other);
	}
}
