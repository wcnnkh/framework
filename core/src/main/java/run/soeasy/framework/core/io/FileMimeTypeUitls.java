package run.soeasy.framework.core.io;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.LinkedMultiValueMap;
import run.soeasy.framework.core.collection.MultiValueMap;
import run.soeasy.framework.core.io.load.SystemResourceLoader;
import run.soeasy.framework.core.strings.StringUtils;
import run.soeasy.framework.logging.LogManager;
import run.soeasy.framework.logging.Logger;

public class FileMimeTypeUitls {
	private static Logger logger = LogManager.getLogger(FileMimeTypeUitls.class);
	private static MultiValueMap<String, MimeType> fileExtensionToMediaTypes;

	static {
		try {
			fileExtensionToMediaTypes = parseMimeTypes();
		} catch (Throwable e) {
			logger.error(e, "Failed to load mime.type file");
			fileExtensionToMediaTypes = CollectionUtils.emptyMultiValueMap();
		}
	}

	private static MultiValueMap<String, MimeType> parseMimeTypes() {
		MultiValueMap<String, MimeType> result = new LinkedMultiValueMap<String, MimeType>();
		Resource resource = SystemResourceLoader.getInstance()
				.getResource(FileMimeTypeUitls.class.getPackage().getName().replace(".", "/") + "/mime.types");
		for (String line : resource.decode(StandardCharsets.US_ASCII).readAllLines().toList()) {
			if (line.isEmpty() || line.charAt(0) == '#') {
				continue;
			}
			String[] tokens = StringUtils.tokenizeToArray(line, " \t\n\r\f");
			MimeType mimeType = MimeTypeUtils.parseMimeType(tokens[0]);
			for (int i = 1; i < tokens.length; i++) {
				String fileExtension = tokens[i].toLowerCase(Locale.ENGLISH);
				result.add(fileExtension, mimeType);
			}
		}
		return result;
	}

	public static MimeType getMimeType(Resource resource) {
		if (resource == null) {
			return null;
		}

		return getMimeType(resource.getName());
	}

	public static MimeType getMimeType(String filename) {
		List<MimeType> mediaTypes = getMimeTypes(filename);
		if (CollectionUtils.isEmpty(mediaTypes)) {
			return null;
		}

		return mediaTypes.get(0);
	}

	public static List<MimeType> getMimeTypes(String filename) {
		String ext = Resource.getExtension(filename);
		if (ext == null) {
			return Collections.emptyList();
		}

		List<MimeType> mimeTypes = fileExtensionToMediaTypes.get(ext.toLowerCase(Locale.ENGLISH));
		if (mimeTypes == null) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableList(mimeTypes);
	}
}
