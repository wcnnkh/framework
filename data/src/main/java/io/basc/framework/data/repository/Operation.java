package io.basc.framework.data.repository;

import java.io.Serializable;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 操作
 * 
 * @author wcnnkh
 *
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Operation implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 操作符
	 */
	private final OperationSymbol operationSymbol;

	private Elements<? extends Expression> columns;

	/**
	 * 补操作的存储库
	 */
	private final Repository repository;

	/**
	 * 操作
	 */
	private Elements<? extends Condition> conditions;

	public Operation(Operation operation) {
		Assert.requiredArgument(operation != null, "operation");
		this.operationSymbol = operation.operationSymbol;
		this.columns = operation.columns;
		this.repository = operation.repository;
		this.conditions = operation.conditions;
	}

	/**
	 * 插入
	 * 
	 * @param insertOperationSymbol
	 * @param columns
	 * @param repository
	 */
	public Operation(InsertOperationSymbol insertOperationSymbol, Elements<? extends Expression> columns,
			Repository repository) {
		this(insertOperationSymbol, repository);
		Assert.requiredArgument(columns != null, "columns");
		this.columns = columns;
	}

	/**
	 * 删除
	 * 
	 * @param deleteOperationSymbol
	 * @param repository
	 * @param conditions
	 */
	public Operation(DeleteOperationSymbol deleteOperationSymbol, Repository repository,
			@Nullable Elements<? extends Condition> conditions) {
		this(deleteOperationSymbol, repository);
		this.conditions = conditions;
	}

	/**
	 * 更新
	 * 
	 * @param updateOperationSymbol
	 * @param repository
	 * @param columns
	 * @param conditions
	 */
	public Operation(UpdateOperationSymbol updateOperationSymbol, Repository repository,
			Elements<? extends Expression> columns, @Nullable Elements<? extends Condition> conditions) {
		this(updateOperationSymbol, repository);
		Assert.requiredArgument(columns != null, "columns");
		this.columns = columns;
		this.conditions = conditions;
	}
}
