package io.basc.framework.microsoft.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import io.basc.framework.convert.annotation.DateFormat;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.io.ClassPathResource;
import io.basc.framework.io.Resource;
import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.MicrosoftUtils;
import io.basc.framework.microsoft.RowCallback;
import io.basc.framework.microsoft.annotation.ExcelColumn;

public class ExcelTest {
	
	@Test
	public void test() throws ExcelException, IOException {
		Resource resource = new ClassPathResource("test.xlsx");
		List<String[]> list = new ArrayList<String[]>();
		MicrosoftUtils.getExcelOperations().read(resource.getInputStream(), new RowCallback() {

			@Override
			public void processRow(int sheetIndex, int rowIndex, String[] contents) {
				list.add(contents);
			}
		});
		assertTrue(list.size() == 2);

		MicrosoftUtils.read(TestBean.class, resource).forEach((e) -> {
			System.out.println(e);
		});
		
	}

	public static class TestBean {
		@ExcelColumn
		private String a;
		@ExcelColumn
		private int b;
		@ExcelColumn
		@DateFormat("yyyy-MM-dd")
		private Date c;

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
		public String toString() {
			return ReflectionUtils.toString(this);
		}
	}
}
