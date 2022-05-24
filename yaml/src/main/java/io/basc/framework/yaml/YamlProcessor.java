package io.basc.framework.yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Representer;

import io.basc.framework.io.IOUtils;
import io.basc.framework.io.Resource;
import io.basc.framework.io.WritableResource;
import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.lang.NestedRuntimeException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.StringUtils;

public class YamlProcessor implements Function<Resource, Properties>, PropertiesResolver {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private ResolutionMethod resolutionMethod = ResolutionMethod.OVERRIDE;

	private List<DocumentMatcher> documentMatchers = Collections.emptyList();

	private boolean matchDefault = true;

	private Set<String> supportedTypes = Collections.emptySet();

	/**
	 * A map of document matchers allowing callers to selectively use only some of
	 * the documents in a YAML resource. In YAML documents are separated by
	 * {@code ---} lines, and each document is converted to properties before the
	 * match is made. E.g.
	 * 
	 * <pre class="code">
	 * environment: dev
	 * url: https://dev.bar.com
	 * name: Developer Setup
	 * ---
	 * environment: prod
	 * url:https://foo.bar.com
	 * name: My Cool App
	 * </pre>
	 * 
	 * when mapped with
	 * 
	 * <pre class="code">
	 * setDocumentMatchers(properties -&gt; (&quot;prod&quot;.equals(properties.getProperty(&quot;environment&quot;)) ? MatchStatus.FOUND
	 * 		: MatchStatus.NOT_FOUND));
	 * </pre>
	 * 
	 * would end up as
	 * 
	 * <pre class="code">
	 * environment=prod
	 * url=https://foo.bar.com
	 * name=My Cool App
	 * </pre>
	 */
	public void setDocumentMatchers(DocumentMatcher... matchers) {
		this.documentMatchers = Arrays.asList(matchers);
	}

	/**
	 * Flag indicating that a document for which all the
	 * {@link #setDocumentMatchers(DocumentMatcher...) document matchers} abstain
	 * will nevertheless match. Default is {@code true}.
	 */
	public void setMatchDefault(boolean matchDefault) {
		this.matchDefault = matchDefault;
	}

	/**
	 * Method to use for resolving resources. Each resource will be converted to a
	 * Map, so this property is used to decide which map entries to keep in the
	 * final output from this factory. Default is {@link ResolutionMethod#OVERRIDE}.
	 */
	public void setResolutionMethod(ResolutionMethod resolutionMethod) {
		Assert.notNull(resolutionMethod, "ResolutionMethod must not be null");
		this.resolutionMethod = resolutionMethod;
	}

	/**
	 * Set the supported types that can be loaded from YAML documents.
	 * <p>
	 * If no supported types are configured, all types encountered in YAML documents
	 * will be supported. If an unsupported type is encountered, an
	 * {@link IllegalStateException} will be thrown when the corresponding YAML node
	 * is processed.
	 * 
	 * @param supportedTypes the supported types, or an empty array to clear the
	 *                       supported types
	 * @see #createYaml()
	 */
	public void setSupportedTypes(Class<?>... supportedTypes) {
		if (ObjectUtils.isEmpty(supportedTypes)) {
			this.supportedTypes = Collections.emptySet();
		} else {
			Assert.noNullElements(supportedTypes, "'supportedTypes' must not contain null elements");
			this.supportedTypes = new HashSet<String>();
			for (Class<?> type : supportedTypes) {
				this.supportedTypes.add(type.getName());
			}
		}
	}

	/**
	 * Provide an opportunity for subclasses to process the Yaml parsed from the
	 * supplied resources. Each resource is parsed in turn and the documents inside
	 * checked against the {@link #setDocumentMatchers(DocumentMatcher...)
	 * matchers}. If a document matches it is passed into the callback, along with
	 * its representation as Properties. Depending on the
	 * {@link #setResolutionMethod(ResolutionMethod)} not all of the documents will
	 * be parsed.
	 * 
	 * @param callback  a callback to delegate to once matching documents are found
	 * @param charset
	 * @param resources locations of YAML {@link Resource resources} to be loaded
	 * @see #createYaml()
	 */
	public void process(MatchCallback callback, Charset charset, Iterable<? extends Resource> resources) {
		if (resources == null) {
			return;
		}

		Yaml yaml = createYaml();
		for (Resource resource : resources) {
			if (resource == null || !resource.exists()) {
				continue;
			}

			boolean found = process(callback, yaml, charset, resource);
			if (this.resolutionMethod == ResolutionMethod.FIRST_FOUND && found) {
				return;
			}
		}
	}

	public void process(MatchCallback callback, @Nullable Charset charset, Resource... resources) {
		if (ArrayUtils.isEmpty(resources)) {
			return;
		}

		process(callback, charset, Arrays.asList(resources));
	}

	/**
	 * Create the {@link Yaml} instance to use.
	 * <p>
	 * The default implementation sets the "allowDuplicateKeys" flag to
	 * {@code false}, enabling built-in duplicate key handling in SnakeYAML 1.18+.
	 * <p>
	 * a {@code Yaml} instance that filters out unsupported types encountered in
	 * YAML documents. If an unsupported type is encountered, an
	 * {@link IllegalStateException} will be thrown when the node is processed.
	 * 
	 * @see LoaderOptions#setAllowDuplicateKeys(boolean)
	 */
	protected Yaml createYaml() {
		LoaderOptions loaderOptions = new LoaderOptions();
		loaderOptions.setAllowDuplicateKeys(false);

		if (!this.supportedTypes.isEmpty()) {
			return new Yaml(new FilteringConstructor(loaderOptions), new Representer(), new DumperOptions(),
					loaderOptions);
		}
		return new Yaml(loaderOptions);
	}

	private boolean process(MatchCallback callback, Yaml yaml, Charset charset, Resource resource) {
		int count = 0;
		if (logger.isDebugEnabled()) {
			logger.debug("Loading from YAML: " + resource);
		}
		Reader reader = null;
		InputStream is = null;
		try {
			is = resource.getInputStream();
			reader = charset == null ? new UnicodeReader(is) : new InputStreamReader(is, charset);
			for (Object object : yaml.loadAll(reader)) {
				if (object != null && process(asMap(object), callback)) {
					count++;
					if (this.resolutionMethod == ResolutionMethod.FIRST_FOUND) {
						break;
					}
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug(
						"Loaded " + count + " document" + (count > 1 ? "s" : "") + " from YAML resource: " + resource);
			}
		} catch (IOException e) {
			handleProcessError(resource, e);
		} finally {
			if (!resource.isOpen()) {
				IOUtils.closeQuietly(reader, is);
			}
		}
		return (count > 0);
	}

	private void handleProcessError(Resource resource, IOException ex) {
		if (this.resolutionMethod != ResolutionMethod.FIRST_FOUND
				&& this.resolutionMethod != ResolutionMethod.OVERRIDE_AND_IGNORE) {
			throw new IllegalStateException(ex);
		}
		if (logger.isWarnEnabled()) {
			logger.warn("Could not load map from " + resource + ": " + ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> asMap(Object object) {
		// YAML can have numbers as keys
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		if (!(object instanceof Map)) {
			// A document can be a text literal
			result.put("document", object);
			return result;
		}

		Map<Object, Object> map = (Map<Object, Object>) object;
		for (Entry<Object, Object> entry : map.entrySet()) {
			Object value = entry.getValue();
			Object key = entry.getKey();
			if (value instanceof Map) {
				value = asMap(value);
			}
			if (key instanceof CharSequence) {
				result.put(key.toString(), value);
			} else {
				// It has to be a map key in this case
				result.put("[" + key.toString() + "]", value);
			}
		}
		return result;
	}

	private boolean process(Map<String, Object> map, MatchCallback callback) {
		Properties properties = CollectionFactory.createStringAdaptingProperties();
		properties.putAll(getFlattenedMap(map));

		if (this.documentMatchers.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Merging document (no matchers set): " + map);
			}
			callback.process(properties, map);
			return true;
		}

		MatchStatus result = MatchStatus.ABSTAIN;
		for (DocumentMatcher matcher : this.documentMatchers) {
			MatchStatus match = matcher.matches(properties);
			result = MatchStatus.getMostSpecific(match, result);
			if (match == MatchStatus.FOUND) {
				if (logger.isDebugEnabled()) {
					logger.debug("Matched document with document matcher: " + properties);
				}
				callback.process(properties, map);
				return true;
			}
		}

		if (result == MatchStatus.ABSTAIN && this.matchDefault) {
			if (logger.isDebugEnabled()) {
				logger.debug("Matched document with default matcher: " + map);
			}
			callback.process(properties, map);
			return true;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Unmatched document: " + map);
		}
		return false;
	}

	/**
	 * Return a flattened version of the given map, recursively following any nested
	 * Map or Collection values. Entries from the resulting map retain the same
	 * order as the source. When called with the Map from a {@link MatchCallback}
	 * the result will contain the same values as the {@link MatchCallback}
	 * Properties.
	 * 
	 * @param source the source map
	 * @return a flattened map
	 */
	protected final Map<String, Object> getFlattenedMap(Map<String, Object> source) {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		buildFlattenedMap(result, source, null);
		return result;
	}

	private void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, @Nullable String path) {
		for (Entry<String, Object> entry : source.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (StringUtils.hasText(path)) {
				if (key.startsWith("[")) {
					key = path + key;
				} else {
					key = path + '.' + key;
				}
			}
			if (value instanceof String) {
				result.put(key, value);
			} else if (value instanceof Map) {
				// Need a compound key
				@SuppressWarnings("unchecked")
				Map<String, Object> map = (Map<String, Object>) value;
				buildFlattenedMap(result, map, key);
			} else if (value instanceof Collection) {
				// Need a compound key
				@SuppressWarnings("unchecked")
				Collection<Object> collection = (Collection<Object>) value;
				if (collection.isEmpty()) {
					result.put(key, "");
				} else {
					int count = 0;
					for (Object object : collection) {
						buildFlattenedMap(result, Collections.singletonMap("[" + (count++) + "]", object), key);
					}
				}
			} else {
				result.put(key, (value != null ? value : ""));
			}
		}
		;
	}

	/**
	 * Callback interface used to process the YAML parsing results.
	 */
	@FunctionalInterface
	public interface MatchCallback {

		/**
		 * Process the given representation of the parsing results.
		 * 
		 * @param properties the properties to process (as a flattened representation
		 *                   with indexed keys in case of a collection or map)
		 * @param map        the result map (preserving the original value structure in
		 *                   the YAML document)
		 */
		void process(Properties properties, Map<String, Object> map);
	}

	/**
	 * Strategy interface used to test if properties match.
	 */
	@FunctionalInterface
	public interface DocumentMatcher {

		/**
		 * Test if the given properties match.
		 * 
		 * @param properties the properties to test
		 * @return the status of the match
		 */
		MatchStatus matches(Properties properties);
	}

	/**
	 * Status returned from {@link DocumentMatcher#matches(java.util.Properties)}.
	 */
	public enum MatchStatus {

		/**
		 * A match was found.
		 */
		FOUND,

		/**
		 * No match was found.
		 */
		NOT_FOUND,

		/**
		 * The matcher should not be considered.
		 */
		ABSTAIN;

		/**
		 * Compare two {@link MatchStatus} items, returning the most specific status.
		 */
		public static MatchStatus getMostSpecific(MatchStatus a, MatchStatus b) {
			return (a.ordinal() < b.ordinal() ? a : b);
		}
	}

	/**
	 * Method to use for resolving resources.
	 */
	public enum ResolutionMethod {

		/**
		 * Replace values from earlier in the list.
		 */
		OVERRIDE,

		/**
		 * Replace values from earlier in the list, ignoring any failures.
		 */
		OVERRIDE_AND_IGNORE,

		/**
		 * Take the first resource in the list that exists and use just that.
		 */
		FIRST_FOUND
	}

	/**
	 * {@link Constructor} that supports filtering of unsupported types.
	 * <p>
	 * If an unsupported type is encountered in a YAML document, an
	 * {@link IllegalStateException} will be thrown from {@link #getClassForName}.
	 */
	private class FilteringConstructor extends Constructor {

		FilteringConstructor(LoaderOptions loaderOptions) {
			super(loaderOptions);
		}

		@Override
		protected Class<?> getClassForName(String name) throws ClassNotFoundException {
			Assert.state(YamlProcessor.this.supportedTypes.contains(name),
					"Unsupported type encountered in YAML document: " + name);
			return super.getClassForName(name);
		}
	}

	@Override
	public Properties apply(Resource resource) {
		final Properties allProperties = new Properties();
		process(new MatchCallback() {

			public void process(Properties properties, Map<String, Object> map) {
				allProperties.putAll(properties);
			}
		}, null, resource);
		return allProperties;
	}

	@Override
	public boolean canResolveProperties(Resource resource) {
		return resource.exists() && resource.getName().endsWith(".yaml") || resource.getName().endsWith(".yml");
	}

	@Override
	public void resolveProperties(Properties properties, Resource resource, Charset charset) {
		process(new MatchCallback() {

			public void process(Properties props, Map<String, Object> map) {
				properties.putAll(props);
			}
		}, charset, resource);
	}

	@Override
	public void persistenceProperties(Properties properties, WritableResource resource, Charset charset) {
		try {
			resource.produce((output) -> {
				Yaml yaml = createYaml();
				Writer writer = charset == null ? new OutputStreamWriter(output)
						: new OutputStreamWriter(output, charset);
				try {
					yaml.dump(properties, writer);
				} finally {
					writer.close();
				}
			});
		} catch (IOException e) {
			throw new NestedRuntimeException(resource.getDescription(), e);
		}
	}
}
