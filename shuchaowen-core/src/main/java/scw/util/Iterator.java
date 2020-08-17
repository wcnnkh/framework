package scw.util;

/**
 * 为了兼容java8以下
 * @author asus1
 *
 * @param <E>
 */
public abstract class Iterator<E> implements java.util.Iterator<E> {

	public void remove() {
		throw new UnsupportedOperationException("remove");
	}

}
