package io.basc.framework.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;

import io.basc.framework.context.annotation.Component;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.Order;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.function.Source;

/**
 * 快速验证
 * 
 * @author wcnnkh
 *
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
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

	public static <E extends Throwable> void validate(
			Source<? extends Set<? extends ConstraintViolation<?>>, ? extends E> source)
			throws ConstraintViolationException, E {
		Set<? extends ConstraintViolation<?>> violations = source.get();
		if (CollectionUtils.isEmpty(violations)) {
			return;
		}
		throw new ConstraintViolationException(violations);
	}

	public static <E extends Throwable> boolean isVerified(
			Source<? extends Set<? extends ConstraintViolation<?>>, ? extends E> source)
			throws ConstraintViolationException, E {
		Set<? extends ConstraintViolation<?>> violations = source.get();
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
