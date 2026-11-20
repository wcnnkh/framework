package run.soeasy.framework.core.exchange;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class SignalException extends CompletionException {
	private static final long serialVersionUID = 1L;
	private final Signal signal;

	public SignalException(@NonNull Throwable cause, Signal signal) {
		super(cause);
		this.signal = signal;
	}

	public static Throwable unwrap(Throwable cause) {
		if (cause == null) {
			return null;
		}

		while (cause instanceof CompletionException || cause instanceof ExecutionException) {
			Throwable e = cause.getCause();
			if (e == null) {
				break;
			}
			cause = e;
		}
		return cause;
	}
}
