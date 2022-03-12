package io.basc.framework.csv.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import io.basc.framework.csv.CsvTemplate;
import io.basc.framework.util.XUtils;

public class CsvTest {
	@Test
	public void test() throws IOException {
		CsvTemplate template = new CsvTemplate();

		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			list.add(XUtils.getUUID());
		}

		File file = File.createTempFile("test", ".csv");
		try {
			template.process(list, file);
			List<String> readList = template.read(file, String.class).collect(Collectors.toList());
			assertTrue(readList.equals(list));
		} finally {
			file.delete();
		}
	}
}
