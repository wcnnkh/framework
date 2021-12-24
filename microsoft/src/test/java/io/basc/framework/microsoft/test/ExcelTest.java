package io.basc.framework.microsoft.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.basc.framework.io.ClassPathResource;
import io.basc.framework.io.Resource;
import io.basc.framework.microsoft.ExcelException;
import io.basc.framework.microsoft.MicrosoftUtils;
import io.basc.framework.microsoft.RowCallback;

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
		assertTrue(list.size() == 1);
	}
}
