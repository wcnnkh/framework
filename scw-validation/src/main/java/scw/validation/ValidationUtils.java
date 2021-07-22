package scw.validation;

import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;

import scw.core.utils.CollectionUtils;
import scw.util.stream.CallableProcessor;

public class ValidationUtils {
	private ValidationUtils() {
	};

	public static <T extends ConstraintViolation<?>> void throwValidationException(Set<? extends T> violations)
			throws ValidationException {
		if (CollectionUtils.isEmpty(violations)) {
			return;
		}

		Optional<? extends T> optional = violations.stream().findFirst();
		if (optional.isPresent()) {
			if (optional.isPresent()) {
				throw new ValidationException(optional.get().getMessage());
			}
		}
		return;
	}

	public static <T extends ConstraintViolation<?>> void validate(
			CallableProcessor<Set<T>, RuntimeException> processor) throws ValidationException {
		Set<T> constraintViolations = processor.process();
		throwValidationException(constraintViolations);
	}
}
