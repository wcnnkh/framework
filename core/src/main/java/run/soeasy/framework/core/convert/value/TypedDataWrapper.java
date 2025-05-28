package run.soeasy.framework.core.convert.value;

public interface TypedDataWrapper<T, W extends TypedData<T>> extends TypedData<T>, SourceDescriptorWrapper<W> {

	@Override
	default TypedValue value() {
		return getSource().value();
	}

	@Override
	default T get() {
		return getSource().get();
	}
}