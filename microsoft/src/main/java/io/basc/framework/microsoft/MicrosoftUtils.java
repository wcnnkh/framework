package io.basc.framework.microsoft;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.Nullable;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;

public final class MicrosoftUtils {
	private static Logger logger = LoggerFactory.getLogger(MicrosoftUtils.class);

	private MicrosoftUtils() {
	};

	private static final ExcelOperations EXCEL_OPERATIONS = Sys.getEnv().getServiceLoader(ExcelOperations.class,
			"io.basc.framework.microsoft.poi.PoiExcelOperations", "io.basc.framework.microsoft.jxl.JxlExcelOperations")
			.first();

	static {
		if (EXCEL_OPERATIONS == null) {
			logger.error("not found excel support");
		} else {
			logger.info("using excel operations {}", EXCEL_OPERATIONS.getClass());
		}
	}

	public static ExcelOperations getExcelOperations() {
		if (EXCEL_OPERATIONS == null) {
			throw new UnsupportedException("excel operations");
		}
		return EXCEL_OPERATIONS;
	}

	public static ExcelMapper export(Object target) throws ExcelException, IOException {
		return export(target, null);
	}

	public static ExcelMapper export(Object target, @Nullable ExcelVersion version) throws ExcelException, IOException {
		Assert.requiredArgument(target != null, "target");
		ExcelExport export;
		if (target instanceof OutputStream) {
			export = getExcelOperations().createExport((OutputStream) target, version);
		} else if (target instanceof File) {
			export = getExcelOperations().createExport((File) target);
		} else {
			throw new UnsupportedException(target.toString());
		}
		return new ExcelMapper(export);
	}

	public static Elements<String[]> read(File source) throws IOException, ExcelException {
		ExcelTemplate reader = new ExcelTemplate();
		return reader.read(source, TypeDescriptor.valueOf(String[].class));
	}
}
