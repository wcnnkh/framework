package scw.util.placeholder.support;

import java.util.LinkedList;

import scw.util.placeholder.ConfigurablePlaceholderReplacer;
import scw.util.placeholder.PlaceholderReplacer;
import scw.util.placeholder.PlaceholderResolver;

public class DefaultPlaceholderReplacer implements ConfigurablePlaceholderReplacer{
	private static final String DEFAULT_PREFIX = "{";
	private static final String DEFAULT_SUFFIX = "}";
	private static final PlaceholderReplacer DEFAULT_SIMPLE_REPLACER = new SimplePlaceholderReplaer(DEFAULT_PREFIX, DEFAULT_SUFFIX, true);
	private static final PlaceholderReplacer DEFAULT_SMART_REPLACER = new SmartPlaceholderReplacer(DEFAULT_PREFIX, DEFAULT_SUFFIX, true);	
	
	private final LinkedList<PlaceholderReplacer> placeholderReplacers = new LinkedList<PlaceholderReplacer>();
	
	public void addPlaceholderReplacer(PlaceholderReplacer placeholderReplacer){
		synchronized (placeholderReplacers) {
			this.placeholderReplacers.add(placeholderReplacer);
		}
	}
	
	public String replacePlaceholders(String value,
			PlaceholderResolver placeholderResolver) {
		String textToUse = value;
		for(PlaceholderReplacer placeholderReplacer : placeholderReplacers){
			textToUse = placeholderReplacer.replacePlaceholders(textToUse, placeholderResolver);
		}
		
		textToUse = SimplePlaceholderReplaer.NON_STRICT_REPLACER.replacePlaceholders(textToUse, placeholderResolver);
		textToUse = SmartPlaceholderReplacer.NON_STRICT_REPLACER.replacePlaceholders(textToUse, placeholderResolver);
		textToUse = DEFAULT_SIMPLE_REPLACER.replacePlaceholders(textToUse, placeholderResolver);
		textToUse = DEFAULT_SMART_REPLACER.replacePlaceholders(textToUse, placeholderResolver);
		return textToUse;
	}
	
	public String replaceRequiredPlaceholders(String value, PlaceholderResolver placeholderResolver){
		String textToUse = value;
		for(PlaceholderReplacer placeholderReplacer : placeholderReplacers){
			textToUse = placeholderReplacer.replacePlaceholders(textToUse, new RequiredPlaceholderResolver(textToUse, placeholderResolver));
		}
		
		textToUse = SimplePlaceholderReplaer.NON_STRICT_REPLACER.replacePlaceholders(textToUse, new RequiredPlaceholderResolver(textToUse, placeholderResolver));
		textToUse = SmartPlaceholderReplacer.NON_STRICT_REPLACER.replacePlaceholders(textToUse, new RequiredPlaceholderResolver(textToUse, placeholderResolver));
		textToUse = DEFAULT_SIMPLE_REPLACER.replacePlaceholders(textToUse, new RequiredPlaceholderResolver(textToUse, placeholderResolver));
		textToUse = DEFAULT_SMART_REPLACER.replacePlaceholders(textToUse, new RequiredPlaceholderResolver(textToUse, placeholderResolver));
		return textToUse;
	}
}
