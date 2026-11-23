package run.soeasy.framework.data.orm.annotation;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.core.strings.StringUtils;
import run.soeasy.framework.data.orm.resolver.ColumnResolver;

public class AnnotationColumnResolver implements ColumnResolver {

	@Override
	public String getColumnName(@NonNull TypeDescriptor entityDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		Column column = propertyDescriptor.getReturnTypeDescriptor().getAnnotation(Column.class);
		if (column != null && StringUtils.isNotEmpty(column.name())) {
			return column.name();
		}
		return propertyDescriptor.getName();
	}

	@Override
	public Streamable<String> getColumnAlias(@NonNull TypeDescriptor entityDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		Column column = propertyDescriptor.getReturnTypeDescriptor().getAnnotation(Column.class);
		if (column != null && column.alias().length > 0) {
			return Streamable.array(column.name());
		}
		return Streamable.empty();
	}

	@Override
	public boolean isExclude(@NonNull TypeDescriptor entityDescriptor, @NonNull PropertyDescriptor propertyDescriptor) {
		Entity entity = entityDescriptor.getAnnotation(Entity.class);
		if (entity != null) {
			String[] columns = entity.exclude();
			if (columns != null) {
				for (String name : columns) {
					if (propertyDescriptor.getName().equals(name)) {
						return true;
					}
				}
			}
		}

		Column column = propertyDescriptor.getReturnTypeDescriptor().getAnnotation(Column.class);
		if (column != null) {
			return column.exclude();
		}
		return false;
	}

}
