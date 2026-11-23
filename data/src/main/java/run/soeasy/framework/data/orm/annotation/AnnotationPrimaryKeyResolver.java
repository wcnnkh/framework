package run.soeasy.framework.data.orm.annotation;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.data.orm.resolver.PrimaryKeyResolver;

public class AnnotationPrimaryKeyResolver implements PrimaryKeyResolver {

	@Override
	public boolean isPrimaryKey(@NonNull TypeDescriptor entityDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor) {
		Entity entity = entityDescriptor.getAnnotation(Entity.class);
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

}
