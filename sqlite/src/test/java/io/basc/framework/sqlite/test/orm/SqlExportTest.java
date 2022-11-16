package io.basc.framework.sqlite.test.orm;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.env.Sys;
import io.basc.framework.microsoft.ExcelTemplate;
import io.basc.framework.orm.annotation.Entity;
import io.basc.framework.orm.annotation.PrimaryKey;
import io.basc.framework.orm.transfer.TransfColumn;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.sqlite.SQLiteDB;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.XUtils;

public class SqlExportTest {
	@Test
	public void test() throws IOException {
		SQLiteDB db = new SQLiteDB(Sys.getEnv().getWorkPath() + "/test.db");
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
		db.query(java.sql.ResultSet.class, sql).export((e) -> template.process(e.iterator(), file));
		List<SqlExportTestTable> list = db.query(SqlExportTestTable.class, sql).list();
		List<SqlExportTestTable> fileRecords = template.read(file, SqlExportTestTable.class).list();
		assertTrue(CollectionUtils.equals(list, fileRecords));
		file.delete();
	}

	@Entity
	public static class SqlExportTestTable {
		@PrimaryKey
		@TransfColumn
		public int a;
		@TransfColumn
		public String b;

		@Override
		public boolean equals(Object obj) {
			return ReflectionUtils.equals(this, obj);
		}

		@Override
		public String toString() {
			return ReflectionUtils.toString(this);
		}
	}
}
