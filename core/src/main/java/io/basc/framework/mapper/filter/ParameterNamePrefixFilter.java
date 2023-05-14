package io.basc.framework.mapper.filter;

public class ParameterNamePrefixFilter extends ParameterDescriptorFilter {
	private final String prefix;

	public ParameterNamePrefixFilter(String prefix) {
		this.prefix = prefix;
		getPredicateRegistry().and((e) -> e.getName().startsWith(prefix));
	}

	public String getPrefix() {
		return prefix;
	}
}
