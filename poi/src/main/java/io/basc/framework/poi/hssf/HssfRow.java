package io.basc.framework.poi.hssf;

import java.util.List;

import io.basc.framework.excel.SheetContext;
import io.basc.framework.excel.SheetRow;
import io.basc.framework.execution.param.Parameter;
import io.basc.framework.execution.param.Parameters;
import io.basc.framework.util.Elements;
import io.basc.framework.util.SimpleItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HssfRow extends SimpleItem implements SheetRow {
	private final List<String> columns;
	private SheetContext sheetContext;

	@Override
	public int getPositionIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Elements<Parameter> getElements() {
		return Parameters.forArgs(columns).getElements();
	}
}
