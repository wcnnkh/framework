package io.basc.framework.data.template;

import io.basc.framework.data.object.ObjectOperationsWrapper;

public interface TemporaryObjectTemplateWrapper<K>
		extends TemporaryObjectTemplate<K>, TemporaryKeyTemplateWrapper<K>, ObjectOperationsWrapper<K> {
	@Override
	TemporaryObjectTemplate<K> getSourceOperations();
}
