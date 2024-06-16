package io.basc.framework.csv;

import org.apache.commons.csv.CSVRecord;

import io.basc.framework.execution.param.Parameter;
import io.basc.framework.execution.param.Parameters;
import io.basc.framework.mapper.io.template.Record;
import io.basc.framework.util.element.Elements;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class CsvRecord implements Record {
	@NonNull
	private final CSVRecord csvRecord;

	@Override
	public Elements<Parameter> getElements() {
		return Parameters.forArgs(csvRecord).getElements();
	}
}
