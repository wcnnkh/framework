package io.basc.framework.data.repository;

public class UpdateOperationSymbol extends OperationSymbol {
	private static final long serialVersionUID = 1L;

	/**
	 * 更新
	 */
	public static final UpdateOperationSymbol UPDATE = new UpdateOperationSymbol("update");

	public UpdateOperationSymbol(String name) {
		super(name);
	}

}
