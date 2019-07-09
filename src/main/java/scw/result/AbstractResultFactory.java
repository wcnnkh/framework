package scw.result;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import scw.core.utils.ConfigUtils;
import scw.core.utils.PropertiesUtils;
import scw.core.utils.StringUtils;

public abstract class AbstractResultFactory implements ResultFactory {
	private Map<Integer, String> code2msgMap;

	public AbstractResultFactory(String propertiesFilePath, String charsetName) {
		if (StringUtils.isEmpty(propertiesFilePath)) {
			return;
		}

		Properties properties = ConfigUtils.getProperties(propertiesFilePath, charsetName);
		Map<String, String> map = PropertiesUtils.getProperties(properties);
		if (map != null) {
			for (Entry<String, String> entry : map.entrySet()) {
				int code = Integer.parseInt(entry.getKey());
				registerCode2Msg(code, entry.getValue());
			}
		}
	}

	public synchronized void registerCode2Msg(int code, String msg) {
		if (code2msgMap == null) {
			code2msgMap = new HashMap<Integer, String>();
		}
		code2msgMap.put(code, msg);
	}

	public String getMsg(int code) {
		return code2msgMap == null ? null : code2msgMap.get(code);
	}

	public abstract int getDefaultErrorCode();

	public abstract int getSuccessCode();

	public abstract int getAuthorizationFailureCode();

	public abstract int getParamterErrorCode();

	public <T> DataResult<T> success() {
		return success(null);
	}

	public <T> DataResult<T> error() {
		int code = getDefaultErrorCode();
		String msg = getMsg(code);
		return error(code, msg == null ? "系统错误" : msg);
	}

	public <T> DataResult<T> error(int code) {
		String msg = getMsg(code);
		return error(code, msg == null ? "操作失败" : msg);
	}

	public <T> DataResult<T> error(String msg) {
		return error(getDefaultErrorCode(), msg);
	}

	public <T> DataResult<T> authorizationFailure() {
		int code = getAuthorizationFailureCode();
		String msg = getMsg(code);
		return error(code, msg == null ? "登录状态已过期" : msg);
	}

	public <T> DataResult<T> parameterError() {
		int code = getParamterErrorCode();
		String msg = getMsg(code);
		return error(code, msg == null ? "参数错误" : msg);
	}

	public <T> DataResult<T> error(Result result) {
		return error(result.getCode(), result.getMsg());
	}
}
