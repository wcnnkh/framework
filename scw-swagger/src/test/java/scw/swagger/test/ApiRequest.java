package scw.swagger.test;

import io.swagger.v3.oas.annotations.media.Schema;

public class ApiRequest{
	@Schema(description = "这是名称")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
