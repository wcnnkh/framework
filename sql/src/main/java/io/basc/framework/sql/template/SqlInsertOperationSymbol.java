package io.basc.framework.sql.template;

import io.basc.framework.data.repository.InsertOperationSymbol;

public class SqlInsertOperationSymbol extends InsertOperationSymbol {
	private static final long serialVersionUID = 1L;
	public static final SqlInsertOperationSymbol SAVE_OR_UPDATE = new SqlInsertOperationSymbol("saveOrUpdate");
	public static final SqlInsertOperationSymbol SAVE_IF_ABSENT = new SqlInsertOperationSymbol("saveIfAbsent");
	
	public SqlInsertOperationSymbol(String name) {
		super(name);
	}
}
