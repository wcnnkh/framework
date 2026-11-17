package run.soeasy.framework.core.exchange;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.core.streaming.StreamableWrapper;

@RequiredArgsConstructor
@Getter
public class CompositeOperation<O extends Operation> implements Operations<O>, StreamableWrapper<O, Streamable<O>> {
	@NonNull
	private final Mode mode;
	@NonNull
	private final Streamable<O> source;

	@Override
	public Operation composite(@NonNull Mode mode, @NonNull Operation operation) {
		if (mode == this.mode) {
			return new CompositeOperation<Operation>(mode, Streamable.array(operation).concat(source));
		}
		return Operations.super.composite(mode, operation);
	}
}