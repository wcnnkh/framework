package scw.http.server.ip;

import scw.core.GlobalPropertyFactory;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.http.server.ServerHttpRequest;

@Configuration(order = Integer.MIN_VALUE)
public class DefaultServerHttpRequestIpGetter implements ServerHttpRequestIpGetter {
	
	// 使用ip的模式 1表示使用第一个ip 2表示使用最后一个ip 其他表示原样返回
	private static final int USE_IP_MODEL = GlobalPropertyFactory.getInstance().getValue("server.http.ip.model",
			int.class, 1);
	private static final String[] IP_HEADERS = GlobalPropertyFactory.getInstance().getValue("mvc.ip.headers",
			String[].class, new String[] { "X-Real-Ip", "X-Forwarded-For" });

	public String getRequestIp(ServerHttpRequest request) {
		String ip = getUntreatedIp(request);
		if (USE_IP_MODEL == 1) {// 使用第一个
			String[] ipArray = StringUtils.commonSplit(ip);
			if (ArrayUtils.isEmpty(ipArray)) {
				return null;
			}

			return ipArray[0];
		} else if (USE_IP_MODEL == 2) {// 使用最后一个
			String[] ipArray = StringUtils.commonSplit(ip);
			if (ArrayUtils.isEmpty(ipArray)) {
				return null;
			}

			return ipArray[ipArray.length - 1];
		}
		return ip;
	}

	/**
	 * 获取未经处理的ip
	 * 
	 * @param headersReadOnly
	 * @param request
	 * @return
	 */
	public static String getUntreatedIp(ServerHttpRequest serverHttpRequest) {
		for (String header : IP_HEADERS) {
			String ip = serverHttpRequest.getHeaders().getFirst(header);
			if (ip == null) {
				continue;
			}

			return ip;
		}
		return serverHttpRequest.getRemoteAddress().getHostName();
	}
}
