package run.soeasy.framework.pdf;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import run.soeasy.framework.core.RandomUtils;
import run.soeasy.framework.io.FileUtils;

public class PDFTest {
	@Test
	public void split() throws IOException {
		File source = new ClassPathResource("pdf/test.pdf").getFile();
		List<File> files = PDF.build(source).split();
		FileUtils.deleteQuietly(files);
		System.out.println(files.size());
		assert files.size() == 3;
	}

	@Test
	public void merge() throws IOException {
		File source = new ClassPathResource("pdf/test.pdf").getFile();
		File target = File.createTempFile(RandomUtils.uuid(), "merge.pdf");
		PDF.build().addPages(source).addPages(source).save(target);
		List<File> targetPageFiles = PDF.build(target).split();
		FileUtils.deleteQuietly(targetPageFiles);
		target.delete();
		System.out.println(targetPageFiles.size());
		assert targetPageFiles.size() == 6;
	}
}
