package io.basc.framework.data.repository;

import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateOperation extends Operation {
	private static final long serialVersionUID = 1L;
	private final Elements<? extends Expression> columns;
	private Elements<? extends Condition> conditions;

	public UpdateOperation(Repository repository, Elements<? extends Expression> columns) {
		this(UpdateOperationSymbol.UPDATE, repository, columns);
	}

	public UpdateOperation(UpdateOperationSymbol operationSymbol, Repository repository,
			Elements<? extends Expression> columns) {
		super(operationSymbol, repository);
		Assert.requiredArgument(columns != null, "columns");
		this.columns = columns;
	}

	@Override
	public UpdateOperationSymbol getOperationSymbol() {
		return (UpdateOperationSymbol) super.getOperationSymbol();
	}
}
