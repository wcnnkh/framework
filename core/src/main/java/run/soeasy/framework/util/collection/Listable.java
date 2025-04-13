package run.soeasy.framework.util.collection;

import run.soeasy.framework.lang.Wrapper;

/**
 * 可列出的
 * 
 * @author shuchaowen
 *
 * @param <E>
 */
public interface Listable<E> {
	public static interface ListableWrapper<E, W extends Listable<E>> extends Listable<E>, Wrapper<W> {
		@Override
		default Elements<E> getElements() {
			return getSource().getElements();
		}

		@Override
		default boolean hasElements() {
			return getSource().hasElements();
		}
	}

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
