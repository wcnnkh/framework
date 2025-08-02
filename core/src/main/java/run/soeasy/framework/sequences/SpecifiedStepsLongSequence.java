package run.soeasy.framework.sequences;

import lombok.NonNull;

class SpecifiedStepsLongSequence<W extends LongSequence> extends SpecifiedStepsNumberSequence<W>
		implements LongSequence {

	public SpecifiedStepsLongSequence(W source, @NonNull Number step) {
		super(source, step);
	}

	@Override
	public long nextLong(long step) throws UnsupportedOperationException {
		return getSource().nextLong(step);
	}

}
