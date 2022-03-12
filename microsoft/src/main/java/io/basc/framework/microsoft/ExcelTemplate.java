package io.basc.framework.microsoft;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.io.Resource;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.orm.transfer.TableTransfer;
import io.basc.framework.orm.transfer.TransfColumns;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.stream.Cursor;
import io.basc.framework.util.stream.StreamProcessorSupport;

public class ExcelTemplate extends TableTransfer {
	private ExcelOperations excelOperations;
	private long readStart = 0;
	private long readLimit = -1;

	public ExcelTemplate() {
		this.excelOperations = MicrosoftUtils.getExcelOperations();
	}

	protected ExcelTemplate(ExcelTemplate source) {
		super(source);
		this.excelOperations = source.excelOperations;
		this.readStart = source.readStart;
		this.readLimit = source.readLimit;
	}

	public ExcelOperations getExcelOperations() {
		return excelOperations;
	}

	public void setExcelOperations(ExcelOperations excelOperations) {
		Assert.requiredArgument(excelOperations != null, "excelOperations");
		this.excelOperations = excelOperations;
	}

	public long getReadStart() {
		return readStart;
	}

	public void setReadStart(long readStart) {
		this.readStart = readStart;
	}

	public long getReadLimit() {
		return readLimit;
	}

	public void setReadLimit(long readLimit) {
		this.readLimit = readLimit;
	}

	@Override
	public final void process(Iterator<? extends Object> source, File target) throws IOException {
		ExcelExport export = getExcelOperations().createExcelExport(target);
		try {
			exportAll(source, (e) -> export.put(e));
		} finally {
			export.close();
		}
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

	public Cursor<String[]> read(Object source) throws ExcelException, IOException {
		Assert.requiredArgument(source != null, "source");
		Stream<String[]> stream;
		if (source instanceof InputStream) {
			stream = excelOperations.read((InputStream) source);
		} else if (source instanceof File) {
			stream = excelOperations.read((File) source);
		} else if (source instanceof Resource) {
			stream = ((Resource) source).read((input) -> excelOperations.read(input));
		} else {
			throw new NotSupportedException(source.getClass().getName());
		}
		return StreamProcessorSupport.cursor(stream).limit(readStart, readLimit);
	}
}
