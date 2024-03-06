package com.abciloveu.converter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StringSetConverter implements AttributeConverter<Set<String>, String> {

	private static final String SPLIT_CHAR = ",";

	@Override
	public String convertToDatabaseColumn(Set<String> stringList) {
		return String.join(SPLIT_CHAR, stringList);
	}

	@Override
	public Set<String> convertToEntityAttribute(String string) {
		return new HashSet<>(Arrays.asList(string.split(SPLIT_CHAR)));
	}
}