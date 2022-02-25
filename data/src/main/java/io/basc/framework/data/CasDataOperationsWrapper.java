package io.basc.framework.data;

import io.basc.framework.data.cas.CasTemporaryKeyValueOperationsWrapper;

public interface CasDataOperationsWrapper
		extends CasDataOperations, CasTemporaryKeyValueOperationsWrapper<String, Object>, DataOperationsWrapper {

	@Override
	CasDataOperations getSourceOperations();
}
