package io.basc.framework.util;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SimpleName implements Name {
	private String name;

	public SimpleName(String name) {
		this.name = name;
	}

	public SimpleName(Name name) {
		this.name = name.getName();
	}
}
