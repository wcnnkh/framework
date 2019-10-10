package scw.net.mime;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;

public class MimeTypeUtils implements MimeTypeConstants {

	public static MimeType parseMimeType(String mimeType) {
		String[] arr = StringUtils.split(mimeType, PARAMETER_SPLIT);
		if (ArrayUtils.isEmpty(arr)) {
			throw new InvalidMimeTypeException(mimeType, "'mimeType' must not be empty");
		}

		String fullType = arr[0];
		if (fullType == null) {
			throw new InvalidMimeTypeException(mimeType, "'mimeType' must not be empty");
		}

		fullType = fullType.trim();
		if (StringUtils.isEmpty(fullType)) {
			throw new InvalidMimeTypeException(mimeType, "'mimeType' must not be empty");
		}

		if (WILDCARD_TYPE.equals(fullType)) {
			fullType = ALL_VALUE;
		}

		String[] typeArr = StringUtils.split(fullType, TYPE_SPLIT);
		if (typeArr == null || typeArr.length != 2) {
			throw new InvalidMimeTypeException(mimeType, "does not contain '/'");
		}

		String type = typeArr[0].trim();
		String subtype = typeArr[1].trim();

		if (WILDCARD_TYPE.equals(type) && !WILDCARD_TYPE.equals(subtype)) {
			throw new InvalidMimeTypeException(mimeType, "wildcard type is legal only in '*/*' (all mime types)");
		}

		Map<String, String> params = new LinkedHashMap<String, String>();
		for (int i = 1; i < arr.length; i++) {
			String[] values = StringUtils.split(arr[i], PARAMETER_KEY_VALUE_CONNECTOR);
			if (values == null || values.length != 2) {
				continue;
			}

			String key = values[0];
			if (key == null) {
				continue;
			}

			key = key.trim();
			if (key.length() == 0) {
				continue;
			}

			String value = values[1];
			if (value == null) {
				continue;
			}

			value = value.trim();
			if (value.length() == 0) {
				continue;
			}

			params.put(key, value);
		}

		return new SimpleMimeType(type, subtype, params);
	}

	public static Collection<MimeType> parseMimeTypes(String mimeTypes) {
		String[] arr = StringUtils.split(mimeTypes, MIME_TYPE_SPLIT);
		List<MimeType> list = new LinkedList<MimeType>();
		for (String mimeType : arr) {
			if (mimeType == null) {
				continue;
			}

			mimeType = mimeType.trim();
			if (mimeType.length() == 0) {
				continue;
			}

			list.add(parseMimeType(mimeType));
		}
		return list;
	}
}
