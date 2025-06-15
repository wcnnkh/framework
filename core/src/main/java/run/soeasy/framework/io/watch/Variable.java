package run.soeasy.framework.io.watch;

import java.io.IOException;

/**
 * 标识这是一个变化量
 * 
 * @author soeasy.run
 */
public interface Variable {
	/**
	 * 最后一次修改标识
	 * 
	 * @return 返回空表示未知
	 */
	long lastModified() throws IOException;
}
