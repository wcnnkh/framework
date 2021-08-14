package scw.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;

import scw.context.annotation.Provider;

/**
 * 快速验证
 * @author shuchaowen
 *
 */
@Provider
public class FastValidator implements Validator {
	private static final Validator VALIDATOR = Validation.byProvider(org.hibernate.validator.HibernateValidator.class)
			.configure().failFast(true).buildValidatorFactory().getValidator();

	public static Validator getValidator() {
		return VALIDATOR;
	}
	
	public static void validate(Object instance) throws ValidationException{
		ValidationUtils.validate(() -> VALIDATOR.validate(instance));
	}

	@Override
	public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
		return VALIDATOR.validate(object, groups);
	}

	@Override
	public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
		return VALIDATOR.validateProperty(object, propertyName, groups);
	}

	@Override
	public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value,
			Class<?>... groups) {
		return VALIDATOR.validateValue(beanType, propertyName, value, groups);
	}

	@Override
	public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
		return VALIDATOR.getConstraintsForClass(clazz);
	}

	@Override
	public <T> T unwrap(Class<T> type) {
		return VALIDATOR.unwrap(type);
	}

	@Override
	public ExecutableValidator forExecutables() {
		return VALIDATOR.forExecutables();
	}

}
