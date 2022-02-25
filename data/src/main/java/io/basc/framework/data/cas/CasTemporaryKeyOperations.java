package io.basc.framework.data.cas;

import io.basc.framework.data.kv.TemporaryKeyOperations;

public interface CasTemporaryKeyOperations<K> extends CasKeyOperations<K>, TemporaryKeyOperations<K> {
}
