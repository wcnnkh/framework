package scw.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.io.IOUtils;
import scw.io.Resource;
import scw.lang.Nullable;
import scw.util.LinkedMultiValueMap;
import scw.util.MultiValueMap;

public class FileMimeTypeUitls {
	private static final MultiValueMap<String, MimeType> fileExtensionToMediaTypes = parseMimeTypes();

	private static MultiValueMap<String, MimeType> parseMimeTypes() {
		String mimeTypesFileName = "/scw/net/mime/mime.types";
		InputStream is = MimeTypeUtils.class.getResourceAsStream(mimeTypesFileName);
		if (is == null) {
			return new LinkedMultiValueMap<String, MimeType>();
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is, MimeTypeUtils.US_ASCII));
			MultiValueMap<String, MimeType> result = new LinkedMultiValueMap<String, MimeType>();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty() || line.charAt(0) == '#') {
					continue;
				}
				String[] tokens = StringUtils.tokenizeToStringArray(line, " \t\n\r\f");
				MimeType mimeType = MimeTypeUtils.parseMimeType(tokens[0]);
				for (int i = 1; i < tokens.length; i++) {
					String fileExtension = tokens[i].toLowerCase(Locale.ENGLISH);
					result.add(fileExtension, mimeType);
				}
			}
			return result;
		} catch (Exception e) {
			throw new IllegalStateException("Could not load '" + mimeTypesFileName + "'", e);
		} finally {
			IOUtils.close(reader, is);
		}
	}

	public static MimeType getMimeType(@Nullable Resource resource) {
		if (resource == null) {
			return null;
		}

		return getMimeType(resource.getFilename());
	}

	public static MimeType getMimeType(@Nullable String filename) {
		List<MimeType> mediaTypes = getMimeTypes(filename);
		if (CollectionUtils.isEmpty(mediaTypes)) {
			return null;
		}

		return mediaTypes.get(0);
	}

	public static List<MimeType> getMimeTypes(@Nullable String filename) {
		String ext = StringUtils.getFilenameExtension(filename);
		if (ext == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(fileExtensionToMediaTypes.get(ext.toLowerCase(Locale.ENGLISH)));
	}
}
