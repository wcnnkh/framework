package scw.validation;

import scw.env.Sys;

public class ValidationUtils {
	private ValidationUtils() {
	};

	private static final Validator VALIDATOR = Sys.env
			.getServiceLoader(Validator.class, "scw.validation.HibernateValidator", "scw.validation.DefaultValidator")
			.first();

	public static Validator getValidator() {
		return VALIDATOR;
	}

	public static void validate(Object object) {
		VALIDATOR.validate(object);
	}
}
