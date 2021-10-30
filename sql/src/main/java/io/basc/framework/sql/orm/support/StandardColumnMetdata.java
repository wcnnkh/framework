package io.basc.framework.sql.orm.support;

import java.util.ArrayList;
import java.util.Collection;

import io.basc.framework.orm.support.StandardPropertyMetadata;
import io.basc.framework.sql.orm.ColumnMetadata;
import io.basc.framework.sql.orm.IndexInfo;

public class StandardColumnMetdata extends StandardPropertyMetadata implements ColumnMetadata {
	private Collection<IndexInfo> indexs;

	public Collection<IndexInfo> getIndexs() {
		if (indexs == null) {
			indexs = new ArrayList<>(4);
		}
		return indexs;
	}

	public void setIndexs(Collection<IndexInfo> indexs) {
		this.indexs = indexs;
	}
}
