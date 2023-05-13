package io.basc.framework.data.repository;

public class SelectOperationSymbol extends OperationSymbol {
	private static final long serialVersionUID = 1L;
	/**
	 * 查询
	 */
	public static final SelectOperationSymbol SELECT = new SelectOperationSymbol("select");

	public SelectOperationSymbol(String name) {
		super(name);
	}

}
