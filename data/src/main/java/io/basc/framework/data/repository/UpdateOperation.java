package io.basc.framework.data.repository;

import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateOperation extends Operation {
	private static final long serialVersionUID = 1L;
	private Elements<? extends Column> values;

	public UpdateOperation() {
		super(OperationSymbol.UPDATE);
	}
}
