package run.soeasy.framework.util.collections;

@FunctionalInterface
public interface CollectionFactory<T> {
	default T create() {
		return create(16);
	}

	default T create(int initialCapacity) {
		return create(initialCapacity, 0.75f);
	}

	T create(int initialCapacity, float loadFactor);
}
