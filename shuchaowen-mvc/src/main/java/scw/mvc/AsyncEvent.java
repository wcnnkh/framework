package scw.mvc;

public class AsyncEvent {
	private final AsyncControl asyncControl;
	private final Throwable throwable;

	public AsyncEvent(AsyncControl asyncControl, Throwable throwable) {
		this.asyncControl = asyncControl;
		this.throwable = throwable;
	}

	public AsyncControl getAsyncControl() {
		return asyncControl;
	}

	public Throwable getThrowable() {
		return throwable;
	}
}
