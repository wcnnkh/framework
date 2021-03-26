package scw.util;

import java.util.Arrays;

import scw.core.utils.ArrayUtils;

public class Accepts<E> implements Accept<E>{
	private Iterable<Accept<E>> iterable;

	public Accepts(Accept<E>... accepts) {
		this(ArrayUtils.isEmpty(accepts) ? null : Arrays.asList(accepts));
	}

	public Accepts(Iterable<Accept<E>> iterable) {
		this.iterable = iterable;
	}

	public boolean accept(E e) {
		for (Accept<E> accept : iterable) {
			if (accept == null) {
				continue;
			}

			if (!accept.accept(e)) {
				return false;
			}
		}
		return true;
	}
	
}
