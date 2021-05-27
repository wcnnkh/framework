package scw.web;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import scw.core.utils.CollectionUtils;
import scw.http.MediaType;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.message.multipart.FileItem;
import scw.net.message.multipart.FileItemParser;
import scw.util.LinkedMultiValueMap;
import scw.util.MultiValueMap;

/**
 * 一个MultiPart请求
 * 
 * @author shuchaowen
 * @see MediaType#MULTIPART_FORM_DATA
 *
 */
public class MultiPartServerHttpRequest extends ServerHttpRequestWrapper implements AutoCloseable {
	private static Logger logger = LoggerFactory.getLogger(MultiPartServerHttpRequest.class);
	private final FileItemParser fileItemParser;

	public MultiPartServerHttpRequest(ServerHttpRequest targetRequest, FileItemParser fileItemParser) {
		super(targetRequest);
		this.fileItemParser = fileItemParser;
	}

	private List<FileItem> fileItems;

	public List<FileItem> getMultiPartList() {
		if (fileItems == null) {
			try {
				fileItems = fileItemParser.parse(this);
			} catch (IOException e) {
				logger.error(e, toString());
			}

			if (CollectionUtils.isEmpty(fileItems)) {
				this.fileItems = Collections.emptyList();
			} else {
				this.fileItems = Collections.unmodifiableList(fileItems);
			}
		}
		return fileItems;
	}

	private MultiValueMap<String, FileItem> multiPartMap;

	public MultiValueMap<String, FileItem> getMultiPartMap() {
		if (multiPartMap == null) {
			List<FileItem> fileItems = getMultiPartList();
			if (CollectionUtils.isEmpty(fileItems)) {
				multiPartMap = CollectionUtils.emptyMultiValueMap();
				return multiPartMap;
			}

			multiPartMap = new LinkedMultiValueMap<String, FileItem>();
			for (FileItem fileItem : fileItems) {
				if (fileItem == null) {
					continue;
				}

				multiPartMap.add(fileItem.getFieldName(), fileItem);
			}

			this.multiPartMap = CollectionUtils.unmodifiableMultiValueMap(multiPartMap);
		}
		return multiPartMap;
	}

	public FileItem getFirstFile() {
		for (FileItem fileItem : getMultiPartList()) {
			if (fileItem.isFormField()) {
				continue;
			}

			return fileItem;
		}

		return null;
	}

	/**
	 * 关闭所有的item
	 * 
	 * @see FileItem#close()
	 */
	public void close() {
		if (!CollectionUtils.isEmpty(fileItems)) {
			for (FileItem item : fileItems) {
				item.close();
			}
		}
	}
}
