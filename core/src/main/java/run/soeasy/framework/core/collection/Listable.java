package run.soeasy.framework.core.collection;

/**
 * 可列出的
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public interface Listable<E> {
	/**
	 * 列出所有元素
	 * 
	 * @return
	 */
	Elements<E> getElements();

	/**
	 * 是否存在元素
	 * 
	 * @return
	 */
	default boolean hasElements() {
		return getElements().isEmpty();
	}
}
