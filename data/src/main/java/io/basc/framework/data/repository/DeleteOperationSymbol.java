package io.basc.framework.data.repository;

public class DeleteOperationSymbol extends OperationSymbol {
	private static final long serialVersionUID = 1L;

	/**
	 * 删除
	 */
	public static final DeleteOperationSymbol DELETE = new DeleteOperationSymbol("delete");

	public DeleteOperationSymbol(String name) {
		super(name);
	}

}
