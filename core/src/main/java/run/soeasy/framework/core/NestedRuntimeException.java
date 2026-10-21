package run.soeasy.framework.core;

public class NestedRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	static {
		// Eagerly load the NestedExceptionUtils class to avoid classloader deadlock
		NestedExceptionUtils.class.getName();
	}

	public NestedRuntimeException(String msg) {
		super(msg);
	}
	
	public NestedRuntimeException(Throwable cause) {
		super(cause);
	}

	public NestedRuntimeException(String msg, Throwable cause) {
		super(msg, cause);
	}

	@Override
	public String getMessage() {
		return NestedExceptionUtils.buildMessage(super.getMessage(), getCause());
	}

	public Throwable getRootCause() {
		return NestedExceptionUtils.getRootCause(this);
	}

	public Throwable getMostSpecificCause() {
		Throwable rootCause = getRootCause();
		return (rootCause != null ? rootCause : this);
	}

	public boolean contains(Class<?> exType) {
		if (exType == null) {
			return false;
		}
		if (exType.isInstance(this)) {
			return true;
		}
		Throwable cause = getCause();
		if (cause == this) {
			return false;
		}
		if (cause instanceof NestedRuntimeException) {
			return ((NestedRuntimeException) cause).contains(exType);
		} else {
			while (cause != null) {
				if (exType.isInstance(cause)) {
					return true;
				}
				if (cause.getCause() == cause) {
					break;
				}
				cause = cause.getCause();
			}
			return false;
		}
	}

}