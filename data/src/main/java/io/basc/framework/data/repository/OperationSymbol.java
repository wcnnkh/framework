package io.basc.framework.data.repository;

public class OperationSymbol extends RepositorySymbol {
	private static final long serialVersionUID = 1L;

	public static final OperationSymbol INSERT = new OperationSymbol("insert");

	public static final OperationSymbol DELETE = new OperationSymbol("delete");

	public static final OperationSymbol UPDATE = new OperationSymbol("update");

	public static final OperationSymbol SELECT = new OperationSymbol("select");

	public OperationSymbol(String name) {
		super(name);
	}
}
