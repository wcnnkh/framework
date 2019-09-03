package scw.mvc;

/**
 * 请求响应模型
 * 
 * @author shuchaowen
 *
 */
public interface RequestResponseModelChannel<R extends Request, P extends Response> extends Channel, RequestResponseModel<R , P> {
}
