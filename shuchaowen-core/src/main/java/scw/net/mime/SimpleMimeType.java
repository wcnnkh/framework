package scw.net.mime;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.Assert;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;

public class SimpleMimeType extends AbstractMimeType implements Serializable {
	private static final long serialVersionUID = 1L;
	static final BitSet TOKEN;

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

	private String type;
	private String subtype;
	private Map<String, String> parameters;
	
	//用于序列化
	@SuppressWarnings("unused")
	private SimpleMimeType(){};

	public SimpleMimeType(String type) {
		this(type, WILDCARD_TYPE);
	}

	public SimpleMimeType(String type, String subtype) {
		this(type, subtype, createEmptyMap());
	}

	private static Map<String, String> createEmptyMap() {
		return Collections.emptyMap();
	}

	public SimpleMimeType(String type, String subtype, Charset charset) {
		this(type, subtype, charset.name());
	}

	public SimpleMimeType(String type, String subtype, String charsetName) {
		this(type, subtype, Collections.singletonMap(PARAM_CHARSET, charsetName));
	}

	public SimpleMimeType(MimeType other, Charset charset) {
		this(other, charset.name());
	}

	public SimpleMimeType(MimeType other, String charsetName) {
		this(other.getType(), other.getSubtype(), addCharsetParameter(charsetName, other.getParameters()));
	}

	private static Map<String, String> addCharsetParameter(String charsetName, Map<String, String> parameters) {
		Map<String, String> map = new LinkedHashMap<String, String>(parameters);
		map.put(PARAM_CHARSET, charsetName.toLowerCase(Locale.ENGLISH));
		return map;
	}

	public SimpleMimeType(MimeType other, Map<String, String> parameters) {
		this(other.getType(), other.getSubtype(), parameters);
	}

	public SimpleMimeType(String type, String subtype, Map<String, String> parameters) {
		Assert.hasLength(type, "'type' must not be empty");
		Assert.hasLength(subtype, "'subtype' must not be empty");
		checkToken(type);
		checkToken(subtype);
		this.type = type.toLowerCase(Locale.ENGLISH);
		this.subtype = subtype.toLowerCase(Locale.ENGLISH);
		if (!CollectionUtils.isEmpty(parameters)) {
			Map<String, String> map = new LinkedHashMap<String, String>();
			for (Entry<String, String> entry : parameters.entrySet()) {
				String key = entry.getKey();
				if (StringUtils.isEmpty(key)) {
					continue;
				}

				String value = entry.getValue();
				if (StringUtils.isEmpty(value)) {
					continue;
				}

				checkParameters(key, value);
				map.put(key, value);
			}
			this.parameters = Collections.unmodifiableMap(map);
		} else {
			this.parameters = Collections.emptyMap();
		}
	}

	public String getType() {
		return type;
	}

	public String getSubtype() {
		return subtype;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getParameters() {
		return parameters == null ? Collections.EMPTY_MAP : parameters;
	}

	public String getParameter(String name) {
		return parameters == null ? null : parameters.get(name);
	}

	/**
	 * Checks the given token string for illegal characters, as defined in RFC
	 * 2616, section 2.2.
	 * 
	 * @throws IllegalArgumentException
	 *             in case of illegal characters
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
		} else if (!isQuotedString(value)) {
			checkToken(value);
		}
	}
}
