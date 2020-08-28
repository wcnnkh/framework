package scw.value;

public class ValueWrapper extends AbstractValueWrapper {
	private final Value value;

	public ValueWrapper(Value value) {
		this.value = value;
	}

	@Override
	public Value getValue() {
		return value;
	}

}
