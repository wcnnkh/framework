package io.basc.framework.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.junit.Test;

import run.soeasy.framework.util.io.FileUtils;
import run.soeasy.framework.util.sequences.uuid.UUIDSequences;

public class FosTest {
	@Test
	public void test() throws IOException {
		File file = File.createTempFile("test", "test");
		try {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 10; i++) {
				FileOutputStream fos = new FileOutputStream(file, true);
				try {
					OutputStreamWriter write = new OutputStreamWriter(fos);
					try {
						String content = UUIDSequences.global().next() + "\n";
						write.append(content);
						sb.append(content);
						write.flush();
					} finally {
						write.close();
					}
				} finally {
					fos.close();
				}
			}
			assertTrue(sb.toString().equals(FileUtils.readFileToString(file)));
		} finally {
			file.delete();
		}
	}
}
