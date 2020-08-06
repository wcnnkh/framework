package scw.office.support;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import scw.core.IteratorCallback;
import scw.core.utils.ArrayUtils;
import scw.db.DB;
import scw.net.message.OutputMessage;
import scw.office.ExcelException;
import scw.office.ExcelExport;
import scw.office.OfficeUtils;
import scw.sql.Sql;
import scw.sql.orm.ResultMapping;

public class SqlExportUtils {
	private SqlExportUtils() {
	};

	public static void export(OutputMessage outputMessage, String fileName, String[] titles,
			final SqlExportRowMapping sqlExportRowMapping, DB db, Sql... sqls) throws ExcelException, IOException {
		export(OfficeUtils.createExcelExport(outputMessage, fileName), titles, sqlExportRowMapping, db,
				Arrays.asList(sqls));
	}

	public static void export(ExcelExport excelExport, String[] titles, SqlExportRowMapping sqlExportRowMapping, DB db,
			Sql... sqls) throws ExcelException, IOException {
		export(excelExport, titles, sqlExportRowMapping, db, Arrays.asList(sqls));
	}

	public static void export(final ExcelExport excelExport, String[] titles,
			final SqlExportRowMapping sqlExportRowMapping, DB db, Collection<Sql> sqls)
			throws ExcelException, IOException {
		if (!ArrayUtils.isEmpty(titles)) {
			excelExport.append(titles);
		}

		for (Sql sql : sqls) {
			db.iterator(sql, new IteratorCallback<ResultMapping>() {

				public boolean iteratorCallback(ResultMapping value) {
					String[] contents = sqlExportRowMapping.mapping(value);
					try {
						excelExport.append(contents);
					} catch (ExcelException e) {
						throw new ExcelException(e);
					} catch (IOException e) {
						throw new ExcelException(e);
					}
					return true;
				}
			});
			excelExport.flush();
		}
	}
}
