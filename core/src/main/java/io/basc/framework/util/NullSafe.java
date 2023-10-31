package io.basc.framework.util;

import java.io.Serializable;

import lombok.Data;

@Data
public class NullSafe<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private final T value;
}
