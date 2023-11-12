package io.basc.framework.util.element;

import java.io.Serializable;

import lombok.Data;

@Data
public class IterativeElement<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private final T value;
	private final boolean hashNext;

	public boolean hasNext() {
		return hashNext;
	}
}
