package io.basc.framework.orm.transfer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import io.basc.framework.http.HttpOutputMessage;
import io.basc.framework.http.HttpUtils;
import io.basc.framework.io.FileUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

/**
 * 导出程序
 * 
 * @author wcnnkh
 *
 */
@FunctionalInterface
public interface Exporter {
	/**
	 * 导出
	 * 
	 * @param target
	 * @throws IOException
	 */
	void export(File target) throws IOException;

	default void export(HttpOutputMessage target, String fileName) throws IOException {
		export(target, fileName, null);
	}

	default void export(HttpOutputMessage target, String fileName, @Nullable Charset charset) throws IOException {
		Assert.requiredArgument(target != null, "target");
		Assert.requiredArgument(StringUtils.hasText(fileName), "fileName");
		File tempFile = File.createTempFile("export", fileName);
		try {
			export(tempFile);
			HttpUtils.writeFileMessageHeaders(target, fileName, charset);
			FileUtils.copyFile(tempFile, target.getOutputStream());
		} finally {
			tempFile.delete();
		}
	}
}
