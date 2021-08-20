package scw.validation.test.pojo;

import javax.validation.constraints.NotNull;

public class ValidatorPojo {
	@NotNull
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
