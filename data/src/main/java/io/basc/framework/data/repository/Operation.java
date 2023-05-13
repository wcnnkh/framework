package io.basc.framework.data.repository;

import java.io.Serializable;

import io.basc.framework.util.Elements;
import lombok.Data;

/**
 * 对一个操作的定义
 * 
 * @author wcnnkh
 *
 */
@Data
public class Operation implements Serializable {
	private static final long serialVersionUID = 1L;
	private final OperationSymbol operationSymbol;

	/**
	 * 补操作的存储库
	 */
	private Elements<? extends Repository> repositorys;

	/**
	 * 条件
	 */
	private Elements<? extends Condition> conditions;

	public Operation(OperationSymbol operationSymbol) {
		this.operationSymbol = operationSymbol;
	}
}
