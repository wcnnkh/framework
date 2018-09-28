package shuchaowen.core.db.storage;

public interface StorageFactory {
	Storage getStorage(Class<?> tableClass);
}
