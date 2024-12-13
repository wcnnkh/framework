package io.basc.framework.util.placeholder;

import lombok.NonNull;

public class DefaultPlaceholderReplacer extends PlaceholderReplacers {
	private volatile PropertyPlaceholderHelper notStrictReplacer = PropertyPlaceholderHelper.NON_STRICT_REPLACER;
	private volatile PropertyPlaceholderHelper strictReplacer = PropertyPlaceholderHelper.STRICT_REPLACER;

	@Override
	public String replacePlaceholders(String value, PlaceholderResolver placeholderResolver) {
		String text = super.replacePlaceholders(value, placeholderResolver);
		return notStrictReplacer.replacePlaceholders(text, placeholderResolver);
	}

	@Override
	public String replaceRequiredPlaceholders(String value, PlaceholderResolver placeholderResolver) {
		String text = super.replaceRequiredPlaceholders(value, placeholderResolver);
		return strictReplacer.replaceRequiredPlaceholders(text, placeholderResolver);
	}

	public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
		this.strictReplacer = this.strictReplacer.setIgnoreUnresolvablePlaceholders(ignoreUnresolvablePlaceholders);
		this.notStrictReplacer = this.notStrictReplacer
				.setIgnoreUnresolvablePlaceholders(ignoreUnresolvablePlaceholders);
	}

	public void setPlaceholderPrefix(@NonNull String placeholderPrefix) {
		this.strictReplacer = this.strictReplacer.setPlaceholderPrefix(placeholderPrefix);
		this.notStrictReplacer = this.notStrictReplacer.setPlaceholderPrefix(placeholderPrefix);
	}

	public void setPlaceholderSuffix(@NonNull String placeholderSuffix) {
		this.strictReplacer = this.strictReplacer.setPlaceholderSuffix(placeholderSuffix);
		this.notStrictReplacer = this.notStrictReplacer.setPlaceholderSuffix(placeholderSuffix);
	}

	public void setValueSeparator(String valueSeparator) {
		this.strictReplacer = this.strictReplacer.setValueSeparator(valueSeparator);
		this.notStrictReplacer = this.notStrictReplacer.setValueSeparator(valueSeparator);
	}

}
