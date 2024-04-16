package io.basc.framework.autoconfigure.stereotype;

import io.basc.framework.core.type.filter.AnnotationTypeFilter;

public class StereotypeTypeFilter extends AnnotationTypeFilter {

	public StereotypeTypeFilter() {
		super(Indexed.class);
	}
}
