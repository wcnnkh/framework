package io.basc.framework.data.repository;

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

	public DeleteOperation() {
		this(DeleteOperationSymbol.DELETE);
	}

	public DeleteOperation(DeleteOperationSymbol deleteOperationSymbol) {
		super(deleteOperationSymbol);
	}
}
