package io.basc.framework.sql.orm.support;

import io.basc.framework.orm.support.StandardEntityStructure;
import io.basc.framework.sql.orm.Column;
import io.basc.framework.sql.orm.TableStructure;

public class StandardTableStructure extends StandardEntityStructure<Column> implements TableStructure {
	private String engine;
	private String rowFormat;

	@Override
	public String getEngine() {
		return engine;
	}

	@Override
	public String getRowFormat() {
		return rowFormat;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	public void setRowFormat(String rowFormat) {
		this.rowFormat = rowFormat;
	}
}
