package com.abciloveu.validation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.MessageResolver;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.PropertiesMessageResolver;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.passay.spring.SpringMessageResolver;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 * https://stackabuse.com/spring-custom-password-validation/
 * https://www.baeldung.com/registration-password-strength-and-rules
 */
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String>, MessageSourceAware {

	MessageResolver messageResolver = new PropertiesMessageResolver();

	public PasswordConstraintValidator() {
		super();
	}

	@Override
	public void initialize(ValidPassword constraintAnnotation) {

	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		messageResolver = new SpringMessageResolver(messageSource);
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		final PasswordValidator validator = new PasswordValidator(messageResolver, Arrays.asList(
			// at least 8 characters
			new LengthRule(8, 50),

			// at least one upper-case character
			new CharacterRule(EnglishCharacterData.UpperCase, 1),

			// at least one lower-case character
			new CharacterRule(EnglishCharacterData.LowerCase, 1),

			// at least one digit character
			new CharacterRule(EnglishCharacterData.Digit, 1),

			// at least one symbol (special character)
			new CharacterRule(EnglishCharacterData.Special, 1),

			// no whitespace
			new WhitespaceRule()

		));

		//support case update : not update password send password null
		if(StringUtils.isNotEmpty(password)){
			final RuleResult result = validator.validate(new PasswordData(password));
			if (result.isValid()) {
				return true;
			}

			final List<String> messages = validator.getMessages(result);
			final String messageTemplate = messages.stream()
					.collect(Collectors.joining(", "));

			context
					.buildConstraintViolationWithTemplate(messageTemplate)
					.addConstraintViolation()
					.disableDefaultConstraintViolation();

			return false;
		}else{
			return true;
		}

	}


}
