package io.basc.framework.data;

import io.basc.framework.data.kv.TemporaryKeyValueOperations;
import io.basc.framework.data.object.TemporaryObjectOperations;

public interface TemporaryDataOperations<K>
		extends DataOperations<K>, TemporaryKeyValueOperations<K, Object>, TemporaryObjectOperations<K> {
}
