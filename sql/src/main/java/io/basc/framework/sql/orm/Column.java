package io.basc.framework.sql.orm;

import java.util.Collection;
import java.util.Collections;

import io.basc.framework.orm.Property;
import io.basc.framework.util.CollectionUtils;

public class Column extends Property {
	protected Collection<IndexInfo> indexs;

	public Column() {
	}

	public Column(Property property) {
		super(property);
	}

	public Column(Column column) {
		super(column);
		this.indexs = column.indexs;
	}

	public Collection<IndexInfo> getIndexs() {
		if (indexs == null && objectRelationalResolver != null) {
			if (objectRelationalResolver instanceof TableResolver) {
				// 考虑到一般的是get数据使用索引，所以优先使用getter
				if (isSupportGetter()) {
					return ((TableResolver) objectRelationalResolver).getIndexs(getDeclaringClass(), getGetter());
				}

				if (isSupportSetter()) {
					return ((TableResolver) objectRelationalResolver).getIndexs(getDeclaringClass(), getSetter());
				}
			}
		}
		return indexs == null ? Collections.emptyList() : Collections.unmodifiableCollection(indexs);
	}

	public boolean hasIndex() {
		return isPrimaryKey() || isUnique() || !CollectionUtils.isEmpty(getIndexs());
	}

	public void setIndexs(Collection<IndexInfo> indexs) {
		this.indexs = indexs;
	}

	@Override
	public Column clone() {
		return new Column(this);
	}
}
