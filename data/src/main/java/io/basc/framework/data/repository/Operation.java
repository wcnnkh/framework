package io.basc.framework.data.repository;

import java.io.Serializable;

import io.basc.framework.util.Assert;
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
public class Operation implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 操作符
	 */
	private final OperationSymbol operationSymbol;

	/**
	 * 补操作的存储库
	 */
	private final Repository repository;

	public Operation(Operation operation) {
		Assert.requiredArgument(operation != null, "operation");
		this.operationSymbol = operation.operationSymbol;
		this.repository = operation.repository;
	}
}
