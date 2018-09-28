package shuchaowen.core.db.storage;

public class DefaultStorageFactory implements StorageFactory{
	private static final Storage DEFAULT_STORAGE = new DefaultStorage();

	public Storage getStorage(Class<?> tableClass) {
		return DEFAULT_STORAGE;
	}
}
