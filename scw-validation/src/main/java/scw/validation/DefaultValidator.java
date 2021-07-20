package scw.validation;

import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import scw.core.utils.CollectionUtils;

public class DefaultValidator extends AbstractValidator {
	private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
	private final Validator validator;

	public DefaultValidator() {
		this(VALIDATOR);
	}

	public DefaultValidator(Validator validator) {
		this.validator = validator;
	}

	@Override
	public <T> void validate(T instance, Class<?>... groups) throws ValidationException {
		Set<ConstraintViolation<T>> violations = validator.validate(instance, groups);
		if (CollectionUtils.isEmpty(violations)) {
			return;
		}

		Optional<ConstraintViolation<T>> optional = violations.stream().findFirst();
		if (optional.isPresent()) {
			throw new ValidationException(optional.get().getMessage());
		}
	}

	@Override
	public <T> void validateProperty(T object, String propertyName, Class<?>... groups) throws ValidationException {
		Set<ConstraintViolation<T>> violations = validator.validateProperty(object, propertyName, groups);
		if (CollectionUtils.isEmpty(violations)) {
			return;
		}

		Optional<ConstraintViolation<T>> optional = violations.stream().findFirst();
		if (optional.isPresent()) {
			throw new ValidationException(optional.get().getMessage());
		}
	}

	@Override
	public <T> void validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups)
			throws ValidationException {
		Set<ConstraintViolation<T>> violations = validator.validateValue(beanType, propertyName, value, groups);
		if (CollectionUtils.isEmpty(violations)) {
			return;
		}

		Optional<ConstraintViolation<T>> optional = violations.stream().findFirst();
		if (optional.isPresent()) {
			throw new ValidationException(optional.get().getMessage());
		}
	}
}
