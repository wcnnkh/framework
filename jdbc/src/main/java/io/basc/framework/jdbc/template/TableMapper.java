package io.basc.framework.jdbc.template;

import io.basc.framework.jdbc.template.config.TableResolver;
import io.basc.framework.orm.EntityMapper;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.Property;

public interface TableMapper extends TableResolver, EntityMapper {

	@Override
	default TableMapping<? extends Column> getMapping(Class<?> entityClass) {
		EntityMapping<? extends Property> mapping = EntityMapper.super.getMapping(entityClass);
		return new DefaultTableMapping<>(mapping, (property) -> new DefaultColumn(property, entityClass, this),
				entityClass, this);
	}
}
