package io.basc.framework.excel;

import io.basc.framework.util.Item;

public interface SheetContext extends Item {

	/**
	 * 当前的index
	 * 
	 * @return
	 */
	@Override
	int getPositionIndex();

	/**
	 * 名称
	 * 
	 * @return
	 */
	@Override
	String getName();
}
