package io.basc.framework.microsoft.support;

import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.convert.Converter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.microsoft.ExcelExport;
import io.basc.framework.microsoft.ExcelVersion;
import io.basc.framework.microsoft.MicrosoftUtils;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlOperations;
import io.basc.framework.sql.SqlUtils;
import io.basc.framework.util.Assert;

public class SqlExcelUtils {
	private SqlExcelUtils() {
	}

	public static void export(SqlOperations sqlOperations, Sql sql, Object target, @Nullable ExcelVersion version,
			Converter<Object, String> toString) throws IOException {
		Assert.requiredArgument(sqlOperations != null, "sqlOperations");
		Assert.requiredArgument(sql != null, "sql");
		Assert.requiredArgument(target != null, "target");
		Assert.requiredArgument(toString != null, "toString");
		AtomicBoolean first = new AtomicBoolean(false);
		ExcelExport excelExport = MicrosoftUtils.getExcelOperations().createExcelExport(target, version);
		try {
			sqlOperations.query(sql, (rs) -> {
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				if (count == 0) {
					return null;
				}

				if (first.compareAndSet(false, true)) {
					excelExport.append(SqlUtils.getColumnNames(rsmd, count));
				}

				Object[] values = SqlUtils.getRowValues(rs, count);
				String[] columns = toString.convert(values);
				excelExport.append(columns);
				return columns;
			});
		} finally {
			excelExport.close();
		}

	}
}
