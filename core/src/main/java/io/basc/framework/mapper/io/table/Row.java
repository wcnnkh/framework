package io.basc.framework.mapper.io.table;

import io.basc.framework.mapper.io.template.Record;
import io.basc.framework.util.Item;

public interface Row extends Record, Item {
	/**
	 * 行索引
	 */
	@Override
	int getPositionIndex();
}
