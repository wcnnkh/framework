package io.basc.framework.microsoft.annotation;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.orm.EntityStructure;
import io.basc.framework.orm.Property;
import io.basc.framework.orm.support.DefaultObjectRelationalMapping;
import io.basc.framework.util.StringUtils;

public class ExcelResolver extends DefaultObjectRelationalMapping {

	@Override
	public Boolean isIgnore(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		return !fieldDescriptor.isAnnotationPresent(ExcelColumn.class);
	}

	@Override
	public Collection<String> getAliasNames(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		ExcelColumn excelColumn = fieldDescriptor.getAnnotation(ExcelColumn.class);
		if (excelColumn == null) {
			return super.getAliasNames(entityClass, fieldDescriptor);
		}

		String[] alias = excelColumn.alias();
		if (alias == null || alias.length == 0) {
			return StringUtils.isEmpty(excelColumn.value()) ? super.getAliasNames(entityClass, fieldDescriptor)
					: Arrays.asList(excelColumn.value());
		}
		return Arrays.asList(alias);
	}

	@Override
	public String getName(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		ExcelColumn excelColumn = fieldDescriptor.getAnnotation(ExcelColumn.class);
		if (excelColumn == null || StringUtils.isEmpty(excelColumn.value())) {
			return super.getName(entityClass, fieldDescriptor);
		}
		return excelColumn.value();
	}
}
