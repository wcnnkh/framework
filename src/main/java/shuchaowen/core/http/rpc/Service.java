package shuchaowen.core.http.rpc;

import java.io.InputStream;
import java.io.OutputStream;

public interface Service {
	/**
	 * 服务者
	 * @param serviceClass
	 */
	public void service(InputStream in, OutputStream os);
}
