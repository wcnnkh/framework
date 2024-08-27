package io.basc.framework.poi.ss;

import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import io.basc.framework.excel.SheetContext;
import io.basc.framework.excel.SheetRow;
import io.basc.framework.execution.param.Parameter;
import io.basc.framework.util.Elements;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PoiRow implements SheetRow {
	private final Row row;
	private final SheetContext sheetContext;

	@Override
	public Elements<Parameter> getElements() {
		return Elements.of(() -> IntStream.range(row.getFirstCellNum(), row.getLastCellNum()).mapToObj((i) -> {
			Cell cell = row.getCell(i);
			return new CellColumn(cell, this, sheetContext);
		}));
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public int getPositionIndex() {
		return row.getRowNum();
	}
}
