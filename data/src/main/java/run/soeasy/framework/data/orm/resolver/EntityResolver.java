package run.soeasy.framework.data.orm.resolver;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.streaming.Streamable;

public interface EntityResolver {
	String getEntityName(@NonNull TypeDescriptor typeDescriptor);

	Streamable<String> getEntityAlias(@NonNull TypeDescriptor typeDescriptor);

	String getColumnName(@NonNull PropertyDescriptor propertyDescriptor);

	Streamable<String> getColumnAlias(@NonNull PropertyDescriptor propertyDescriptor);

	boolean isExclude(@NonNull TypeDescriptor typeDescriptor, @NonNull PropertyDescriptor propertyDescriptor);

	boolean isPrimaryKey(@NonNull TypeDescriptor typeDescriptor, @NonNull PropertyDescriptor propertyDescriptor);
}
