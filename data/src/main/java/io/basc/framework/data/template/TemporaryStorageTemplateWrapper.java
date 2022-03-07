package io.basc.framework.data.template;

import io.basc.framework.data.StorageOperationsWrapper;

public interface TemporaryStorageTemplateWrapper
		extends TemporaryStorageTemplate, TemporaryKeyValueTemplateWrapper<String, Object>,
		TemporaryObjectTemplateWrapper<String>, StorageOperationsWrapper {

	@Override
	TemporaryStorageTemplate getSourceOperations();
}
