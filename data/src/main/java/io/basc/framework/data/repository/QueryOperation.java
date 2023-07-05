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
	 * 查询的列
	 */
	private Elements<? extends Expression> columns;

	/**
	 * 查询条件
	 */
	private Elements<? extends Condition> conditions;

	/**
	 * 排序
	 */
	private Elements<? extends Sort> sorts;

	public QueryOperation(Repository repository) {
		this(QueryOperationSymbol.QUERY, repository);
	}

	public QueryOperation(QueryOperationSymbol queryOperationSymbol, Repository repository) {
		super(queryOperationSymbol, repository);
	}

}
