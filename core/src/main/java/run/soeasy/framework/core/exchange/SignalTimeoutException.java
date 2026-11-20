package run.soeasy.framework.core.exchange;

import java.util.concurrent.TimeoutException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignalTimeoutException extends TimeoutException {
	private static final long serialVersionUID = 1L;
	private final Signal signal;
}
