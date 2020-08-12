package scw.microsoft;

public class ExcelException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ExcelException(String message) {
		super(message);
	}

	public ExcelException(Throwable e) {
		super(e);
	}

	public ExcelException(String message, Throwable e) {
		super(message, e);
	}

}
