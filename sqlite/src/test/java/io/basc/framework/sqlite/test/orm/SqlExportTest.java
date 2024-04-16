package io.basc.framework.sqlite.test.orm;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import io.basc.framework.jdbc.SimpleSql;
import io.basc.framework.jdbc.Sql;
import io.basc.framework.microsoft.ExcelTemplate;
import io.basc.framework.orm.stereotype.Entity;
import io.basc.framework.orm.stereotype.PrimaryKey;
import io.basc.framework.orm.transfer.TransfColumn;
import io.basc.framework.sqlite.SQLiteFileDatabase;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.XUtils;
import lombok.Data;

public class SqlExportTest {
	@Test
	public void test() throws IOException {
		SQLiteFileDatabase db = new SQLiteFileDatabase("test.db");
		db.createTable(SqlExportTestTable.class);

		for (int i = 0; i < 10; i++) {
			SqlExportTestTable table = new SqlExportTestTable();
			table.a = i;
			table.b = XUtils.getUUID();
			db.saveIfAbsent(table);
		}

		Sql sql = new SimpleSql("select * from sql_export_test_table");
		ExcelTemplate template = new ExcelTemplate();
		File file = File.createTempFile("export", "aaa.xls");
		db.query(sql, (e) -> e).getElements().transfer((e) -> template.write(e.iterator(), file));
		List<SqlExportTestTable> list = db.query(SqlExportTestTable.class, sql).getElements().toList();
		List<SqlExportTestTable> fileRecords = template.read(file, SqlExportTestTable.class).toList();
		assertTrue(CollectionUtils.equals(list, fileRecords));
		file.delete();
	}

	@Data
	@Entity
	public static class SqlExportTestTable {
		@PrimaryKey
		@TransfColumn
		public int a;
		@TransfColumn
		public String b;
	}
}
