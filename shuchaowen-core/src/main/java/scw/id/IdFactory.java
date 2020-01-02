package scw.id;

/**
 * ID工厂
 * @author asus1
 *
 * @param <T>
 */
public interface IdFactory<T> {
	T generator(String name);
}
