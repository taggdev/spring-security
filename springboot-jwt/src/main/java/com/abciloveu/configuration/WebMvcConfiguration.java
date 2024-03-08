package com.abciloveu.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.abciloveu.converter.factory.StringToEntityPkConverterFactory;

//@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverterFactory(new StringToEntityPkConverterFactory());
	}
}