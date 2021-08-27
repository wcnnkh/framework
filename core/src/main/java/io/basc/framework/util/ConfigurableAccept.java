package io.basc.framework.util;

import java.util.LinkedList;

public class ConfigurableAccept<E> extends LinkedList<Accept<E>> implements
		Accept<E> {
	private static final long serialVersionUID = 1L;

	public boolean accept(E e) {
		for (Accept<E> accept : this) {
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
