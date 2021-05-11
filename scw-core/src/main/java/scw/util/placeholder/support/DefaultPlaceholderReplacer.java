package scw.util.placeholder.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import scw.core.OrderComparator;
import scw.core.utils.CollectionUtils;
import scw.util.placeholder.ConfigurablePlaceholderReplacer;
import scw.util.placeholder.PlaceholderReplacer;
import scw.util.placeholder.PlaceholderResolver;

public class DefaultPlaceholderReplacer implements ConfigurablePlaceholderReplacer{
	private static final String DEFAULT_PREFIX = "{";
	private static final String DEFAULT_SUFFIX = "}";
	private static final PlaceholderReplacer DEFAULT_SIMPLE_REPLACER = new SimplePlaceholderReplaer(DEFAULT_PREFIX, DEFAULT_SUFFIX, true);
	private static final PlaceholderReplacer DEFAULT_SMART_REPLACER = new SmartPlaceholderReplacer(DEFAULT_PREFIX, DEFAULT_SUFFIX, true);	
	private volatile List<PlaceholderReplacer> placeholderReplacers;
	
	public void addPlaceholderReplacer(PlaceholderReplacer placeholderReplacer){
		if(placeholderReplacer == null){
			return ;
		}
		
		synchronized (this) {
			if(placeholderReplacers == null){
				placeholderReplacers = new ArrayList<PlaceholderReplacer>(8);
			}
			this.placeholderReplacers.add(placeholderReplacer);
			Collections.sort(placeholderReplacers, OrderComparator.INSTANCE.reversed());
		}
	}
	
	@Override
	public Iterator<PlaceholderReplacer> iterator() {
		if(placeholderReplacers == null){
			return Collections.emptyIterator();
		}
		return CollectionUtils.getIterator(placeholderReplacers, true);
	}
	
	public String replacePlaceholders(String value,
			PlaceholderResolver placeholderResolver) {
		String textToUse = value;
		for(PlaceholderReplacer replacer : this){
			textToUse = replacer.replacePlaceholders(textToUse, placeholderResolver);
		}
		
		textToUse = SimplePlaceholderReplaer.NON_STRICT_REPLACER.replacePlaceholders(textToUse, placeholderResolver);
		textToUse = SmartPlaceholderReplacer.NON_STRICT_REPLACER.replacePlaceholders(textToUse, placeholderResolver);
		textToUse = DEFAULT_SIMPLE_REPLACER.replacePlaceholders(textToUse, placeholderResolver);
		textToUse = DEFAULT_SMART_REPLACER.replacePlaceholders(textToUse, placeholderResolver);
		return textToUse;
	}
	
	public String replaceRequiredPlaceholders(String value, PlaceholderResolver placeholderResolver){
		String textToUse = value;
		for(PlaceholderReplacer replacer : this){
			textToUse = replacer.replacePlaceholders(textToUse, new RequiredPlaceholderResolver(textToUse, placeholderResolver));
		}
		
		textToUse = SimplePlaceholderReplaer.NON_STRICT_REPLACER.replacePlaceholders(textToUse, new RequiredPlaceholderResolver(textToUse, placeholderResolver));
		textToUse = SmartPlaceholderReplacer.NON_STRICT_REPLACER.replacePlaceholders(textToUse, new RequiredPlaceholderResolver(textToUse, placeholderResolver));
		textToUse = DEFAULT_SIMPLE_REPLACER.replacePlaceholders(textToUse, new RequiredPlaceholderResolver(textToUse, placeholderResolver));
		textToUse = DEFAULT_SMART_REPLACER.replacePlaceholders(textToUse, new RequiredPlaceholderResolver(textToUse, placeholderResolver));
		return textToUse;
	}
}
