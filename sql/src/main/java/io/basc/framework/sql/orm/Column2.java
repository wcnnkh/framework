package io.basc.framework.sql.orm;

import java.util.Collection;
import java.util.Collections;

import io.basc.framework.mapper.Field;
import io.basc.framework.orm.EntityMappingResolver;
import io.basc.framework.orm.Property;
import io.basc.framework.util.CollectionUtils;

public class Column2 extends Property {
	protected Collection<IndexInfo> indexs;

	public Column2() {
	}

	public Column2(Property property) {
		super(property);
	}

	public Column2(Field property, EntityMappingResolver objectRelationalResolver) {
		super(property, objectRelationalResolver);
	}

	public Column2(Column2 column) {
		super(column);
		this.indexs = column.indexs;
	}

	@Override
	public void setParent(Property parent) {
		if (parent instanceof Column2) {
			setParent((Column2) parent);
			return;
		}

		Column2 column = parent == null ? null : new Column2(parent);
		setParent(column);
	}

	@Override
	public Column2 getParent() {
		Property property = super.getParent();
		if (property == null) {
			return null;
		}

		if (property instanceof Column2) {
			return (Column2) property;
		}
		return new Column2(property);
	}

	public void setParent(Column2 column) {
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
	public Column2 clone() {
		return new Column2(this);
	}

	@Override
	public Column2 rename(String name) {
		Column2 column = clone();
		column.setName(name);
		return column;
	}
}
