package io.basc.framework.data.repository;

import io.basc.framework.util.collections.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeleteOperation extends Operation {
	private static final long serialVersionUID = 1L;
	private Elements<? extends Condition> conditions;

	public DeleteOperation(Repository repository) {
		this(DeleteOperationSymbol.DELETE, repository);
	}

	public DeleteOperation(DeleteOperationSymbol operationSymbol, Repository repository) {
		super(operationSymbol, repository);
	}

	@Override
	public DeleteOperationSymbol getOperationSymbol() {
		return (DeleteOperationSymbol) super.getOperationSymbol();
	}
}
