package io.basc.framework.net;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.collect.LinkedCaseInsensitiveMap;

public class MimeType implements Comparable<MimeType>, Serializable {

	private static final long serialVersionUID = 4085923477777865903L;

	protected static final String WILDCARD_TYPE = "*";

	private static final String PARAM_CHARSET = "charset";

	private static final BitSet TOKEN;

	static {
		// variable names refer to RFC 2616, section 2.2
		BitSet ctl = new BitSet(128);
		for (int i = 0; i <= 31; i++) {
			ctl.set(i);
		}
		ctl.set(127);

		BitSet separators = new BitSet(128);
		separators.set('(');
		separators.set(')');
		separators.set('<');
		separators.set('>');
		separators.set('@');
		separators.set(',');
		separators.set(';');
		separators.set(':');
		separators.set('\\');
		separators.set('\"');
		separators.set('/');
		separators.set('[');
		separators.set(']');
		separators.set('?');
		separators.set('=');
		separators.set('{');
		separators.set('}');
		separators.set(' ');
		separators.set('\t');

		TOKEN = new BitSet(128);
		TOKEN.set(0, 128);
		TOKEN.andNot(ctl);
		TOKEN.andNot(separators);
	}

	private final String type;

	private final String subtype;

	private final Map<String, String> parameters;

	/**
	 * Create a new {@code MimeType} for the given primary type.
	 * <p>
	 * The {@linkplain #getSubtype() subtype} is set to <code>"&#42;"</code>, and
	 * the parameters are empty.
	 * 
	 * @param type the primary type
	 * @throws IllegalArgumentException if any of the parameters contains illegal
	 *                                  characters
	 */
	public MimeType(String type) {
		this(type, WILDCARD_TYPE);
	}

	/**
	 * Create a new {@code MimeType} for the given primary type and subtype.
	 * <p>
	 * The parameters are empty.
	 * 
	 * @param type    the primary type
	 * @param subtype the subtype
	 * @throws IllegalArgumentException if any of the parameters contains illegal
	 *                                  characters
	 */
	public MimeType(String type, String subtype) {
		this(type, subtype, Collections.<String, String>emptyMap());
	}

	/**
	 * Create a new {@code MimeType} for the given type, subtype, and character set.
	 * 
	 * @param type    the primary type
	 * @param subtype the subtype
	 * @param charset the character set
	 * @throws IllegalArgumentException if any of the parameters contains illegal
	 *                                  characters
	 */
	public MimeType(String type, String subtype, Charset charset) {
		this(type, subtype, Collections.singletonMap(PARAM_CHARSET, charset.name()));
	}

	/**
	 * Copy-constructor that copies the type, subtype, parameters of the given
	 * {@code MimeType}, and allows to set the specified character set.
	 * 
	 * @param other   the other MimeType
	 * @param charset the character set
	 * @throws IllegalArgumentException if any of the parameters contains illegal
	 *                                  characters
	 */
	public MimeType(MimeType other, Charset charset) {
		this(other.getType(), other.getSubtype(), addCharsetParameter(charset.name(), other.getParameters()));
	}

	public MimeType(MimeType other, String charsetName) {
		this(other.getType(), other.getSubtype(), addCharsetParameter(charsetName, other.getParameters()));
	}

	/**
	 * Copy-constructor that copies the type and subtype of the given
	 * {@code MimeType}, and allows for different parameter.
	 * 
	 * @param other      the other MimeType
	 * @param parameters the parameters (may be {@code null})
	 * @throws IllegalArgumentException if any of the parameters contains illegal
	 *                                  characters
	 */
	public MimeType(MimeType other, Map<String, String> parameters) {
		this(other.getType(), other.getSubtype(), parameters);
	}

	/**
	 * Create a new {@code MimeType} for the given type, subtype, and parameters.
	 * 
	 * @param type       the primary type
	 * @param subtype    the subtype
	 * @param parameters the parameters (may be {@code null})
	 * @throws IllegalArgumentException if any of the parameters contains illegal
	 *                                  characters
	 */
	public MimeType(String type, String subtype, Map<String, String> parameters) {
		Assert.hasLength(type, "'type' must not be empty");
		Assert.hasLength(subtype, "'subtype' must not be empty");
		checkToken(type);
		checkToken(subtype);
		this.type = type.toLowerCase(Locale.ENGLISH);
		this.subtype = subtype.toLowerCase(Locale.ENGLISH);
		if (!CollectionUtils.isEmpty(parameters)) {
			Map<String, String> map = new LinkedCaseInsensitiveMap<String>(parameters.size(), Locale.ENGLISH);
			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				String attribute = entry.getKey();
				String value = entry.getValue();
				checkParameters(attribute, value);
				map.put(attribute, value);
			}
			this.parameters = Collections.unmodifiableMap(map);
		} else {
			this.parameters = Collections.emptyMap();
		}
	}

	/**
	 * Checks the given token string for illegal characters, as defined in RFC 2616,
	 * section 2.2.
	 * 
	 * @throws IllegalArgumentException in case of illegal characters
	 * @see <a href="https://tools.ietf.org/html/rfc2616#section-2.2">HTTP 1.1,
	 *      section 2.2</a>
	 */
	private void checkToken(String token) {
		for (int i = 0; i < token.length(); i++) {
			char ch = token.charAt(i);
			if (!TOKEN.get(ch)) {
				throw new IllegalArgumentException("Invalid token character '" + ch + "' in token \"" + token + "\"");
			}
		}
	}

	protected void checkParameters(String attribute, String value) {
		Assert.hasLength(attribute, "'attribute' must not be empty");
		Assert.hasLength(value, "'value' must not be empty");
		checkToken(attribute);
		if (PARAM_CHARSET.equals(attribute)) {
			value = unquote(value);
			Charset.forName(value);
		} else if (!isQuotedString(value)) {
			checkToken(value);
		}
	}

	private boolean isQuotedString(String s) {
		if (s.length() < 2) {
			return false;
		} else {
			return ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'")));
		}
	}

	protected String unquote(String s) {
		if (s == null) {
			return null;
		}
		return (isQuotedString(s) ? s.substring(1, s.length() - 1) : s);
	}

	public boolean isWildcardType() {
		return WILDCARD_TYPE.equals(getType());
	}

	/**
	 * Indicates whether the {@linkplain #getSubtype() subtype} is the wildcard
	 * character <code>&#42;</code> or the wildcard character followed by a suffix
	 * (e.g. <code>&#42;+xml</code>).
	 * 
	 * @return whether the subtype is a wildcard
	 */
	public boolean isWildcardSubtype() {
		return WILDCARD_TYPE.equals(getSubtype()) || getSubtype().startsWith("*+");
	}

	public boolean equalsTypeAndSubtype(MimeType other) {
		if (other == null) {
			return false;
		}
		return getType().equalsIgnoreCase(other.getType()) && getSubtype().equalsIgnoreCase(other.getSubtype());
	}

	/**
	 * Indicates whether this MIME Type is concrete, i.e. whether neither the type
	 * nor the subtype is a wildcard character <code>&#42;</code>.
	 * 
	 * @return whether this MIME Type is concrete
	 */
	public boolean isConcrete() {
		return !isWildcardType() && !isWildcardSubtype();
	}

	public String getType() {
		return this.type;
	}

	public String getSubtype() {
		return this.subtype;
	}

	/**
	 * Return the character set, as indicated by a {@code charset} parameter, if
	 * any.
	 * 
	 * @return the character set, or {@code null} if not available
	 */
	public Charset getCharset() {
		String charset = getCharsetName();
		return (charset != null ? Charset.forName(unquote(charset)) : null);
	}

	public String getCharsetName() {
		return getParameter(PARAM_CHARSET);
	}

	/**
	 * Return a generic parameter value, given a parameter name.
	 * 
	 * @param name the parameter name
	 * @return the parameter value, or {@code null} if not present
	 */
	public String getParameter(String name) {
		return this.parameters.get(name);
	}

	/**
	 * Return all generic parameter values.
	 * 
	 * @return a read-only map (possibly empty, never {@code null})
	 */
	public Map<String, String> getParameters() {
		return this.parameters;
	}

	/**
	 * Indicate whether this MIME Type includes the given MIME Type.
	 * <p>
	 * For instance, {@code text/*} includes {@code text/plain} and
	 * {@code text/html}, and {@code application/*+xml} includes
	 * {@code application/soap+xml}, etc. This method is <b>not</b> symmetric.
	 * 
	 * @param other the reference MIME Type with which to compare
	 * @return {@code true} if this MIME Type includes the given MIME Type;
	 *         {@code false} otherwise
	 */
	public boolean includes(MimeType other) {
		if (other == null) {
			return false;
		}
		if (isWildcardType()) {
			// */* includes anything
			return true;
		} else if (getType().equals(other.getType())) {
			if (getSubtype().equals(other.getSubtype())) {
				return true;
			}
			if (isWildcardSubtype()) {
				// Wildcard with suffix, e.g. application/*+xml
				int thisPlusIdx = getSubtype().lastIndexOf('+');
				if (thisPlusIdx == -1) {
					return true;
				} else {
					// application/*+xml includes application/soap+xml
					int otherPlusIdx = other.getSubtype().indexOf('+');
					if (otherPlusIdx != -1) {
						String thisSubtypeNoSuffix = getSubtype().substring(0, thisPlusIdx);
						String thisSubtypeSuffix = getSubtype().substring(thisPlusIdx + 1);
						String otherSubtypeSuffix = other.getSubtype().substring(otherPlusIdx + 1);
						if (thisSubtypeSuffix.equals(otherSubtypeSuffix) && WILDCARD_TYPE.equals(thisSubtypeNoSuffix)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Indicate whether this MIME Type is compatible with the given MIME Type.
	 * <p>
	 * For instance, {@code text/*} is compatible with {@code text/plain},
	 * {@code text/html}, and vice versa. In effect, this method is similar to
	 * {@link #includes}, except that it <b>is</b> symmetric.
	 * 
	 * @param other the reference MIME Type with which to compare
	 * @return {@code true} if this MIME Type is compatible with the given MIME
	 *         Type; {@code false} otherwise
	 */
	public boolean isCompatibleWith(MimeType other) {
		if (other == null) {
			return false;
		}
		if (isWildcardType() || other.isWildcardType()) {
			return true;
		} else if (getType().equals(other.getType())) {
			if (getSubtype().equals(other.getSubtype())) {
				return true;
			}
			// Wildcard with suffix? e.g. application/*+xml
			if (isWildcardSubtype() || other.isWildcardSubtype()) {
				int thisPlusIdx = getSubtype().indexOf('+');
				int otherPlusIdx = other.getSubtype().indexOf('+');
				if (thisPlusIdx == -1 && otherPlusIdx == -1) {
					return true;
				} else if (thisPlusIdx != -1 && otherPlusIdx != -1) {
					String thisSubtypeNoSuffix = getSubtype().substring(0, thisPlusIdx);
					String otherSubtypeNoSuffix = other.getSubtype().substring(0, otherPlusIdx);
					String thisSubtypeSuffix = getSubtype().substring(thisPlusIdx + 1);
					String otherSubtypeSuffix = other.getSubtype().substring(otherPlusIdx + 1);
					if (thisSubtypeSuffix.equals(otherSubtypeSuffix) && (WILDCARD_TYPE.equals(thisSubtypeNoSuffix)
							|| WILDCARD_TYPE.equals(otherSubtypeNoSuffix))) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MimeType)) {
			return false;
		}
		MimeType otherType = (MimeType) other;
		return (this.type.equalsIgnoreCase(otherType.type) && this.subtype.equalsIgnoreCase(otherType.subtype)
				&& parametersAreEqual(otherType));
	}

	private boolean parametersAreEqual(MimeType other) {
		if (this.parameters.size() != other.parameters.size()) {
			return false;
		}

		for (String key : this.parameters.keySet()) {
			if (!other.parameters.containsKey(key)) {
				return false;
			}
			if (PARAM_CHARSET.equals(key)) {
				if (!ObjectUtils.equals(getCharset(), other.getCharset())) {
					return false;
				}
			} else if (!ObjectUtils.equals(this.parameters.get(key), other.parameters.get(key))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = this.type.hashCode();
		result = 31 * result + this.subtype.hashCode();
		result = 31 * result + this.parameters.hashCode();
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		appendTo(builder);
		return builder.toString();
	}

	protected void appendTo(StringBuilder builder) {
		builder.append(this.type);
		builder.append('/');
		builder.append(this.subtype);
		appendTo(this.parameters, builder);
	}

	private void appendTo(Map<String, String> map, StringBuilder builder) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			builder.append(';');
			builder.append(entry.getKey());
			builder.append('=');
			builder.append(entry.getValue());
		}
	}

	/**
	 * Compares this MIME Type to another alphabetically.
	 * 
	 * @param other the MIME Type to compare to
	 * @see MimeTypeUtils#sortBySpecificity(List)
	 */
	public int compareTo(MimeType other) {
		int comp = getType().compareToIgnoreCase(other.getType());
		if (comp != 0) {
			return comp;
		}
		comp = getSubtype().compareToIgnoreCase(other.getSubtype());
		if (comp != 0) {
			return comp;
		}
		comp = getParameters().size() - other.getParameters().size();
		if (comp != 0) {
			return comp;
		}

		TreeSet<String> thisAttributes = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		thisAttributes.addAll(getParameters().keySet());
		TreeSet<String> otherAttributes = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		otherAttributes.addAll(other.getParameters().keySet());
		Iterator<String> thisAttributesIterator = thisAttributes.iterator();
		Iterator<String> otherAttributesIterator = otherAttributes.iterator();

		while (thisAttributesIterator.hasNext()) {
			String thisAttribute = thisAttributesIterator.next();
			String otherAttribute = otherAttributesIterator.next();
			comp = thisAttribute.compareToIgnoreCase(otherAttribute);
			if (comp != 0) {
				return comp;
			}
			if (PARAM_CHARSET.equals(thisAttribute)) {
				Charset thisCharset = getCharset();
				Charset otherCharset = other.getCharset();
				if (thisCharset != otherCharset) {
					if (thisCharset == null) {
						return -1;
					}
					if (otherCharset == null) {
						return 1;
					}
					comp = thisCharset.compareTo(otherCharset);
					if (comp != 0) {
						return comp;
					}
				}
			} else {
				String thisValue = getParameters().get(thisAttribute);
				String otherValue = other.getParameters().get(otherAttribute);
				if (otherValue == null) {
					otherValue = "";
				}
				comp = thisValue.compareTo(otherValue);
				if (comp != 0) {
					return comp;
				}
			}
		}

		return 0;
	}

	/**
	 * Parse the given String value into a {@code MimeType} object, with this method
	 * name following the 'valueOf' naming convention (as supported by
	 * 
	 * @see MimeTypeUtils#parseMimeType(String)
	 */
	public static MimeType valueOf(String value) {
		return MimeTypeUtils.parseMimeType(value);
	}

	private static Map<String, String> addCharsetParameter(String charsetName, Map<String, String> parameters) {
		Map<String, String> map = new LinkedHashMap<String, String>(parameters);
		map.put(PARAM_CHARSET, charsetName);
		return map;
	}

	public static class SpecificityComparator<T extends MimeType> implements Comparator<T> {

		public int compare(T mimeType1, T mimeType2) {
			if (mimeType1.isWildcardType() && !mimeType2.isWildcardType()) { // */*
																				// <
																				// audio/*
				return 1;
			} else if (mimeType2.isWildcardType() && !mimeType1.isWildcardType()) { // audio/*
																					// >
																					// */*
				return -1;
			} else if (!mimeType1.getType().equals(mimeType2.getType())) { // audio/basic
																			// ==
																			// text/html
				return 0;
			} else { // mediaType1.getType().equals(mediaType2.getType())
				if (mimeType1.isWildcardSubtype() && !mimeType2.isWildcardSubtype()) { // audio/*
																						// <
																						// audio/basic
					return 1;
				} else if (mimeType2.isWildcardSubtype() && !mimeType1.isWildcardSubtype()) { // audio/basic
																								// >
																								// audio/*
					return -1;
				} else if (!mimeType1.getSubtype().equals(mimeType2.getSubtype())) { // audio/basic
																						// ==
																						// audio/wave
					return 0;
				} else { // mediaType2.getSubtype().equals(mediaType2.getSubtype())
					return compareParameters(mimeType1, mimeType2);
				}
			}
		}

		protected int compareParameters(T mimeType1, T mimeType2) {
			int paramsSize1 = mimeType1.getParameters().size();
			int paramsSize2 = mimeType2.getParameters().size();
			return (paramsSize2 < paramsSize1 ? -1 : (paramsSize2 == paramsSize1 ? 0 : 1)); // audio/basic;level=1
																							// <
																							// audio/basic
		}
	}

}
