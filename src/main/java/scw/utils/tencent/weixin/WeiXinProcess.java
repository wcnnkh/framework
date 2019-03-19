package scw.utils.tencent.weixin;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.http.HttpUtils;

public abstract class WeiXinProcess {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final Map<String, String> postRequestProperties = new HashMap<String, String>(2);
	static {
		postRequestProperties.put("Content-type", "application/json;charset=utf-8");
	}

	private int errcode;// 错误码
	private String errmsg;// 错误信息

	public int getErrcode() {
		return errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public boolean isSuccess() {
		return errcode == 0;
	}

	public boolean isError() {
		return errcode != 0;
	}

	protected JSONObject post(String url, Object data) {
		String body = null;
		if (data != null) {
			body = JSONObject.toJSONString(body);
		}

		String content = HttpUtils.doPost(url, postRequestProperties, body);
		if (StringUtils.isNull(content)) {
			throw new ShuChaoWenRuntimeException("请求错误：url=" + url + ", body=" + body);
		}

		JSONObject json = wrapper(content);
		if (!isSuccess()) {
			StringBuilder sb = new StringBuilder();
			sb.append("api:").append(url);
			sb.append(",body：").append(body);
			sb.append(",response:").append(content);
			logger.warn(sb.toString());
		}
		return json;
	}

	private JSONObject wrapper(String content) {
		JSONObject json = JSONObject.parseObject(content);
		this.errcode = json.getIntValue("errcode");
		this.errmsg = json.getString("errmsg");
		return json;
	}

	protected JSONObject get(String url) {
		String content = HttpUtils.doGet(url);
		if (StringUtils.isNull(content)) {
			throw new ShuChaoWenRuntimeException("请求错误:" + url);
		}

		JSONObject json = wrapper(content);
		if (!isSuccess()) {
			if (!isSuccess()) {
				StringBuilder sb = new StringBuilder();
				sb.append("api:").append(url);
				sb.append(",response:").append(content);
				logger.warn(sb.toString());
			}
		}
		return json;
	}
}
