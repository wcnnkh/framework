package run.soeasy.framework.util.placeholder;

/**
 * 占位符替换
 * 
 * @author wcnnkh
 *
 */
public interface PlaceholderReplacer {
	String replacePlaceholders(String source, PlaceholderResolver placeholderResolver);

	String replaceRequiredPlaceholders(String source, PlaceholderResolver placeholderResolver)
			throws IllegalArgumentException;
}
