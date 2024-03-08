package com.abciloveu.converter.factory;

import java.util.Arrays;

import com.abciloveu.common.EntityPk;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import com.abciloveu.exception.PkConversionException;

/**
 * For encapsulating conversion logic for a range of objects, we can try ConverterFactory implementation
 * https://www.baeldung.com/spring-mvc-custom-data-binder
 */
public class StringToEntityPkConverterFactory implements ConverterFactory<String, EntityPk> {

	private static final Logger LOG = LoggerFactory.getLogger(StringToEntityPkConverterFactory.class);

	@Override
	public <T extends EntityPk> Converter<String, T> getConverter(Class<T> targetClass) {
		return new StringToEntityPkConverter<>(targetClass);
	}

	private static class StringToEntityPkConverter<T extends EntityPk> implements Converter<String, T> {

		private Class<T> targetClass;

		public StringToEntityPkConverter(Class<T> targetClass) {
			this.targetClass = targetClass;
		}

		@Override
		public T convert(String source) {
			if (StringUtils.isEmpty(source)) {
				return null;
			}

			final String[] arguments = Arrays.stream(StringUtils.splitPreserveAllTokens(source, ":"))
					.map(StringUtils::trimToNull).toArray(String[]::new);
			
			final Class<?> parameterTypes[] = new Class<?>[arguments.length];

			try {
				Arrays.fill(parameterTypes, String.class);

				LOG.debug("Create entityPk type = {} with arguments = {}", targetClass.getName(), arguments);
				return ConstructorUtils.invokeConstructor(targetClass, arguments, parameterTypes);
			}
			catch (NoSuchMethodException e) {
				LOG.error("Cannot not found constructor type={}, with parameterTypes = {}", targetClass, parameterTypes, e.getMessage());
				final String errorMessage = String.format("Cannot convert '%s' to '%s'.", source, targetClass.getSimpleName());
				throw new PkConversionException(errorMessage);
			}
			catch (Exception e) {
				LOG.error("Cannot create entityPk type = {}, with = {}", targetClass, arguments, e.getMessage());
				final String errorMessage = String.format("Cannot convert '%s' to '%s'.", source, targetClass.getSimpleName());
				throw new PkConversionException(errorMessage);
			}
		}
	}
}