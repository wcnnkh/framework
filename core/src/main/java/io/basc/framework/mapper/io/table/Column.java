package io.basc.framework.mapper.io.table;

import io.basc.framework.execution.param.Parameter;
import io.basc.framework.util.Item;

public interface Column extends Parameter {
	/**
	 * 列索引
	 */
	@Override
	int getPositionIndex();

	/**
	 * 所在行
	 * 
	 * @return
	 */
	Item getRow();
}
