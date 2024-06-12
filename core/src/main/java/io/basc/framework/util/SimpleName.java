package io.basc.framework.util;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SimpleName implements Name {
	private String name;
	
	public SimpleName(Name name) {
		this.name = name.getName();
	}
}
