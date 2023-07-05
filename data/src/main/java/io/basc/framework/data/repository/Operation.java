package io.basc.framework.data.repository;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 对一个操作的定义
 * 
 * @author wcnnkh
 *
 */
@Data
@AllArgsConstructor
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
}
