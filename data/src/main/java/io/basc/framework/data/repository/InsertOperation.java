package io.basc.framework.data.repository;

import io.basc.framework.util.Assert;
import io.basc.framework.util.collections.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InsertOperation extends Operation {
	private static final long serialVersionUID = 1L;
	private final Elements<? extends Expression> columns;
	private Elements<? extends Condition> conditions;

	public InsertOperation(Repository repository, Elements<? extends Expression> columns) {
		this(InsertOperationSymbol.INSERT, repository, columns);
	}

	public InsertOperation(InsertOperationSymbol operationSymbol, Repository repository,
			Elements<? extends Expression> columns) {
		super(operationSymbol, repository);
		Assert.requiredArgument(columns != null, "columns");
		this.columns = columns;
	}

	@Override
	public InsertOperationSymbol getOperationSymbol() {
		return (InsertOperationSymbol) super.getOperationSymbol();
	}
}
