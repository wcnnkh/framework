package io.basc.framework.orm.symbol;

import io.basc.framework.data.repository.DeleteOperationSymbol;

public class EntityDeleteOperationSymbol extends DeleteOperationSymbol {
	private static final long serialVersionUID = 1L;

	public static final EntityDeleteOperationSymbol DELETE_BY_PRIMARY_KEYS = new EntityDeleteOperationSymbol(
			"deleteByPrimaryKeys");

	public EntityDeleteOperationSymbol(String name) {
		super(name);
	}

}
