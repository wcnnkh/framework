package io.basc.framework.data.repository;

import lombok.Getter;

@Getter
public class InsertOperationSymbol extends OperationSymbol {
	private static final long serialVersionUID = 1L;

	public static final InsertOperationSymbol INSERT = new InsertOperationSymbol("insert", false);
	public static final InsertOperationSymbol SAVE_OR_UPDATE = new InsertOperationSymbol("saveOrUpdate", true);
	public static final InsertOperationSymbol SAVE_IF_ABSENT = new InsertOperationSymbol("saveIfAbsent", true);

	/**
	 * 包含条件
	 */
	private final boolean includeConditions;

	public InsertOperationSymbol(String name, boolean includeConditions) {
		super(name);
		this.includeConditions = includeConditions;
	}

}
