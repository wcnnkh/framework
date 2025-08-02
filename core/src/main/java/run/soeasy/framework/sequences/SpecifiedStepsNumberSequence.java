package run.soeasy.framework.sequences;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.domain.Wrapper;

@Getter
@RequiredArgsConstructor
class SpecifiedStepsNumberSequence<W extends NumberSequence> implements NumberSequence, Wrapper<W> {
	private final W source;
	@NonNull
	private final Number step;

	@Override
	public @NonNull Number next(@NonNull Number step) throws UnsupportedOperationException {
		return source.next(step);
	}

}
