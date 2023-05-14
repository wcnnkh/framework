package io.basc.framework.util;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "element")
@AllArgsConstructor
public class Indexed<E> implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 索引，从0开始
	 */
	private final long index;
	/**
	 * 对应的元素
	 */
	private final E element;
}
