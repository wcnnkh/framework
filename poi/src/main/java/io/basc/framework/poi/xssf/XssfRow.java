package io.basc.framework.poi.xssf;

import java.util.List;

import io.basc.framework.excel.SheetContext;
import io.basc.framework.excel.SheetRow;
import io.basc.framework.execution.param.Parameter;
import io.basc.framework.execution.param.Parameters;
import io.basc.framework.util.SimpleItem;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class XssfRow extends SimpleItem implements SheetRow {
	private final SheetContext sheetContext;
	private final List<String> list;

	@Override
	public Elements<Parameter> getElements() {
		return Parameters.forArgs(list).getElements();
	}
}
