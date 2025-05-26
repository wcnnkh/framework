package run.soeasy.framework.core.domain;

import java.io.Serializable;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "element")
@AllArgsConstructor
public final class Sequential<E> implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 索引
	 */
	private final BigInteger index;
	/**
	 * 对应的元素
	 */
	private final E element;
}