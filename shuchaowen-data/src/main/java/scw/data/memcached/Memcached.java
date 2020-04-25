package scw.data.memcached;

import scw.beans.annotation.Bean;
import scw.data.DataTemplete;
import scw.data.cas.CASOperations;

@Bean("memcached")
public interface Memcached extends DataTemplete {
	CASOperations getCASOperations();
}
