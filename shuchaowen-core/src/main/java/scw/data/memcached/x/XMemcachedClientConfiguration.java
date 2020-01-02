package scw.data.memcached.x;

import net.rubyeye.xmemcached.MemcachedClient;
import scw.beans.annotation.AutoImpl;

@AutoImpl({ PropertiesXMemcachedClientConfiguration.class, DefaultXMemcachedClientConfiguration.class })
public interface XMemcachedClientConfiguration {
	MemcachedClient configuration() throws Exception;
}
