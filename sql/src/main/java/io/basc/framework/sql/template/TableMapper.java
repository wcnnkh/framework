package io.basc.framework.sql.template;

import io.basc.framework.orm.EntityMapper;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.Property;
import io.basc.framework.sql.template.config.TableResolver;

public interface TableMapper extends TableResolver, EntityMapper {

	@Override
	default TableMapping<? extends Column> getMapping(Class<?> entityClass) {
		EntityMapping<? extends Property> mapping = EntityMapper.super.getMapping(entityClass);
		return new DefaultTableMapping<>(mapping, (property) -> new DefaultColumn(property, entityClass, this),
				entityClass, this);
	}
}
