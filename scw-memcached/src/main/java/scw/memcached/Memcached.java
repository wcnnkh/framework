package scw.memcached;

import scw.data.DataOperations;
import scw.data.cas.CASOperations;

public interface Memcached extends DataOperations {
	CASOperations getCASOperations();
}
