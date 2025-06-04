package run.soeasy.framework.core.convert.value;

public interface TypedDataAccessor<T> extends TypedData<T>, AccessibleDescriptor {
	void set(T value);
}
