package io.basc.framework.util.alias;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Name;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class SimpleNamed extends SimpleName implements Named {
	private Elements<String> aliasNames = Elements.empty();

	public SimpleNamed(Name name) {
		super(name);
	}

	public SimpleNamed(Named named) {
		this((Name) named);
		this.aliasNames = named.getAliasNames();
	}
}
