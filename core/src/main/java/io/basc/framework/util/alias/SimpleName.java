package io.basc.framework.util.alias;

import io.basc.framework.util.Name;
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
