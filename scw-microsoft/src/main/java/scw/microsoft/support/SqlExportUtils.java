package scw.microsoft.support;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

import scw.core.utils.ArrayUtils;
import scw.db.DB;
import scw.http.HttpOutputMessage;
import scw.microsoft.ExcelException;
import scw.microsoft.ExcelExport;
import scw.microsoft.MicrosoftUtils;
import scw.sql.Sql;
import scw.util.stream.Processor;

public class SqlExportUtils {
	private SqlExportUtils() {
	};

	public static void export(HttpOutputMessage outputMessage, String fileName, String[] titles,
			Processor<ResultSet, String[], SQLException> sqlExportRowMapping, DB db, Sql... sqls) throws Exception {
		export(MicrosoftUtils.createExcelExport(outputMessage, fileName), titles, sqlExportRowMapping, db,
				Arrays.asList(sqls));
	}

	public static void export(ExcelExport excelExport, String[] titles,
			Processor<ResultSet, String[], SQLException> sqlExportRowMapping, DB db, Sql... sqls)
			throws ExcelException, IOException {
		export(excelExport, titles, sqlExportRowMapping, db, Arrays.asList(sqls));
	}

	public static void export(ExcelExport excelExport, String[] titles,
			Processor<ResultSet, String[], SQLException> sqlExportRowMapping, DB db, Collection<Sql> sqls)
			throws ExcelException, IOException {
		if (!ArrayUtils.isEmpty(titles)) {
			excelExport.append(titles);
		}

		for (Sql sql : sqls) {
			db.query(sql, sqlExportRowMapping).forEach((contents) -> {
				try {
					excelExport.append(contents);
				} catch (IOException e) {
					throw new ExcelException(e);
				}
			});
			excelExport.flush();
		}
	}
}
