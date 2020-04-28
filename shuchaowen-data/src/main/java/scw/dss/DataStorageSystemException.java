package scw.dss;

public class DataStorageSystemException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DataStorageSystemException(Throwable e) {
		super(e);
	}

	public DataStorageSystemException(String message) {
		super(message);
	}

	public DataStorageSystemException(String message, Throwable e) {
		super(message, e);
	}
}
