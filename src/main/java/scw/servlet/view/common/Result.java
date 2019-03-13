package scw.servlet.view.common;

import java.io.Serializable;

import scw.common.utils.StringUtils;
import scw.servlet.view.AbstractTextView;
import scw.servlet.view.common.enums.ResultCode;

import com.alibaba.fastjson.JSONObject;

public class Result extends AbstractTextView implements Serializable {
	private static final long serialVersionUID = 1L;
	private int code;
	private String msg;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
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
		JSONObject json = new JSONObject(4);
		String msg = getMsg();
		json.put("code", StringUtils.isEmpty(msg)? 0:getCode());
		json.put("msg", msg);
		return json.toJSONString();
	}
}
