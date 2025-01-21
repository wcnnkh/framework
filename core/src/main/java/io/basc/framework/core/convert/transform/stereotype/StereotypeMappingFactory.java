package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import lombok.NonNull;

public interface StereotypeMappingFactory<SD extends StereotypeDescriptor, SM extends StereotypeMapping<? extends SD>> {
	SM getStereotypeMapping(@NonNull TypeDescriptor requiredType);
}
