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
	private Elements<? extends Column> columns;

	public InsertOperation() {
		super(OperationSymbol.INSERT);
	}
}
