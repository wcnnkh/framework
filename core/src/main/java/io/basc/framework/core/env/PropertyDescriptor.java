package io.basc.framework.core.env;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.alias.Named;

public interface PropertyDescriptor extends Named {
	TypeDescriptor getTypeDescriptor();
}
