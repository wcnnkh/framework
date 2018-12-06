package shuchaowen.core;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import shuchaowen.common.utils.ConfigUtils;
import shuchaowen.core.util.StringUtils;

public class DefaultFileFactory implements FileFactory{
	private static final String CONFIG_SUFFIX = "SHUCHAOWEN_CONFIG_SUFFIX";
	
	public File getFile(String path) {
		String configSuffix = ConfigUtils.getSystemProperty(CONFIG_SUFFIX);
		if (StringUtils.isNull(configSuffix)) {
			return getFile(path, null);
		} else {
			return getFile(path, Arrays.asList(StringUtils.commonSplit(configSuffix)));
		}
	}
	
	protected File getFile(String filePath, Collection<String> testSuffix) {
		File file = new File(getFilePath(filePath));
		if (testSuffix == null || testSuffix.isEmpty()) {
			return file;
		}

		for (String sf : testSuffix) {
			File testFile = new File(file.getParent() + File.separator + getTestFileName(file.getName(), sf));
			if (testFile.exists()) {
				return testFile;
			}
		}
		return file;
	}
	
	protected String getTestFileName(String fileName, String str) {
		int index = fileName.indexOf(".");
		if (index == -1) {// 不存在
			return fileName + str;
		} else {
			return fileName.substring(0, index) + str + fileName.substring(index);
		}
	}
	
	protected String getFilePath(final String filePath) {
		String path = ConfigUtils.format(filePath);
		File file = new File(path);
		if (file.exists()) {
			return path;
		} else {// 如果找不到目录就到classpath下去找
			String classPath = ConfigUtils.getClassPath() + path;
			file = new File(classPath);
			if (file.exists()) {
				return classPath;
			} else {// 如果clsspath下找不到就去workpath下去找
				String workPath = ConfigUtils.getWorkPath() + path;
				file = new File(workPath);
				if (file.exists()) {
					return workPath;
				} else {
					return path;
				}
			}
		}
	}
}
