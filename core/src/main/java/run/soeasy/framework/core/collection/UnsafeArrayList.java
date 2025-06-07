package run.soeasy.framework.core.collection;

import java.util.AbstractList;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 数据不安全的ArrayList， 不可以进行add操作, {@link #toArray()}不进行任何拷贝
 * 
 * @author soeasy.run
 *
 * @param <E>
 */
@RequiredArgsConstructor
public class UnsafeArrayList<E> extends AbstractList<E> {
	@NonNull
	private final E[] array;

	@Override
	public E get(int index) {
		return array[index];
	}

	@Override
	public E set(int index, E element) {
		E old = array[index];
		array[index] = element;
		return old;
	}

	@Override
	public int size() {
		return array.length;
	}

	@Override
	public Object[] toArray() {
		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if (array.length == 0) {
			return a;
		}

		if (array.length > a.length) {
			if (a.getClass().getComponentType() == array.getClass().getComponentType()) {
				return (T[]) array;
			}
		}
		return super.toArray(a);
	}
}