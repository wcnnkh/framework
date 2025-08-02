package run.soeasy.framework.sequences;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.domain.Wrapped;

@Getter
class SpecifiedLengthStringSequence<W extends StringSequence> extends Wrapped<W> implements StringSequence {
	private final int length;

	public SpecifiedLengthStringSequence(@NonNull W source, int length) {
		super(source);
		this.length = length;
	}

	@Override
	public String next(int length) throws UnsupportedOperationException {
		return getSource().next(length);
	}
}
