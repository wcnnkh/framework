package run.soeasy.framework.core.exchange;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SuccessfullyRegistered implements Receipt, RegistrationWrapper<Registration> {
	@NonNull
	private final Registration source;

	@Override
	public Registration getSource() {
		return source;
	}

	@Override
	public Throwable cause() {
		return null;
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public boolean isSuccess() {
		return true;
	}
}