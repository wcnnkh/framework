package io.basc.framework.microsoft;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.orm.transfer.TableTransfer;
import io.basc.framework.orm.transfer.TransfColumns;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Assert;

public class ExcelTemplate extends TableTransfer {
	private ExcelOperations excelOperations;

	public ExcelTemplate() {
		this.excelOperations = MicrosoftUtils.getExcelOperations();
	}

	protected ExcelTemplate(ExcelTemplate source) {
		super(source);
		this.excelOperations = source.excelOperations;
	}

	public ExcelOperations getExcelOperations() {
		return excelOperations;
	}

	public void setExcelOperations(ExcelOperations excelOperations) {
		Assert.requiredArgument(excelOperations != null, "excelOperations");
		this.excelOperations = excelOperations;
	}

	@Override
	public void write(Iterator<? extends Object> source, OutputStream target) throws IOException {
		if (!source.hasNext()) {
			return;
		}

		ExcelExport export = getExcelOperations().createExport(target);
		try {
			exportAll(source, (e) -> export.put(e));
		} finally {
			export.close();
		}
	}

	@Override
	public void write(Iterator<? extends Object> source, Writer target) throws IOException {
		throw new UnsupportedException("write to Writer");
	}

	public final void putAll(Iterator<? extends Object> source, ExcelExport export) throws IOException {
		while (source.hasNext()) {
			put(source.next(), export);
		}
	}

	public final void put(Object source, ExcelExport export) throws IOException {
		TransfColumns<String, String> columns = mapColumns(source);
		if (isHeader() && columns.hasKeys()) {
			titles(export, columns.keys().collect(Collectors.toList()));
		}
		export.put(columns.values().collect(Collectors.toList()));
	}

	public final void titles(ExcelExport export, String... titles) throws ExcelException, IOException {
		if (ArrayUtils.isEmpty(titles)) {
			return;
		}

		titles(export, Arrays.asList(titles));
	}

	public void titles(ExcelExport export, List<String> titles) throws ExcelException, IOException {
		if (!export.isEmpty()) {
			return;
		}

		export.put(titles);
	}

	@Override
	public Stream<String[]> read(InputStream inputStream) throws IOException {
		return excelOperations.read(inputStream).map((e) -> e.getValues());
	}

	@Override
	public Stream<String[]> read(Reader reader) throws IOException {
		throw new UnsupportedException("read by Reader");
	}
}
