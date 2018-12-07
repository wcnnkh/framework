package shuchaowen.common.exception;

public class NotFoundException extends RuntimeException{
	private static final long serialVersionUID = 5341163945147654715L;

	public NotFoundException(String message) {
		super(message);
	}
}
