package io.basc.framework.csv.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import io.basc.framework.csv.CSV;
import io.basc.framework.io.FileSystemResource;
import io.basc.framework.mapper.io.template.Record;
import io.basc.framework.util.XUtils;
import io.basc.framework.util.element.Elements;

public class CsvTest {
	@Test
	public void test() throws IOException {
		File file = File.createTempFile("test", ".csv");
		FileSystemResource fileSystemResource = new FileSystemResource(file);
		CSV csv = new CSV(fileSystemResource);
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 3; i++) {
			list.add(XUtils.getUUID());
		}

		System.out.println(list);
		try {
			csv.doWriteRecords(Elements.of(list).map(Arrays::asList).map(Record::forArgs));
			List<String> readList = csv.readAllRecords().map((e) -> e.getElements().first().getAsString()).toList();
			System.out.println(readList);
			assertTrue(readList.equals(list));
		} finally {
			file.delete();
		}
	}
}
