package io.basc.framework.data.repository;

import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 删除操作
 * 
 * @author wcnnkh
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DeleteOperation extends Operation {
	private static final long serialVersionUID = 1L;

	/**
	 * 条件
	 */
	private Elements<? extends Condition> conditions;

	public DeleteOperation(Repository repository) {
		this(DeleteOperationSymbol.DELETE, repository);
	}

	public DeleteOperation(DeleteOperationSymbol deleteOperationSymbol, Repository repository) {
		super(deleteOperationSymbol, repository);
	}
}
