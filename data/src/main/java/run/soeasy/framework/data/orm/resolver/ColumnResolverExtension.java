package run.soeasy.framework.data.orm.resolver;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;
import run.soeasy.framework.core.streaming.Streamable;

public interface ColumnResolverExtension {
	String getColumnName(@NonNull TypeDescriptor entityDescriptor, @NonNull PropertyDescriptor propertyDescriptor, @NonNull ColumnResolver chain);

	Streamable<String> getColumnAlias(@NonNull TypeDescriptor entityDescriptor,
			@NonNull PropertyDescriptor propertyDescriptor, @NonNull ColumnResolver chain);

	boolean isExclude(@NonNull TypeDescriptor entityDescriptor, @NonNull PropertyDescriptor propertyDescriptor, @NonNull ColumnResolver chain);
}
