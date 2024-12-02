package io.basc.framework.util.alias;

import io.basc.framework.util.Elements;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

public interface Title extends Named {

	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	@NoArgsConstructor
	public static class SimpleTitle extends SimpleNamed implements Title {
		private static final long serialVersionUID = 1L;
		private Elements<String> aliasNames = Elements.empty();

		public SimpleTitle(String name) {
			super(name);
		}

		public SimpleTitle(Title title) {
			super(title);
			this.aliasNames = title.getAliasNames();
		}

		@Override
		public Elements<String> getAliasNames() {
			return aliasNames == null ? Elements.empty() : aliasNames;
		}

		public void setAliasNames(Elements<String> aliasNames) {
			this.aliasNames = aliasNames;
		}
	}

	public static Title of(String name, Elements<String> aliasNames) {
		SimpleTitle title = new SimpleTitle(name);
		title.setAliasNames(aliasNames);
		return title;
	}

	Elements<String> getAliasNames();
}
