package io.basc.framework.data.repository;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 索引名和索引方法相同被视为同一组索引
 * 
 * @author wcnnkh
 *
 */
@Data
@EqualsAndHashCode(of = { "name", "method" })
@AllArgsConstructor
public class IndexInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String name;
	private final IndexType type;
	private final int length;
	private final IndexMethod method;
	private final SortOrder order;

	public IndexInfo(String name, String type, int length, String method, String order) {
		this(name, IndexType.forName(type), length, IndexMethod.forName(method), SortOrder.forName(order));
	}
}
