package run.soeasy.framework.data.orm.resolver;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.mapping.property.PropertyDescriptor;

public interface PrimaryKeyResolver {
	boolean isPrimaryKey(@NonNull TypeDescriptor entityDescriptor, @NonNull PropertyDescriptor propertyDescriptor);
}
