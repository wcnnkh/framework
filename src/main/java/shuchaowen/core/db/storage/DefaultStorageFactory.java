package shuchaowen.core.db.storage;

/**
 * 此方式不使用缓存
 * @author shuchaowen
 *
 */
public class DefaultStorageFactory implements StorageFactory{
	private static final Storage DEFAULT_STORAGE = new DefaultStorage();

	public Storage getStorage(Class<?> tableClass) {
		return DEFAULT_STORAGE;
	}
}
