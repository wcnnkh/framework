package io.basc.framework.swagger.test;

import io.swagger.v3.oas.annotations.media.Schema;

public class ApiResponse {
	@Schema(description="错误码")
	private int code;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
