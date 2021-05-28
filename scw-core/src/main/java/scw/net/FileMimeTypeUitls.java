package scw.net;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.io.Resource;
import scw.io.ResourceUtils;
import scw.lang.Nullable;
import scw.util.LinkedMultiValueMap;
import scw.util.MultiValueMap;

public class FileMimeTypeUitls {
	private static final MultiValueMap<String, MimeType> fileExtensionToMediaTypes = parseMimeTypes();

	private static MultiValueMap<String, MimeType> parseMimeTypes() {
		MultiValueMap<String, MimeType> result = new LinkedMultiValueMap<String, MimeType>();
		String mimeTypesFileName = "/scw/net/mime/mime.types";
		Resource resource = ResourceUtils.getSystemResource(mimeTypesFileName);
		for(String line : ResourceUtils.getLines(resource, MimeTypeUtils.US_ASCII)){
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
	}

	public static MimeType getMimeType(@Nullable Resource resource) {
		if (resource == null) {
			return null;
		}

		return getMimeType(resource.getName());
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
		
		List<MimeType> mimeTypes = fileExtensionToMediaTypes.get(ext.toLowerCase(Locale.ENGLISH));
		if(mimeTypes == null){
			return Collections.emptyList();
		}
		
		return Collections.unmodifiableList(mimeTypes);
	}
}
