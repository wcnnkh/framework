package io.basc.framework.data.repository;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Range;
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
	private Elements<? extends Sort> orders;
	private Range<Long> limit;

	public QueryOperation(Repository repository) {
		this(QueryOperationSymbol.QUERY, repository);
	}

	public QueryOperation(QueryOperationSymbol queryOperationSymbol, Repository repository) {
		super(queryOperationSymbol, repository);
	}
}
