package io.basc.framework.excel;

import io.basc.framework.execution.param.Parameter;
import io.basc.framework.util.SimpleItem;
import io.basc.framework.util.element.Elements;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SheetColumns extends SimpleItem implements SheetRow {
	@NonNull
	private final SheetColumn[] columns;
	@NonNull
	private final SheetContext sheetContext;

	@Override
	public Elements<Parameter> getElements() {
		return Elements.forArray(columns);
	}
}
