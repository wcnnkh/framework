package run.soeasy.framework.data.orm;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.streaming.Streamable;

public interface EntityResolverExtension {
	default String getEntityName(@NonNull TypeDescriptor typeDescriptor, @NonNull EntityResolver chain) {
		return chain.getEntityName(typeDescriptor);
	}

	default Streamable<String> getEntityAlias(@NonNull TypeDescriptor typeDescriptor, @NonNull EntityResolver chain) {
		return chain.getEntityAlias(typeDescriptor);
	}

	default boolean isPrimaryKey(@NonNull TypeDescriptor typeDescriptor, @NonNull PropertyDescriptor propertyDescriptor,
			@NonNull EntityResolver chain) {
		return chain.isPrimaryKey(typeDescriptor, propertyDescriptor);
	}

	default String getColumnName(@NonNull PropertyDescriptor propertyDescriptor, @NonNull EntityResolver chain) {
		return chain.getColumnName(propertyDescriptor);
	}

	default Streamable<String> getColumnAlias(@NonNull PropertyDescriptor propertyDescriptor,
			@NonNull EntityResolver chain) {
		return chain.getColumnAlias(propertyDescriptor);
	}

	default boolean isExclude(@NonNull TypeDescriptor typeDescriptor, @NonNull PropertyDescriptor propertyDescriptor,
			@NonNull EntityResolver chain) {
		return chain.isExclude(typeDescriptor, propertyDescriptor);
	}
}
