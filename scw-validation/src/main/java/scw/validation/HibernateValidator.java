package scw.validation;

import java.util.Optional;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import scw.core.utils.CollectionUtils;

public class HibernateValidator extends AbstractValidator {
	protected static final Validator VALIDATOR = Validation.byProvider(org.hibernate.validator.HibernateValidator.class)
			.configure().failFast(true).buildValidatorFactory().getValidator();

	private final Validator validator;

	public HibernateValidator() {
		this(VALIDATOR);
	}

	public HibernateValidator(Validator validator) {
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
