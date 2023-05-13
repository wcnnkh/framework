package io.basc.framework.data.repository;

import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询
 * <p>
 * 
 * select {expressions} form {repositorys} where {conditions} order by {sorts}
 * 
 * @author wcnnkh
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SelectOperation extends Operation {
	private static final long serialVersionUID = 1L;
	/**
	 * 查询的表达式
	 */
	private Elements<? extends Expression> expressions;
	/**
	 * 排序
	 */
	private Elements<? extends Sort> sorts;

	public SelectOperation() {
		super(SelectOperationSymbol.SELECT);
	}

	public SelectOperation(SelectOperationSymbol selectOperationSymbol) {
		super(selectOperationSymbol);
	}

	public SelectOperation(Operation operation) {
		this(SelectOperationSymbol.SELECT, operation);
	}

	public SelectOperation(SelectOperationSymbol selectOperationSymbol, Operation operation) {
		super(selectOperationSymbol, operation);
	}

}
