package io.basc.framework.net;

import java.net.URI;

/**
 * 一个请求的定义
 * 
 * @author shuchaowen
 *
 */
public interface Request extends Message {
	public static interface RequestWrapper<W extends Request> extends Request, MessageWrapper<W> {
		@Override
		default URI getURI() {
			return getSource().getURI();
		}
	}

	URI getURI();
}
