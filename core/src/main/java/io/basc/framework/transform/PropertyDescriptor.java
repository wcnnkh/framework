package io.basc.framework.transform;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.alias.Named;

public interface PropertyDescriptor extends Named {
	TypeDescriptor getTypeDescriptor();
}
