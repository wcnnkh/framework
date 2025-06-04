package run.soeasy.framework.core.collection;

import java.io.Serializable;

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
	private final long index;
	/**
	 * 对应的元素
	 */
	private final E element;

	/**
	 * 是否是最后一个
	 */
	private final boolean last;

	public boolean hasNext() {
		return !last;
	}
}