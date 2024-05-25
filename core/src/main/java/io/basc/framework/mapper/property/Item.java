package io.basc.framework.mapper.property;

public interface Item extends Named {

	/**
	 * 位置索引(从0开始)
	 * 
	 * @return 默认返回-1, 表示无索引位置
	 */
	default int getPositionIndex() {
		return -1;
	}
}
