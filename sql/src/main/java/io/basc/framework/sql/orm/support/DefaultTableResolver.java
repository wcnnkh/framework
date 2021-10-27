package io.basc.framework.sql.orm.support;

import io.basc.framework.data.domain.Range;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.sql.orm.IndexInfo;
import io.basc.framework.sql.orm.TableResolver;

public class DefaultTableResolver extends ConfigurableServices<TableResolver> implements TableResolver{

	@Override
	public Range<Double> getRange(Class<?> entityClass,
			FieldDescriptor descriptor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IndexInfo getIndex(Class<?> entityClass, FieldDescriptor descriptor) {
		// TODO Auto-generated method stub
		return null;
	}

}
