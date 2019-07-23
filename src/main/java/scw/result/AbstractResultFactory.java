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
	private final boolean defaultRollbackOnly;

	public AbstractResultFactory(boolean defaultRollbackOnly, String propertiesFilePath, String charsetName) {
		this.defaultRollbackOnly = defaultRollbackOnly;
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

	public <T> DataResult<T> success() {
		return success(null);
	}

	public <T> DataResult<T> error(int code, String msg, T data) {
		return error(code, msg, data, defaultRollbackOnly);
	}

	public <T> DataResult<T> error(int code, T data, boolean rollbackOnly) {
		String msg = getMsg(code);
		return error(code, msg == null ? "操作失败" : msg, data, rollbackOnly);
	}

	public <T> DataResult<T> error(int code, String msg) {
		return error(code, msg, null);
	}

	public <T> DataResult<T> error(int code, T data) {
		return error(code, data, true);
	}

	public <T> DataResult<T> error(int code) {
		String msg = getMsg(code);
		return error(code, msg == null ? "操作失败" : msg);
	}

	public <T> DataResult<T> error(String msg, T data, boolean rollbackOnly) {
		return error(getDefaultErrorCode(), msg, data, rollbackOnly);
	}

	public <T> DataResult<T> error(T data, boolean rollbackOnly) {
		return error(getDefaultErrorCode(), data, rollbackOnly);
	}

	public <T> DataResult<T> error(T data) {
		return error(data, true);
	}

	public <T> DataResult<T> error(String msg, T data) {
		return error(msg, data, true);
	}

	public <T> DataResult<T> error(String msg) {
		return error(getDefaultErrorCode(), msg);
	}

	public <T> DataResult<T> error() {
		int code = getDefaultErrorCode();
		String msg = getMsg(code);
		return error(code, msg == null ? "系统错误" : msg);
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
		return error(result.getCode(), result.getMsg(), null, result.isRollbackOnly());
	}
}
