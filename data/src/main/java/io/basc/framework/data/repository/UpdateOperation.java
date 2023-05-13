package io.basc.framework.data.repository;

import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateOperation extends Operation {
	private static final long serialVersionUID = 1L;
	private Elements<? extends Column> columns;

	public UpdateOperation() {
		super(UpdateOperationSymbol.UPDATE);
	}

	public UpdateOperation(UpdateOperationSymbol updateOperationSymbol) {
		super(updateOperationSymbol);
	}

	public UpdateOperation(UpdateOperationSymbol updateOperationSymbol, Operation operation) {
		super(updateOperationSymbol, operation);
	}

	public UpdateOperation(UpdateOperationSymbol updateOperationSymbol, InsertOperation insertOperation) {
		super(updateOperationSymbol);
		this.columns = insertOperation.getColumns();
	}
}
