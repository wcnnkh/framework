package scw.servlet.view.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import scw.core.utils.StringUtils;
import scw.json.JSONUtils;
import scw.servlet.view.AbstractTextView;
import scw.servlet.view.common.enums.ResultCode;
import scw.transaction.RollbackOnlyResult;

public class Result extends AbstractTextView implements Serializable, RollbackOnlyResult {

	private static final long serialVersionUID = 1L;
	private int code;
	private String msg;

	public int getCode() {
		if (code == 0) {
			return StringUtils.isEmpty(getMsg()) ? 0 : ResultCode.error.getCode();
		}

		return code;
	}

	public Result setCode(int code) {
		this.code = code;
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public Result setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public boolean isSuccess() {
		return getCode() == ResultCode.success.getCode();
	}

	public boolean isError() {
		return getCode() != ResultCode.success.getCode();
	}

	public Result setCode(ResultCode code) {
		this.code = code.getCode();
		this.msg = code.getMsg();
		return this;
	}

	public Result setCode(int code, String msg) {
		this.code = code;
		this.msg = msg;
		return this;
	}

	public static Result success() {
		return new Result();
	}

	public static Result error(String msg) {
		return new Result().setMsg(msg);
	}

	public static Result loginExpired() {
		return new Result().setCode(ResultCode.login_status_expired);
	}

	public static Result parameterError() {
		return new Result().setCode(ResultCode.parameter_error);
	}

	public static Result result(Result result) {
		Result r = new Result();
		r.setCode(result.getCode());
		r.setMsg(result.getMsg());
		return r;
	}

	@Override
	public String getResponseText() {
		Map<String, Object> result = new HashMap<String, Object>(2, 1);
		result.put("code", getCode());
		result.put("msg", getMsg());
		return JSONUtils.toJSONString(result);
	}

	public boolean isRollbackOnly() {
		return isError();
	}
}
