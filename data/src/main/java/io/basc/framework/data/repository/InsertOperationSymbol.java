package io.basc.framework.data.repository;

public class InsertOperationSymbol extends OperationSymbol {
	private static final long serialVersionUID = 1L;

	public static final InsertOperationSymbol INSERT = new InsertOperationSymbol("insert");

	public static final InsertOperationSymbol INSERT_IF_ABSENT = new InsertOperationSymbol("insertIfAbsent");

	public static final InsertOperationSymbol INSERT_OR_UPDATE = new InsertOperationSymbol("insertOrUpdate");

	public InsertOperationSymbol(String name) {
		super(name);
	}

}
