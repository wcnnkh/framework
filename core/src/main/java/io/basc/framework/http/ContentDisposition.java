package io.basc.framework.http;

import io.basc.framework.lang.Constants;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.value.StringValue;
import io.basc.framework.value.Value;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class ContentDisposition {
	private static final String INVALID_HEADER_FIELD_PARAMETER_FORMAT = "Invalid header field parameter format (as defined in RFC 5987)";
	private final String type;

	private final String name;

	private final String filename;

	private final Charset charset;

	private final Map<String, Value> attributes;

	/**
	 * Private constructor. See static factory methods in this class.
	 */
	private ContentDisposition(String type, String name, String filename, Charset charset,
			Map<String, Value> attributes) {
		this.type = type;
		this.name = name;
		this.filename = filename;
		this.charset = charset;
		this.attributes = attributes;
	}

	/**
	 * Return the disposition type, like for example {@literal inline},
	 * {@literal attachment}, {@literal form-data}, or {@code null} if not defined.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Return the value of the {@literal name} parameter, or {@code null} if not
	 * defined.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return the value of the {@literal filename} parameter (or the value of the
	 * {@literal filename*} one decoded as defined in the RFC 5987), or {@code null}
	 * if not defined.
	 */
	public String getFilename() {
		return this.filename;
	}

	/**
	 * Return the charset defined in {@literal filename*} parameter, or {@code null}
	 * if not defined.
	 */
	public Charset getCharset() {
		return this.charset;
	}

	public Map<String, Value> getAttributes() {
		if (attributes == null) {
			return Collections.emptyMap();
		}
		return attributes;
	}

	public Value getAttribute(String name) {
		return getAttributes().get(name);
	}

	@Override
	public boolean equals(@Nullable Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ContentDisposition)) {
			return false;
		}
		ContentDisposition otherCd = (ContentDisposition) other;
		return (ObjectUtils.equals(this.type, otherCd.type) && ObjectUtils.equals(this.name, otherCd.name)
				&& ObjectUtils.equals(this.filename, otherCd.filename)
				&& ObjectUtils.equals(this.charset, otherCd.charset));
	}

	@Override
	public int hashCode() {
		int result = ObjectUtils.hashCode(this.type);
		result = 31 * result + ObjectUtils.hashCode(this.name);
		result = 31 * result + ObjectUtils.hashCode(this.filename);
		result = 31 * result + ObjectUtils.hashCode(this.charset);
		return result;
	}

	/**
	 * Return the header value for this content disposition as defined in RFC 6266.
	 * 
	 * @see #parse(String)
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (this.type != null) {
			sb.append(this.type);
		}
		if (this.name != null) {
			sb.append("; name=\"");
			sb.append(this.name).append('\"');
		}
		if (this.filename != null) {
			if (this.charset == null || Constants.US_ASCII.equals(this.charset)) {
				sb.append("; filename=\"");
				sb.append(escapeQuotationsInFilename(this.filename)).append('\"');
			} else {
				sb.append("; filename*=");
				sb.append(encodeFilename(this.filename, this.charset));
			}
		}

		if (!CollectionUtils.isEmpty(attributes)) {
			for (Entry<String, Value> entry : attributes.entrySet()) {
				sb.append("; ").append(entry.getKey()).append("=");
				if (entry.getValue().isNumber()) {
					sb.append(entry.getValue().getAsNumber());
				} else {
					sb.append("\"").append(entry.getValue().getAsString()).append("\"");
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Return a builder for a {@code ContentDisposition}.
	 * 
	 * @param type the disposition type like for example {@literal inline},
	 *             {@literal attachment}, or {@literal form-data}
	 * @return the builder
	 */
	public static Builder builder(String type) {
		return new BuilderImpl(type);
	}

	/**
	 * Return an empty content disposition.
	 */
	public static ContentDisposition empty() {
		return new ContentDisposition("", null, null, null, null);
	}

	/**
	 * Parse a {@literal Content-Disposition} header value as defined in RFC 2183.
	 * 
	 * @param contentDisposition the {@literal Content-Disposition} header value
	 * @return the parsed content disposition
	 * @see #toString()
	 */
	public static ContentDisposition parse(String contentDisposition) {
		List<String> parts = tokenize(contentDisposition);
		String type = parts.get(0);
		String name = null;
		String filename = null;
		Charset charset = null;
		Map<String, Value> attributes = new LinkedHashMap<String, Value>(8);
		for (int i = 1; i < parts.size(); i++) {
			String part = parts.get(i);
			int eqIndex = part.indexOf('=');
			if (eqIndex != -1) {
				String attribute = part.substring(0, eqIndex);
				boolean isText = part.startsWith("\"", eqIndex + 1) && part.endsWith("\"");
				String value = isText ? part.substring(eqIndex + 2, part.length() - 1) : part.substring(eqIndex + 1);
				if (attribute.equals("name")) {
					name = value;
				} else if (attribute.equals("filename*")) {
					int idx1 = value.indexOf('\'');
					int idx2 = value.indexOf('\'', idx1 + 1);
					if (idx1 != -1 && idx2 != -1) {
						charset = Charset.forName(value.substring(0, idx1).trim());
						Assert.isTrue(Constants.UTF_8.equals(charset) || Constants.ISO_8859_1.equals(charset),
								"Charset should be UTF-8 or ISO-8859-1");
						filename = decodeFilename(value.substring(idx2 + 1), charset);
					} else {
						// US ASCII
						filename = decodeFilename(value, Constants.US_ASCII);
					}
				} else if (attribute.equals("filename") && (filename == null)) {
					filename = value;
				} else {
					attributes.put(attribute, new StringValue(value));
				}
			} else {
				throw new IllegalArgumentException("Invalid content disposition format");
			}
		}
		return new ContentDisposition(type, name, filename, charset, attributes);
	}

	private static List<String> tokenize(String headerValue) {
		int index = headerValue.indexOf(';');
		String type = (index >= 0 ? headerValue.substring(0, index) : headerValue).trim();
		if (type.isEmpty()) {
			throw new IllegalArgumentException("Content-Disposition header must not be empty");
		}
		List<String> parts = new ArrayList<String>();
		parts.add(type);
		if (index >= 0) {
			do {
				int nextIndex = index + 1;
				boolean quoted = false;
				boolean escaped = false;
				while (nextIndex < headerValue.length()) {
					char ch = headerValue.charAt(nextIndex);
					if (ch == ';') {
						if (!quoted) {
							break;
						}
					} else if (!escaped && ch == '"') {
						quoted = !quoted;
					}
					escaped = (!escaped && ch == '\\');
					nextIndex++;
				}
				String part = headerValue.substring(index + 1, nextIndex).trim();
				if (!part.isEmpty()) {
					parts.add(part);
				}
				index = nextIndex;
			} while (index < headerValue.length());
		}
		return parts;
	}

	/**
	 * Decode the given header field param as described in RFC 5987.
	 * <p>
	 * Only the US-ASCII, UTF-8 and ISO-8859-1 charsets are supported.
	 * 
	 * @param filename the filename
	 * @param charset  the charset for the filename
	 * @return the encoded header field param
	 * @see <a href="https://tools.ietf.org/html/rfc5987">RFC 5987</a>
	 */
	private static String decodeFilename(String filename, Charset charset) {
		Assert.notNull(filename, "'input' String` should not be null");
		Assert.notNull(charset, "'charset' should not be null");
		byte[] value = filename.getBytes(charset);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int index = 0;
		while (index < value.length) {
			byte b = value[index];
			if (isRFC5987AttrChar(b)) {
				bos.write((char) b);
				index++;
			} else if (b == '%' && index < value.length - 2) {
				char[] array = new char[] { (char) value[index + 1], (char) value[index + 2] };
				try {
					bos.write(Integer.parseInt(String.valueOf(array), 16));
				} catch (NumberFormatException ex) {
					throw new IllegalArgumentException(INVALID_HEADER_FIELD_PARAMETER_FORMAT, ex);
				}
				index += 3;
			} else {
				throw new IllegalArgumentException(INVALID_HEADER_FIELD_PARAMETER_FORMAT);
			}
		}
		return new String(bos.toByteArray(), charset);
	}

	private static boolean isRFC5987AttrChar(byte c) {
		return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '!' || c == '#'
				|| c == '$' || c == '&' || c == '+' || c == '-' || c == '.' || c == '^' || c == '_' || c == '`'
				|| c == '|' || c == '~';
	}

	private static String escapeQuotationsInFilename(String filename) {
		if (filename.indexOf('"') == -1 && filename.indexOf('\\') == -1) {
			return filename;
		}
		boolean escaped = false;
		StringBuilder sb = new StringBuilder();
		for (char c : filename.toCharArray()) {
			sb.append((c == '"' && !escaped) ? "\\\"" : c);
			escaped = (!escaped && c == '\\');
		}
		// Remove backslash at the end..
		if (escaped) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * Encode the given header field param as describe in RFC 5987.
	 * 
	 * @param input   the header field param
	 * @param charset the charset of the header field param string, only the
	 *                US-ASCII, UTF-8 and ISO-8859-1 charsets are supported
	 * @return the encoded header field param
	 * @see <a href="https://tools.ietf.org/html/rfc5987">RFC 5987</a>
	 */
	private static String encodeFilename(String input, Charset charset) {
		Assert.notNull(input, "`input` is required");
		Assert.notNull(charset, "`charset` is required");
		Assert.isTrue(!Constants.US_ASCII.equals(charset), "ASCII does not require encoding");
		Assert.isTrue(Constants.UTF_8.equals(charset) || Constants.ISO_8859_1.equals(charset),
				"Only UTF-8 and ISO-8859-1 supported.");
		byte[] source = input.getBytes(charset);
		int len = source.length;
		StringBuilder sb = new StringBuilder(len << 1);
		sb.append(charset.name());
		sb.append("''");
		for (byte b : source) {
			if (isRFC5987AttrChar(b)) {
				sb.append((char) b);
			} else {
				sb.append('%');
				char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
				char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
				sb.append(hex1);
				sb.append(hex2);
			}
		}
		return sb.toString();
	}

	/**
	 * A mutable builder for {@code ContentDisposition}.
	 */
	public interface Builder {

		/**
		 * Set the value of the {@literal name} parameter.
		 */
		Builder name(String name);

		/**
		 * Set the value of the {@literal filename} parameter. The given filename will
		 * be formatted as quoted-string, as defined in RFC 2616, section 2.2, and any
		 * quote characters within the filename value will be escaped with a backslash,
		 * e.g. {@code "foo\"bar.txt"} becomes {@code "foo\\\"bar.txt"}.
		 */
		Builder filename(String filename);

		/**
		 * Set the value of the {@literal filename*} that will be encoded as defined in
		 * the RFC 5987. Only the US-ASCII, UTF-8 and ISO-8859-1 charsets are supported.
		 * <p>
		 * <strong>Note:</strong> Do not use this for a {@code "multipart/form-data"}
		 * requests as per <a link="https://tools.ietf.org/html/rfc7578#section-4.2">RFC
		 * 7578, Section 4.2</a> and also RFC 5987 itself mentions it does not apply to
		 * multipart requests.
		 */
		Builder filename(String filename, @Nullable Charset charset);

		/**
		 * 其他属性 <a href="https://tools.ietf.org/html/rfc6266#appendix-B">RFC 6266
		 * 
		 * @param attributes
		 * @return
		 */
		Builder attributes(Map<String, Value> attributes);

		/**
		 * Build the content disposition.
		 */
		ContentDisposition build();
	}

	private static class BuilderImpl implements Builder {

		private String type;

		@Nullable
		private String name;

		@Nullable
		private String filename;

		@Nullable
		private Charset charset;

		private Map<String, Value> attributes;

		public BuilderImpl(String type) {
			Assert.hasText(type, "'type' must not be not empty");
			this.type = type;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder filename(String filename) {
			Assert.hasText(filename, "No filename");
			this.filename = filename;
			return this;
		}

		public Builder filename(String filename, Charset charset) {
			Assert.hasText(filename, "No filename");
			this.filename = filename;
			this.charset = charset;
			return this;
		}

		public Builder attributes(Map<String, Value> attributes) {
			this.attributes = attributes;
			return this;
		}

		public ContentDisposition build() {
			return new ContentDisposition(this.type, this.name, this.filename, this.charset, this.attributes);
		}
	}
}
