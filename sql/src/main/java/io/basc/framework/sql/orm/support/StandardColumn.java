package io.basc.framework.sql.orm.support;

import java.util.ArrayList;
import java.util.Collection;

import io.basc.framework.orm.support.StandardProperty;
import io.basc.framework.sql.orm.Column;
import io.basc.framework.sql.orm.IndexInfo;

public class StandardColumn extends StandardProperty implements Column {
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
