package io.basc.framework.microsoft.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import io.basc.framework.convert.annotation.DateFormat;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.ExcelVersion;
import io.basc.framework.microsoft.MicrosoftUtils;
import io.basc.framework.orm.transfer.TransfColumn;
import io.basc.framework.util.RandomUtils;
import io.basc.framework.util.XUtils;

public class ExcelResolverTest {

	@Test
	public void test() throws ExcelException, IOException {
		List<TestBean> exportList = new ArrayList<TestBean>();
		for (int i = 0; i < 10; i++) {
			exportList.add(new TestBean(XUtils.getUUID(), RandomUtils.random(0, i), new Date()));
		}
		File file = File.createTempFile("export", ExcelVersion.XLSX.getFileSuffixName());

		MicrosoftUtils.export(file).putAll(exportList).close();

		List<TestBean> readList = MicrosoftUtils.read(file, TestBean.class).toList();
		assertTrue(exportList.equals(readList));
		file.delete();
	}

	public static class TestBean {
		@TransfColumn
		private String a;
		@TransfColumn
		private int b;
		@TransfColumn
		@DateFormat("yyyy-MM-dd HH:mm:ss,SSS")
		private Date c;

		public TestBean(String a, int b, Date c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}

		public String getA() {
			return a;
		}

		public void setA(String a) {
			this.a = a;
		}

		public int getB() {
			return b;
		}

		public void setB(int b) {
			this.b = b;
		}

		public Date getC() {
			return c;
		}

		public void setC(Date c) {
			this.c = c;
		}

		@Override
		public int hashCode() {
			return ReflectionUtils.hashCode(this);
		}

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
