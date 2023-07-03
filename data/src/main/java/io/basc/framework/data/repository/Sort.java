package io.basc.framework.data.repository;

import java.io.Serializable;

import io.basc.framework.util.Elements;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 排序
 * 
 * @author wcnnkh
 *
 */
@Data
@AllArgsConstructor
public class Sort implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Expression expression;
	private final SortSymbol sortSymbol;
	private final Elements<? extends Sort> withSorts;

	public Sort(Column column, SortSymbol sortSymbol) {
		this(new Expression(column), sortSymbol);
	}

	public Sort(Expression expression, SortSymbol sortSymbol) {
		this(expression, sortSymbol, null);
	}
}
