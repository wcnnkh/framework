package scw.upload.support;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import scw.io.IOUtils;
import scw.servlet.mvc.http.Multipart;
import scw.upload.DefaultUploadService;
import scw.upload.UploadException;
import scw.upload.UploadResult;

public class DefaultServletUploadService extends DefaultUploadService implements ServletUploadService {

	public DefaultServletUploadService(String rootPath, String rootUrl) {
		super(rootPath, rootUrl);
	}

	public HttpUploadResult upload(FileItem fileItem) {
		InputStream inputStream = null;
		try {
			inputStream = fileItem.getInputStream();
			UploadResult uploadResult = upload(inputStream, fileItem.getName());
			return new SimpleServletUploadResult(uploadResult, fileItem.getName(), fileItem.getFieldName());
		} catch (Exception e) {
			throw new UploadException("上传失败：" + fileItem.getName(), e);
		} finally {
			IOUtils.close(inputStream);
		}
	}

	public Collection<HttpUploadResult> multipleUpload(HttpServletRequest httpServletRequest) throws Exception {
		Multipart multipart = new Multipart(httpServletRequest);
		Collection<FileItem> fileItemList = multipart.getAllFileItemList(false, true);
		Collection<HttpUploadResult> results = new ArrayList<HttpUploadResult>(fileItemList.size());
		for (FileItem fileItem : fileItemList) {
			results.add(upload(fileItem));
			fileItem.delete();
		}
		return results;
	}

	public HttpUploadResult upload(HttpServletRequest httpServletRequest) throws Exception {
		Multipart multipart = new Multipart(httpServletRequest);
		FileItem fileItem = multipart.getFirstFileItem(false);
		if (fileItem == null) {
			throw new UploadException("无文件");
		}

		if (fileItem.getSize() <= 0) {
			throw new UploadException("文件内容为空");
		}
		return upload(fileItem);
	}
}
