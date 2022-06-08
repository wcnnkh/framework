package io.basc.framework.sql.orm;

import java.util.Collection;
import java.util.Collections;

import io.basc.framework.mapper.Field;
import io.basc.framework.orm.ObjectRelationalResolver;
import io.basc.framework.orm.Property;
import io.basc.framework.util.CollectionUtils;

public class Column extends Property {
	protected Collection<IndexInfo> indexs;

	public Column() {
	}

	public Column(Property property) {
		super(property);
	}

	public Column(Field property, ObjectRelationalResolver objectRelationalResolver) {
		super(property, objectRelationalResolver);
	}

	public Column(Column column) {
		super(column);
		this.indexs = column.indexs;
	}

	@Override
	public void setParent(Property parent) {
		if (parent instanceof Column) {
			setParent((Column) parent);
			return;
		}

		Column column = parent == null ? null : new Column(parent);
		setParent(column);
	}

	@Override
	public Column getParent() {
		Property property = super.getParent();
		if (property == null) {
			return null;
		}

		if (property instanceof Column) {
			return (Column) property;
		}
		return new Column(property);
	}

	public void setParent(Column column) {
		super.setParent(column);
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

	@Override
	public Column rename(String name) {
		Column column = clone();
		column.setName(name);
		return column;
	}
}
