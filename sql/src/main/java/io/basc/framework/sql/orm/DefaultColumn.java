package io.basc.framework.sql.orm;

import io.basc.framework.mapper.Field;
import io.basc.framework.orm.DefaultProperty;
import io.basc.framework.orm.EntityMappingResolver;
import io.basc.framework.util.Elements;

public class DefaultColumn extends DefaultProperty implements Column {
	private Elements<IndexInfo> indexs;

	public DefaultColumn(Field field) {
		super(field);
	}

	public DefaultColumn(Field field, Class<?> sourceClass, EntityMappingResolver resolver,
			TableResolver tableResolver) {
		super(field, sourceClass, resolver);
		this.indexs = field.getSetters().map((e) -> tableResolver.getIndexs(sourceClass, e)).first();
	}

	@Override
	public Elements<IndexInfo> getIndexs() {
		return indexs == null ? Elements.empty() : indexs;
	}
}
