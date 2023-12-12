package io.basc.framework.observe;

import java.io.IOException;

/**
 * 变量
 * 
 * @author wcnnkh
 *
 */
public interface Variable {
	/**
	 * 最后一次修改标识
	 * 
	 * @return
	 */
	long lastModified() throws IOException;
}
