package io.basc.framework.data.repository;

public class QueryOperationSymbol extends OperationSymbol {
	private static final long serialVersionUID = 1L;
	/**
	 * 查询
	 */
	public static final QueryOperationSymbol QUERY = new QueryOperationSymbol("query");

	public QueryOperationSymbol(String name) {
		super(name);
	}

}
