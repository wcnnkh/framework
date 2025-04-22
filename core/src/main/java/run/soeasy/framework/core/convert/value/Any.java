package run.soeasy.framework.core.convert.value;

public final class Any extends Data<Object> implements ValueAccessor {
	private static final long serialVersionUID = 1L;

	@Override
	public Any any() {
		return this;
	}
}
