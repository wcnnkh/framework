package run.soeasy.framework.web;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 忽略响应异常，直接返回ResponseEntity
 * 
 * @author soeasy.run
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IgnoreResponseErrorHandler implements ResponseErrorHandler {
	public static final ResponseErrorHandler INSTANCE = new IgnoreResponseErrorHandler();

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		// 始终返回false，表示没有错误，即不触发异常
		return false;
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		// 由于hasError返回false，此方法不会被调用
	}
}
