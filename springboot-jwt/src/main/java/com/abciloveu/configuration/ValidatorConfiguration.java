package com.abciloveu.configuration;



import javax.validation.ValidatorFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
public class ValidatorConfiguration {

	/**
	 * @param messageSource
	 * @return
	 * 
	 * @see ValidationAutoConfiguration#defaultValidator()
	 */
	@Primary
	@Bean
	@ConditionalOnBean(MessageSource.class)
    public LocalValidatorFactoryBean beanValidator(MessageSource messageSource) {
		final LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }
	
	@Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(@Nullable ValidatorFactory validatorFactory) {
		final MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
		postProcessor.setValidatorFactory(validatorFactory);
		return postProcessor;
    }
}
