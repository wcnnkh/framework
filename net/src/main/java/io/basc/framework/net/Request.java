package io.basc.framework.net;

import java.net.URI;

/**
 * 一个请求的定义
 * 
 * @author shuchaowen
 *
 */
public interface Request extends Message {
	URI getURI();
}
