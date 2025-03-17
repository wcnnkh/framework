package run.soeasy.framework.net;

/**
 * 一个请求的定义
 * 
 * @author shuchaowen
 *
 */
public interface Request extends Message {
	public static interface RequestWrapper<W extends Request> extends Request, MessageWrapper<W> {
	}
}