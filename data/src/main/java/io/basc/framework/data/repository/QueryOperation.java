package io.basc.framework.data.repository;

import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询
 * <p>
 * 
 * select {expressions} form {repositorys} where {conditions} order by {sorts}
 * 
 * @author wcnnkh
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryOperation extends Operation {
	private static final long serialVersionUID = 1L;
	/**
	 * 查询的表达式
	 */
	private Elements<? extends Expression> expressions;
	/**
	 * 排序
	 */
	private Elements<? extends Sort> sorts;

	public QueryOperation() {
		super(QueryOperationSymbol.QUERY);
	}

	public QueryOperation(QueryOperationSymbol selectOperationSymbol) {
		super(selectOperationSymbol);
	}

	public QueryOperation(Operation operation) {
		this(QueryOperationSymbol.QUERY, operation);
	}

	public QueryOperation(QueryOperationSymbol selectOperationSymbol, Operation operation) {
		super(selectOperationSymbol, operation);
	}

}
