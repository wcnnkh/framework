package io.basc.framework.data.repository;

public class InsertOperationSymbol extends OperationSymbol {
	private static final long serialVersionUID = 1L;

	public static final InsertOperationSymbol INSERT = new InsertOperationSymbol("insert");

	public InsertOperationSymbol(String name) {
		super(name);
	}

}
