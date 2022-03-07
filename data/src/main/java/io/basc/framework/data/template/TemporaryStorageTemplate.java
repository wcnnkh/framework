package io.basc.framework.data.template;

import io.basc.framework.data.StorageOperations;

public interface TemporaryStorageTemplate
		extends TemporaryKeyValueTemplate<String, Object>, TemporaryObjectTemplate<String>, StorageOperations {
}
