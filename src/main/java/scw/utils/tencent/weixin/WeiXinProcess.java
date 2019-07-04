package scw.utils.tencent.weixin;

import scw.core.json.JSONObject;
import scw.core.json.JSONUtils;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.net.http.HttpUtils;
import scw.core.utils.StringUtils;

public abstract class WeiXinProcess {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

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
			body = JSONUtils.toJSONString(body);
		}

		String content = HttpUtils.postJson(url, null, body);
		if (StringUtils.isNull(content)) {
			throw new RuntimeException("请求错误：url=" + url + ", body=" + body);
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
		JSONObject json = JSONUtils.parseObject(content);
		this.errcode = json.getIntValue("errcode");
		this.errmsg = json.getString("errmsg");
		return json;
	}

	protected JSONObject get(String url) {
		String content = HttpUtils.doGet(url);
		if (StringUtils.isNull(content)) {
			throw new RuntimeException("请求错误:" + url);
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
