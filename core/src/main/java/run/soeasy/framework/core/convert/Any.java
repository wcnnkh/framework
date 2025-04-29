package run.soeasy.framework.core.convert;

public final class Any extends Data<Object> implements TypedValueAccessor {
	private static final long serialVersionUID = 1L;

	@Override
	public Any value() {
		return this;
	}
}
