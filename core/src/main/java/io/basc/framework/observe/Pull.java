package io.basc.framework.observe;

/**
 * 拉模式
 * 
 * @author shuchaowen
 *
 */
public interface Pull {
	/**
	 * 最后一次修改标识
	 * 
	 * @return
	 */
	long lastModified();
}
