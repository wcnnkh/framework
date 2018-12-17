package shuchaowen.web.servlet.view.common;

import java.io.Serializable;

import shuchaowen.web.servlet.view.AbstractTextView;
import shuchaowen.web.servlet.view.common.enums.Code;

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
		if (isSuccess()) {
			this.code = Code.error.getCode();
		}
		this.msg = msg;
		return this;
	}

	public boolean isSuccess() {
		return getCode() == Code.success.getCode();
	}

	public boolean isError() {
		return getCode() != Code.success.getCode();
	}

	public Result setCode(Code code) {
		this.code = code.getCode();
		this.msg = code.getMsg();
		return this;
	}

	public Result setCode(Code code, String msg) {
		this.code = code.getCode();
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
		return new Result().setCode(Code.login_status_expired);
	}

	@Override
	public String getResponseText() {
		JSONObject json = new JSONObject(4);
		json.put("code", getCode());
		json.put("msg", getMsg());
		return json.toJSONString();
	}
}
