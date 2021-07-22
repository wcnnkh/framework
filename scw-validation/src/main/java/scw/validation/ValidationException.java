package scw.validation;

public class ValidationException extends javax.validation.ValidationException {
	private static final long serialVersionUID = 1L;

	public ValidationException(String message) {
		super(message);
	}
}
