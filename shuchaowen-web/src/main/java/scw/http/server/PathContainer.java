package scw.http.server;

import java.util.List;

import scw.util.MultiValueMap;

public abstract class PathContainer {
	/**
	 * The original path from which this instance was parsed.
	 */
	public abstract String value();

	/**
	 * The contained path elements, either {@link Separator} or
	 * {@link PathSegment}.
	 */
	public abstract List<Element> elements();

	/**
	 * Extract a sub-path from the given offset into the elements list.
	 * 
	 * @param index
	 *            the start element index (inclusive)
	 * @return the sub-path
	 */
	public PathContainer subPath(int index) {
		return subPath(index, elements().size());
	}

	/**
	 * Extract a sub-path from the given start offset into the element list
	 * (inclusive) and to the end offset (exclusive).
	 * 
	 * @param startIndex
	 *            the start element index (inclusive)
	 * @param endIndex
	 *            the end element index (exclusive)
	 * @return the sub-path
	 */
	public PathContainer subPath(int startIndex, int endIndex) {
		return DefaultPathContainer.subPath(this, startIndex, endIndex);
	}

	/**
	 * Parse the path value into a sequence of {@code "/"} {@link Separator
	 * Separator} and {@link PathSegment PathSegment} elements.
	 * 
	 * @param path
	 *            the encoded, raw path value to parse
	 * @return the parsed path
	 */
	public static PathContainer parsePath(String path) {
		return DefaultPathContainer.createFromUrlPath(path, Options.HTTP_PATH);
	}

	/**
	 * Parse the path value into a sequence of {@link Separator Separator} and
	 * {@link PathSegment PathSegment} elements.
	 * 
	 * @param path
	 *            the encoded, raw path value to parse
	 * @param options
	 *            to customize parsing
	 * @return the parsed path
	 * @since 5.2
	 */
	public static PathContainer parsePath(String path, Options options) {
		return DefaultPathContainer.createFromUrlPath(path, options);
	}

	/**
	 * A path element, either separator or path segment.
	 */
	interface Element {

		/**
		 * The unmodified, original value of this element.
		 */
		String value();
	}

	/**
	 * Path separator element.
	 */
	interface Separator extends Element {
	}

	/**
	 * Path segment element.
	 */
	interface PathSegment extends Element {

		/**
		 * Return the path segment value, decoded and sanitized, for path
		 * matching.
		 */
		String valueToMatch();

		/**
		 * Expose {@link #valueToMatch()} as a character array.
		 */
		char[] valueToMatchAsChars();

		/**
		 * Path parameters associated with this path segment.
		 */
		MultiValueMap<String, String> parameters();
	}

	/**
	 * Options to customize parsing based on the type of input path.
	 * 
	 * @since 5.2
	 */
	public static class Options {

		/**
		 * Options for HTTP URL paths:
		 * <p>
		 * Separator '/' with URL decoding and parsing of path params.
		 */
		public final static Options HTTP_PATH = Options.create('/', true);

		/**
		 * Options for a message route:
		 * <p>
		 * Separator '.' without URL decoding nor parsing of params. Escape
		 * sequences for the separator char in segment values are still decoded.
		 */
		public final static Options MESSAGE_ROUTE = Options.create('.', false);

		private final char separator;

		private final boolean decodeAndParseSegments;

		private Options(char separator, boolean decodeAndParseSegments) {
			this.separator = separator;
			this.decodeAndParseSegments = decodeAndParseSegments;
		}

		public char separator() {
			return this.separator;
		}

		public boolean shouldDecodeAndParseSegments() {
			return this.decodeAndParseSegments;
		}

		/**
		 * Create an {@link Options} instance with the given settings.
		 * 
		 * @param separator
		 *            the separator for parsing the path into segments;
		 *            currently this must be slash or dot.
		 * @param decodeAndParseSegments
		 *            whether to URL decode path segment values and parse path
		 *            parameters. If set to false, only escape sequences for the
		 *            separator char are decoded.
		 */
		public static Options create(char separator, boolean decodeAndParseSegments) {
			return new Options(separator, decodeAndParseSegments);
		}
	}
}
