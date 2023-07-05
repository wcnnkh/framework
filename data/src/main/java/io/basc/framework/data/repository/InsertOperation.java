package io.basc.framework.data.repository;

import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 插入操作
 * 
 * @author wcnnkh
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InsertOperation extends Operation {
	private static final long serialVersionUID = 1L;
	private Elements<? extends Expression> columns;

	public InsertOperation(Repository repository, Elements<? extends Expression> columns) {
		this(InsertOperationSymbol.INSERT, repository, columns);
	}

	public InsertOperation(InsertOperationSymbol insertOperationSymbol, Repository repository,
			Elements<? extends Expression> columns) {
		super(insertOperationSymbol, repository);
		this.columns = columns;
	}
}
