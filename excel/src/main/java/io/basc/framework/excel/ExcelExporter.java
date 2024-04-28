package io.basc.framework.excel;

import java.io.IOException;
import java.util.Arrays;

import io.basc.framework.execution.Parameters;
import io.basc.framework.mapper.transfer.RowExporter;

public interface ExcelExporter extends RowExporter {

	default void put(Object... row) throws IOException, ExcelException {
		Parameters parameters = Parameters.forArgs(Arrays.asList(row));
		doWriteRecord(parameters);
	}
}
