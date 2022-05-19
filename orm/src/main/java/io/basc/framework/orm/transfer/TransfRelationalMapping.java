package io.basc.framework.orm.transfer;

import java.util.Arrays;
import java.util.Collection;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.orm.ObjectRelationalMapper;
import io.basc.framework.orm.support.DefaultObjectRelationalMapper;
import io.basc.framework.util.StringUtils;

public class TransfRelationalMapping extends DefaultObjectRelationalMapper {

	public static final ObjectRelationalMapper INSTANCE = new TransfRelationalMapping();

	@Override
	public Boolean isIgnore(Class<?> entityClass, ParameterDescriptor descriptor) {
		return !descriptor.isAnnotationPresent(TransfColumn.class);
	}

	@Override
	public Collection<String> getAliasNames(Class<?> entityClass, ParameterDescriptor descriptor) {
		TransfColumn excelColumn = descriptor.getAnnotation(TransfColumn.class);
		if (excelColumn == null) {
			return super.getAliasNames(entityClass, descriptor);
		}

		String[] alias = excelColumn.alias();
		if (alias == null || alias.length == 0) {
			return StringUtils.isEmpty(excelColumn.value()) ? super.getAliasNames(entityClass, descriptor)
					: Arrays.asList(excelColumn.value());
		}
		return Arrays.asList(alias);
	}

	@Override
	public String getName(Class<?> entityClass, ParameterDescriptor descriptor) {
		TransfColumn excelColumn = descriptor.getAnnotation(TransfColumn.class);
		if (excelColumn == null || StringUtils.isEmpty(excelColumn.value())) {
			return super.getName(entityClass, descriptor);
		}
		return excelColumn.value();
	}
}
