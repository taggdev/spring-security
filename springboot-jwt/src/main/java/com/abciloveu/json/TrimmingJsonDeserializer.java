package com.abciloveu.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jackson.JsonComponent;

/**
 * For Spring Boot, we just have to create a custom deserializer as documented in the manual.
 * @see http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-json-components
 *
 */
@JsonComponent
public class TrimmingJsonDeserializer extends JsonDeserializer<String> {

	@Override
	public String deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		return parser.hasToken(JsonToken.VALUE_STRING) ? trimToNull(parser.getText()) : null;
	}
	
	private String trimToNull(String text) {
		if("NULL".equalsIgnoreCase(text)) {
			return null;
		}
		
		return StringUtils.trim(text);
	}
}
