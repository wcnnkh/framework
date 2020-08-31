package scw.io.support;

import java.io.File;

import scw.core.utils.StringUtils;
import scw.core.utils.XUtils;
import scw.io.FileUtils;

/**
 * 这是一个临时文件，在系统回收时会自动删除，但推荐使用结束后删除
 * 
 * @author shuchaowen
 *
 */
public class TemporaryFile extends File {
	private static final long serialVersionUID = 1L;

	public TemporaryFile(String filename) {
		super(FileUtils.getTempDirectoryPath() + File.separator + XUtils.getUUID()
				+ (StringUtils.isEmpty(filename) ? "" : (File.separator + filename)));
		deleteOnExit();
	}

	@Override
	protected void finalize() throws Throwable {
		delete();
		super.finalize();
	}
}
