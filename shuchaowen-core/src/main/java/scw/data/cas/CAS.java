package scw.data.cas;

public interface CAS<T> {
	long getCas();

	T getValue();
}
