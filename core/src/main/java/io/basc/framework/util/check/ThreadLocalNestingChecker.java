package io.basc.framework.util.check;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.ListIterator;

import io.basc.framework.util.Assert;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.Registration;
import io.basc.framework.util.register.DisposableRegistration;

public class ThreadLocalNestingChecker<E> extends ThreadLocal<LinkedList<E>> implements NestingChecker<E> {

	private Comparator<E> comparator = (o1, o2) -> ObjectUtils.equals(o1, o2) ? 0 : 1;

	/**
	 * 0表示不检查
	 */
	private int inspectionDepth;

	/**
	 * 默认检查最后一个
	 */
	public ThreadLocalNestingChecker() {
		this(1);
	}

	/**
	 * @param inspectionDepth 0表示不检查 -1表示检查所有
	 */
	public ThreadLocalNestingChecker(int inspectionDepth) {
		this.inspectionDepth = inspectionDepth;
	}

	public Comparator<E> getComparator() {
		return comparator;
	}

	public void setComparator(Comparator<E> comparator) {
		Assert.requiredArgument(comparator != null, "comparator");
		this.comparator = comparator;
	}

	public int getInspectionDepth() {
		return inspectionDepth;
	}

	@Override
	public boolean isNestingExists(E element) {
		return isNestingExists(element, getInspectionDepth(), getComparator());
	}

	public boolean isNestingExists(E element, int inspectionDepth, Comparator<? super E> comparator) {
		if (element == null) {
			return false;
		}

		LinkedList<E> list = get();
		if (list == null) {
			return false;
		}

		int depth = 0;
		ListIterator<E> listIterator = list.listIterator(list.size());
		while (listIterator.hasPrevious()) {
			E e = listIterator.previous();
			if (inspectionDepth >= 0 && depth >= inspectionDepth) {
				return false;
			}

			if (comparator.compare(element, e) == 0) {
				return true;
			}
			depth++;
		}
		return false;
	}

	@Override
	public Registration registerNestedElement(E element) {
		if (element == null) {
			return Registration.CANCELLED;
		}

		LinkedList<E> list = get();
		if (list == null) {
			list = new LinkedList<E>();
			set(list);
		}
		list.add(element);
		return new DisposableRegistration(() -> stackOut(element));
	}

	public void setInspectionDepth(int inspectionDepth) {
		this.inspectionDepth = inspectionDepth;
	}

	public void stackOut(E element) {
		LinkedList<E> list = get();
		if (list == null) {
			throw new IllegalStateException("remove nesting element " + element);
		}

		E nesting = list.getLast();
		if (!ObjectUtils.equals(element, nesting)) {
			throw new IllegalStateException("remove nesting [" + nesting + "] conversion service [" + element + "]");
		}

		list.removeLast();
		if (list.isEmpty()) {
			remove();
		} else {
			set(list);
		}
	}

}
