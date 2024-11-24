package io.basc.framework.mapper.io.table;

import io.basc.framework.core.execution.param.Parameter;
import io.basc.framework.util.Elements;
import io.basc.framework.util.SimpleItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SimpleRow extends SimpleItem implements Row {
	private final Column[] columns;

	@Override
	public Elements<Parameter> getElements() {
		return Elements.forArray(columns);
	}
}
