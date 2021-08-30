package io.basc.framework.validation;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.stream.CallableProcessor;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;

/**
 * 快速验证
 * 
 * @author shuchaowen
 *
 */
@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class FastValidator implements Validator {
	private static final Validator VALIDATOR = Validation.byProvider(org.hibernate.validator.HibernateValidator.class)
			.configure().failFast(true).buildValidatorFactory().getValidator();

	public static Validator getValidator() {
		return VALIDATOR;
	}

	public static void validate(Object instance) throws ConstraintViolationException {
		validate(() -> VALIDATOR.validate(instance));
	}

	public static boolean isVerified(Object instance) throws ConstraintViolationException {
		return isVerified(() -> VALIDATOR.validate(instance));
	}

	public static void validate(
			CallableProcessor<Set<? extends ConstraintViolation<?>>, ? extends RuntimeException> validateProcessor)
			throws ConstraintViolationException {
		Set<? extends ConstraintViolation<?>> violations = validateProcessor.process();
		if (CollectionUtils.isEmpty(violations)) {
			return;
		}
		throw new ConstraintViolationException(violations);
	}

	public static boolean isVerified(
			CallableProcessor<Set<? extends ConstraintViolation<?>>, ? extends RuntimeException> validateProcessor)
			throws ConstraintViolationException {
		Set<? extends ConstraintViolation<?>> violations = validateProcessor.process();
		return CollectionUtils.isEmpty(violations);
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
