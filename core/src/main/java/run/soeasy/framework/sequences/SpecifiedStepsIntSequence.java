package run.soeasy.framework.sequences;

import lombok.NonNull;

class SpecifiedStepsIntSequence<W extends IntSequence> extends SpecifiedStepsNumberSequence<W> implements IntSequence {

	public SpecifiedStepsIntSequence(W source, @NonNull Number step) {
		super(source, step);
	}

	@Override
	public int nextInt(int step) throws UnsupportedOperationException {
		return getSource().nextInt(step);
	}

}
