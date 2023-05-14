package io.basc.framework.sql.orm;

import io.basc.framework.orm.DefaultProperty;
import io.basc.framework.orm.Property;
import io.basc.framework.util.Elements;

public class DefaultColumn extends DefaultProperty implements Column {
	private Elements<IndexInfo> indexs;

	public DefaultColumn(Property property) {
		super(property);
	}

	public DefaultColumn(Column column) {
		super(column);
		this.indexs = column.getIndexs();
	}

	public DefaultColumn(Property property, Class<?> sourceClass, TableResolver tableResolver) {
		super(property);
		this.indexs = property.getSetters().map((e) -> tableResolver.getIndexs(sourceClass, e)).first();
	}

	@Override
	public Elements<IndexInfo> getIndexs() {
		return indexs == null ? Elements.empty() : indexs;
	}
}
