package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.basc.framework.codec.support.CharsetCodec;
import io.basc.framework.io.FileRecords;
import io.basc.framework.util.XUtils;

public class FileRecordsTest {
	@Test
	public void test() throws IOException {
		FileRecords<String> records = new FileRecords<String>(CharsetCodec.UTF_8);
		try {
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < 10; i++) {
				StringBuilder sb = new StringBuilder();
				for (int a = 0; a < 10; a++) {
					sb.append(XUtils.getUUID());
				}
				list.add(sb.toString());
				records.append(list.get(i));
			}

			System.out.println(list);
			List<String> readList = records.iterator().list();
			System.out.println(readList);
			assertTrue(readList.equals(list));
		} finally {
			records.delete();
		}
	}
}
