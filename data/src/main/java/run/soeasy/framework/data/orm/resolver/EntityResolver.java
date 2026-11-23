package run.soeasy.framework.data.orm.resolver;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.streaming.Streamable;

public interface EntityResolver {
	String getEntityName(@NonNull TypeDescriptor entityDescriptor);

	Streamable<String> getEntityAlias(@NonNull TypeDescriptor entityDescriptor);
}
