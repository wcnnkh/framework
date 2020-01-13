package scw.http.server;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.core.Assert;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.lang.Nullable;
import scw.util.LinkedMultiValueMap;
import scw.util.MultiValueMap;

final class DefaultPathContainer extends PathContainer {

	private static final MultiValueMap<String, String> EMPTY_PARAMS = new LinkedMultiValueMap<String, String>();

	private static final PathContainer EMPTY_PATH = new DefaultPathContainer("");

	private static final Map<Character, DefaultSeparator> SEPARATORS = new HashMap<Character, DefaultSeparator>(2);

	static {
		SEPARATORS.put('/', new DefaultSeparator('/', "%2F"));
		SEPARATORS.put('.', new DefaultSeparator('.', "%2E"));
	}

	private final String path;

	private final List<Element> elements;

	private DefaultPathContainer(String path) {
		this.path = path;
		this.elements = Collections.emptyList();
	}

	private DefaultPathContainer(String path, List<Element> elements) {
		this.path = path;
		this.elements = Collections.unmodifiableList(elements);
	}

	@Override
	public String value() {
		return this.path;
	}

	@Override
	public List<Element> elements() {
		return this.elements;
	}

	@Override
	public boolean equals(@Nullable Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PathContainer)) {
			return false;
		}
		return value().equals(((PathContainer) other).value());
	}

	@Override
	public int hashCode() {
		return this.path.hashCode();
	}

	@Override
	public String toString() {
		return value();
	}

	static PathContainer createFromUrlPath(String path, Options options) {
		if (path.isEmpty()) {
			return EMPTY_PATH;
		}
		char separator = options.separator();
		DefaultSeparator separatorElement = SEPARATORS.get(separator);
		if (separatorElement == null) {
			throw new IllegalArgumentException("Unexpected separator: '" + separator + "'");
		}
		List<Element> elements = new ArrayList<Element>();
		int begin;
		if (path.charAt(0) == separator) {
			begin = 1;
			elements.add(separatorElement);
		} else {
			begin = 0;
		}
		while (begin < path.length()) {
			int end = path.indexOf(separator, begin);
			String segment = (end != -1 ? path.substring(begin, end) : path.substring(begin));
			if (!segment.isEmpty()) {
				elements.add(options.shouldDecodeAndParseSegments() ? decodeAndParsePathSegment(segment)
						: new DefaultPathSegment(segment, separatorElement));
			}
			if (end == -1) {
				break;
			}
			elements.add(separatorElement);
			begin = end + 1;
		}
		return new DefaultPathContainer(path, elements);
	}

	private static PathSegment decodeAndParsePathSegment(String segment) {
		Charset charset = StandardCharsets.UTF_8;
		int index = segment.indexOf(';');
		if (index == -1) {
			String valueToMatch = StringUtils.uriDecode(segment, charset);
			return new DefaultPathSegment(segment, valueToMatch, EMPTY_PARAMS);
		} else {
			String valueToMatch = StringUtils.uriDecode(segment.substring(0, index), charset);
			String pathParameterContent = segment.substring(index);
			MultiValueMap<String, String> parameters = parsePathParams(pathParameterContent, charset);
			return new DefaultPathSegment(segment, valueToMatch, parameters);
		}
	}

	private static MultiValueMap<String, String> parsePathParams(String input, Charset charset) {
		MultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>();
		int begin = 1;
		while (begin < input.length()) {
			int end = input.indexOf(';', begin);
			String param = (end != -1 ? input.substring(begin, end) : input.substring(begin));
			parsePathParamValues(param, charset, result);
			if (end == -1) {
				break;
			}
			begin = end + 1;
		}
		return result;
	}

	private static void parsePathParamValues(String input, Charset charset, MultiValueMap<String, String> output) {
		if (StringUtils.hasText(input)) {
			int index = input.indexOf('=');
			if (index != -1) {
				String name = input.substring(0, index);
				String value = input.substring(index + 1);
				for (String v : StringUtils.commaDelimitedListToStringArray(value)) {
					name = StringUtils.uriDecode(name, charset);
					if (StringUtils.hasText(name)) {
						output.add(name, StringUtils.uriDecode(v, charset));
					}
				}
			} else {
				String name = StringUtils.uriDecode(input, charset);
				if (StringUtils.hasText(name)) {
					output.add(input, "");
				}
			}
		}
	}

	static PathContainer subPath(PathContainer container, int fromIndex, int toIndex) {
		List<Element> elements = container.elements();
		if (fromIndex == 0 && toIndex == elements.size()) {
			return container;
		}
		if (fromIndex == toIndex) {
			return EMPTY_PATH;
		}

		Assert.isTrue(fromIndex >= 0 && fromIndex < elements.size(), "Invalid fromIndex: " + fromIndex);
		Assert.isTrue(toIndex >= 0 && toIndex <= elements.size(), "Invalid toIndex: " + toIndex);
		Assert.isTrue(fromIndex < toIndex, "fromIndex: " + fromIndex + " should be < toIndex " + toIndex);

		List<Element> subList = elements.subList(fromIndex, toIndex);
		String path = StringUtils.join(subList, "");
		return new DefaultPathContainer(path, subList);
	}

	private static class DefaultSeparator implements Separator {

		private final String separator;

		private final String encodedSequence;

		DefaultSeparator(char separator, String encodedSequence) {
			this.separator = String.valueOf(separator);
			this.encodedSequence = encodedSequence;
		}

		public String value() {
			return this.separator;
		}

		public String encodedSequence() {
			return this.encodedSequence;
		}
	}

	private static class DefaultPathSegment implements PathSegment {

		private final String value;

		private final String valueToMatch;

		private final char[] valueToMatchAsChars;

		private final MultiValueMap<String, String> parameters;

		/**
		 * Constructor for decoded and parsed segments.
		 */
		DefaultPathSegment(String value, String valueToMatch, MultiValueMap<String, String> params) {
			this.value = value;
			this.valueToMatch = valueToMatch;
			this.valueToMatchAsChars = valueToMatch.toCharArray();
			this.parameters = CollectionUtils.unmodifiableMultiValueMap(params);
		}

		/**
		 * Constructor for segments without decoding and parsing.
		 */
		DefaultPathSegment(String value, DefaultSeparator separator) {
			this.value = value;
			this.valueToMatch = value.contains(separator.encodedSequence())
					? value.replaceAll(separator.encodedSequence(), separator.value()) : value;
			this.valueToMatchAsChars = this.valueToMatch.toCharArray();
			this.parameters = EMPTY_PARAMS;
		}

		public String value() {
			return this.value;
		}

		public String valueToMatch() {
			return this.valueToMatch;
		}

		public char[] valueToMatchAsChars() {
			return this.valueToMatchAsChars;
		}

		public MultiValueMap<String, String> parameters() {
			return this.parameters;
		}

		@Override
		public boolean equals(@Nullable Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof PathSegment)) {
				return false;
			}
			return value().equals(((PathSegment) other).value());
		}

		@Override
		public int hashCode() {
			return this.value.hashCode();
		}

		@Override
		public String toString() {
			return "[value='" + this.value + "']";
		}
	}

}
