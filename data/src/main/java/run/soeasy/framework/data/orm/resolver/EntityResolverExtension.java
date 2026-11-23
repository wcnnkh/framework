package run.soeasy.framework.data.orm.resolver;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;

public interface EntityResolverExtension {
	String getEntityName(@NonNull TypeDescriptor entityDescriptor, @NonNull EntityResolver chain);
}
