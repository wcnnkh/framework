package run.soeasy.framework.core.convert.transform.stereotype;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Source;

public interface PropertyFactory {
	boolean hasProperty(@NonNull PropertyDescriptor propertyDescriptor);

	Source getProperty(@NonNull PropertyDescriptor propertyDescriptor);
}
