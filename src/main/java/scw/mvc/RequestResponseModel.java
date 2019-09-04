package scw.mvc;

/**
 * 请求响应模型
 * 
 * @author shuchaowen
 *
 */
public interface RequestResponseModel<R extends Request, P extends Response> {
	<T extends R> T getRequest();

	<T extends P> T getResponse();
}
