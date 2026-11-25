package run.soeasy.framework.data.orm.annotation;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.streaming.Streamable;
import run.soeasy.framework.core.strings.StringUtils;
import run.soeasy.framework.data.orm.resolver.EntityResolver;

public class AnnotationEntityResolver implements EntityResolver {

	@Override
	public String getEntityName(@NonNull TypeDescriptor typeDescriptor) {
		Entity entity = typeDescriptor.getAnnotation(Entity.class);
		if (entity == null) {
			entity = typeDescriptor.getType().getAnnotation(Entity.class);
		}
		if (entity != null && StringUtils.isNotEmpty(entity.name())) {
			return entity.name();
		}
		return null;
	}

	@Override
	public Streamable<String> getEntityAlias(@NonNull TypeDescriptor typeDescriptor) {
		Entity entity = typeDescriptor.getAnnotation(Entity.class);
		if (entity == null) {
			entity = typeDescriptor.getType().getAnnotation(Entity.class);
		}
		if (entity != null && entity.alias().length > 0) {
			return Streamable.array(entity.alias());
		}
		return Streamable.empty();
	}

	@Override
	public boolean isPrimaryKey(@NonNull TypeDescriptor typeDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		Entity entity = typeDescriptor.getAnnotation(Entity.class);
		if (entity == null) {
			entity = typeDescriptor.getType().getAnnotation(Entity.class);
		}
		if (entity != null) {
			String[] columns = entity.primaryKeys();
			if (columns != null) {
				for (String name : columns) {
					if (propertyDescriptor.getName().equals(name)) {
						return true;
					}
				}
			}
		}
		return propertyDescriptor.getReturnTypeDescriptor().isAnnotationPresent(PrimaryKey.class);
	}

	@Override
	public String getColumnName(@NonNull PropertyDescriptor propertyDescriptor) {
		Column column = propertyDescriptor.getReturnTypeDescriptor().getAnnotation(Column.class);
		if (column != null && StringUtils.isNotEmpty(column.name())) {
			return column.name();
		}
		return null;
	}

	@Override
	public Streamable<String> getColumnAlias(@NonNull PropertyDescriptor propertyDescriptor) {
		Column column = propertyDescriptor.getReturnTypeDescriptor().getAnnotation(Column.class);
		if (column != null && column.alias().length > 0) {
			return Streamable.array(column.alias());
		}
		return Streamable.empty();
	}

	@Override
	public boolean isExclude(@NonNull TypeDescriptor typeDescriptor, @NonNull PropertyDescriptor propertyDescriptor) {
		Entity entity = typeDescriptor.getAnnotation(Entity.class);
		if (entity == null) {
			entity = typeDescriptor.getType().getAnnotation(Entity.class);
		}
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
