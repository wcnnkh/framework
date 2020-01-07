package scw.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import scw.core.utils.ObjectUtils;
import scw.core.utils.StringUtils;

public abstract class AbstractMimeType implements MimeType, MimeTypeConstants {

	protected boolean isQuotedString(String s) {
		if (s.length() < 2) {
			return false;
		} else {
			for (String quoted : QUOTEDS) {
				if (s.startsWith(quoted) && s.endsWith(quoted)) {
					return true;
				}
			}
			return false;
		}
	}

	protected String unquote(String s) {
		return (isQuotedString(s) ? s.substring(1, s.length() - 1) : s);
	}

	public boolean isConcrete() {
		return !isWildcardType() && !isWildcardSubtype();
	}

	public boolean isWildcardType() {
		return WILDCARD_TYPE.equals(getType());
	}

	public boolean isWildcardSubtype() {
		return WILDCARD_TYPE.equals(getSubtype()) || getSubtype().startsWith(WILDCARD_SUBTYPE_PREFIX);
	}

	public String getCharsetName() {
		String charset = getParameter(PARAM_CHARSET);
		return (charset != null ? unquote(charset) : null);
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
		return (getType().equalsIgnoreCase(otherType.getType()) && getSubtype().equalsIgnoreCase(otherType.getSubtype())
				&& parametersAreEqual(otherType));
	}

	private boolean parametersAreEqual(MimeType other) {
		Map<String, String> parameters = getParameters();
		Map<String, String> otherParameters = other.getParameters();

		if (parameters.size() != otherParameters.size()) {
			return false;
		}

		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			String key = entry.getKey();
			if (!otherParameters.containsKey(key)) {
				return false;
			}
			if (PARAM_CHARSET.equals(key)) {
				if (!ObjectUtils.nullSafeEquals(getCharsetName(), other.getCharsetName())) {
					return false;
				}
			} else if (!ObjectUtils.nullSafeEquals(entry.getValue(), other.getParameter(key))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = getType().hashCode();
		result = 31 * result + getSubtype().hashCode();
		result = 31 * result + getParameters().hashCode();
		return result;
	}

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
					int otherPlusIdx = other.getSubtype().lastIndexOf('+');
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
				int thisPlusIdx = getSubtype().lastIndexOf('+');
				int otherPlusIdx = other.getSubtype().lastIndexOf('+');
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

	public boolean equalsTypeAndSubtype(MimeType other) {
		if (other == null) {
			return false;
		}
		return getType().equalsIgnoreCase(other.getType()) && getSubtype().equalsIgnoreCase(other.getSubtype());
	}

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
				String thisCharsetName = getCharsetName();
				String otherCharsetName = other.getCharsetName();
				if (!StringUtils.equals(thisCharsetName, otherCharsetName, true)) {
					if (thisCharsetName == null) {
						return -1;
					}
					if (otherCharsetName == null) {
						return 1;
					}
					comp = thisCharsetName.compareToIgnoreCase(otherCharsetName);
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

	public boolean isPresentIn(Collection<? extends MimeType> mimeTypes) {
		for (MimeType mimeType : mimeTypes) {
			if (mimeType.equalsTypeAndSubtype(this)) {
				return true;
			}
		}
		return false;
	}

	private volatile String toStringValue;

	@Override
	public String toString() {
		String value = this.toStringValue;
		if (value == null) {
			StringBuilder builder = new StringBuilder();
			appendTo(getParameters(), builder);
			value = builder.toString();
			this.toStringValue = value;
		}
		return value;
	}

	protected void appendTo(Map<String, String> map, StringBuilder builder) {
		builder.append(getType());
		builder.append(TYPE_SPLIT);
		builder.append(getSubtype());

		for (Entry<String, String> entry : map.entrySet()) {
			builder.append(PARAMETER_SPLIT);
			builder.append(entry.getKey());
			builder.append(PARAMETER_KEY_VALUE_CONNECTOR);
			builder.append(entry.getValue());
		}
	}
}
