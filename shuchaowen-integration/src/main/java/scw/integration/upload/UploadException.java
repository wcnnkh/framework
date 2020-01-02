package scw.integration.upload;

public class UploadException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UploadException(String message) {
		super(message);
	}

	public UploadException(String message, Throwable e) {
		super(message, e);
	}
}