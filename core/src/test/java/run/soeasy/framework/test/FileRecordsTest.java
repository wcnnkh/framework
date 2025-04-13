package run.soeasy.framework.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import run.soeasy.framework.codec.support.CharsetCodec;
import run.soeasy.framework.core.io.FileRecords;
import run.soeasy.framework.sequences.uuid.UUIDSequences;

public class FileRecordsTest {
	@Test
	public void test() throws IOException {
		File file = File.createTempFile(UUIDSequences.global().next(), UUIDSequences.global().next());
		FileRecords<String> records = new FileRecords<String>(file, CharsetCodec.UTF_8);
		try {
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < 10; i++) {
				StringBuilder sb = new StringBuilder();
				for (int a = 0; a < 10; a++) {
					sb.append(UUIDSequences.global().next());
				}
				list.add(sb.toString());
				records.append(list.get(i));
			}

			System.out.println(list);
			List<String> readList = records.toList();
			System.out.println(readList);
			assertTrue(readList.equals(list));
		} finally {
			records.delete();
		}
	}
}
