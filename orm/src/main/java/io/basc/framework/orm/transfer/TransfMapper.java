package io.basc.framework.orm.transfer;

import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.orm.support.DefaultEntityMapper;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;

public class TransfMapper extends DefaultEntityMapper {

	public static final TransfMapper INSTANCE = new TransfMapper();

	@Override
	public boolean isIgnore(Class<?> entityClass, ParameterDescriptor descriptor) {
		return !descriptor.getTypeDescriptor().isAnnotationPresent(TransfColumn.class);
	}

	@Override
	public Elements<String> getAliasNames(Class<?> entityClass, ParameterDescriptor descriptor) {
		TransfColumn excelColumn = descriptor.getTypeDescriptor().getAnnotation(TransfColumn.class);
		if (excelColumn == null) {
			return super.getAliasNames(entityClass, descriptor);
		}

		String[] alias = excelColumn.alias();
		if (alias == null || alias.length == 0) {
			return StringUtils.isEmpty(excelColumn.value()) ? super.getAliasNames(entityClass, descriptor)
					: Elements.forArray(excelColumn.value());
		}
		return Elements.forArray(alias);
	}

	@Override
	public String getName(Class<?> entityClass, ParameterDescriptor descriptor) {
		TransfColumn excelColumn = descriptor.getTypeDescriptor().getAnnotation(TransfColumn.class);
		if (excelColumn == null || StringUtils.isEmpty(excelColumn.value())) {
			return super.getName(entityClass, descriptor);
		}
		return excelColumn.value();
	}
}
