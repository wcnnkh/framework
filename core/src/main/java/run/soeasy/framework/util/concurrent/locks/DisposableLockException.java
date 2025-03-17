package run.soeasy.framework.util.concurrent.locks;

public class DisposableLockException extends InterruptedException {
	private static final long serialVersionUID = 1L;

	public DisposableLockException(String message) {
		super(message);
	}
}
