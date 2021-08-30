package io.basc.framework.memcached;

import io.basc.framework.data.DataOperations;
import io.basc.framework.data.cas.CASOperations;

public interface Memcached extends DataOperations {
	CASOperations getCASOperations();
}
