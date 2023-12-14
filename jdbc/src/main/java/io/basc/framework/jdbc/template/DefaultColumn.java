package io.basc.framework.jdbc.template;

import io.basc.framework.jdbc.template.config.TableResolver;
import io.basc.framework.orm.DefaultProperty;
import io.basc.framework.orm.PropertyDescriptor;
import io.basc.framework.util.element.Elements;

public class DefaultColumn extends DefaultProperty implements Column {
	private Elements<IndexInfo> indexs;

	public DefaultColumn(PropertyDescriptor property) {
		super(property);
	}

	public DefaultColumn(Column column) {
		super(column);
		this.indexs = column.getIndexs();
	}

	public DefaultColumn(PropertyDescriptor property, Class<?> sourceClass, TableResolver tableResolver) {
		super(property);
		this.indexs = property.getSetters().map((e) -> tableResolver.getIndexs(sourceClass, e)).first();
	}

	@Override
	public Elements<IndexInfo> getIndexs() {
		return indexs == null ? Elements.empty() : indexs;
	}
}
