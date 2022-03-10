package io.basc.framework.data;

import io.basc.framework.data.kv.TemporaryObjectCasOperations;

public interface TemporaryDataCasOperations
		extends DataCasOperations, TemporaryDataOperations, TemporaryObjectCasOperations<String> {
}
