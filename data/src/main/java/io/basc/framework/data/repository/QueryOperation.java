package io.basc.framework.data.repository;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Range;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询操作
 * 
 * @author wcnnkh
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryOperation extends Operation {
	private static final long serialVersionUID = 1L;
	private final Elements<? extends Expression> columns;
	private Elements<? extends Condition> conditions;
	private Elements<? extends Sort> orders;
	private Range<Long> limit;

	public QueryOperation(Elements<? extends Expression> columns, Repository repository) {
		this(QueryOperationSymbol.QUERY, columns, repository);
	}

	public QueryOperation(QueryOperationSymbol operationSymbol, Elements<? extends Expression> columns,
			Repository repository) {
		super(operationSymbol, repository);
		Assert.requiredArgument(columns != null, "columns");
		this.columns = columns;
	}

	@Override
	public QueryOperationSymbol getOperationSymbol() {
		return (QueryOperationSymbol) super.getOperationSymbol();
	}
}
