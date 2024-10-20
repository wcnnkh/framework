package io.basc.framework.io.support;

import java.util.Locale;

import io.basc.framework.io.DefaultResourceLoader;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;

/**
 * Helper class for loading a localized resource, specified through name,
 * extension and current locale.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/io/support/LocalizedResourceHelper.java
 */
public class LocalizedResourceHelper {

	/** The default separator to use in-between file name parts: an underscore. */
	public static final String DEFAULT_SEPARATOR = "_";

	private final ResourceLoader resourceLoader;

	private String separator = DEFAULT_SEPARATOR;

	/**
	 * Create a new LocalizedResourceHelper with a DefaultResourceLoader.
	 * 
	 * @see io.basc.framework.io.DefaultResourceLoader
	 */
	public LocalizedResourceHelper() {
		this.resourceLoader = new DefaultResourceLoader();
	}

	/**
	 * Create a new LocalizedResourceHelper with the given ResourceLoader.
	 * 
	 * @param resourceLoader the ResourceLoader to use
	 */
	public LocalizedResourceHelper(ResourceLoader resourceLoader) {
		Assert.notNull(resourceLoader, "ResourceLoader must not be null");
		this.resourceLoader = resourceLoader;
	}

	/**
	 * Set the separator to use in-between file name parts. Default is an underscore
	 * ("_").
	 */
	public void setSeparator(@Nullable String separator) {
		this.separator = (separator != null ? separator : DEFAULT_SEPARATOR);
	}

	/**
	 * Find the most specific localized resource for the given name, extension and
	 * locale:
	 * <p>
	 * The file will be searched with locations in the following order, similar to
	 * {@code java.util.ResourceBundle}'s search order:
	 * <ul>
	 * <li>[name]_[language]_[country]_[variant][extension]
	 * <li>[name]_[language]_[country][extension]
	 * <li>[name]_[language][extension]
	 * <li>[name][extension]
	 * </ul>
	 * <p>
	 * If none of the specific files can be found, a resource descriptor for the
	 * default location will be returned.
	 * 
	 * @param name      the name of the file, without localization part nor
	 *                  extension
	 * @param extension the file extension (e.g. ".xls")
	 * @param locale    the current locale (may be {@code null})
	 * @return the most specific localized resource found
	 * @see java.util.ResourceBundle
	 */
	public Resource findLocalizedResource(String name, String extension, @Nullable Locale locale) {
		Assert.notNull(name, "Name must not be null");
		Assert.notNull(extension, "Extension must not be null");

		Resource resource = null;

		if (locale != null) {
			String lang = locale.getLanguage();
			String country = locale.getCountry();
			String variant = locale.getVariant();

			// Check for file with language, country and variant localization.
			if (variant.length() > 0) {
				String location = name + this.separator + lang + this.separator + country + this.separator + variant
						+ extension;
				resource = this.resourceLoader.getResource(location);
			}

			// Check for file with language and country localization.
			if ((resource == null || !resource.exists()) && country.length() > 0) {
				String location = name + this.separator + lang + this.separator + country + extension;
				resource = this.resourceLoader.getResource(location);
			}

			// Check for document with language localization.
			if ((resource == null || !resource.exists()) && lang.length() > 0) {
				String location = name + this.separator + lang + extension;
				resource = this.resourceLoader.getResource(location);
			}
		}

		// Check for document without localization.
		if (resource == null || !resource.exists()) {
			String location = name + extension;
			resource = this.resourceLoader.getResource(location);
		}

		return resource;
	}

}
