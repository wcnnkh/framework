package scw.upload;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import scw.core.utils.StringUtils;
import scw.core.utils.XTime;
import scw.core.utils.XUtils;
import scw.http.server.ServerHttpRequest;
import scw.json.JSONUtils;
import scw.lang.ParameterException;
import scw.util.Base64;

public final class KindEditorUpload implements Upload {
	private final HashMap<String, String> extMap = new HashMap<String, String>();
	private final Map<String, Long> maxSizeMap = new HashMap<String, Long>();
	{
		extMap.put("image", ".gif.jpg.jpeg.png.bmp");
		extMap.put("flash", ".swf.flv");
		extMap.put("media", ".swf.flv.mp3.wav.wma.wmv.mid.avi.mpg.asf.rm.rmvb");
		extMap.put("file", ".doc.docx.xls.xlsx.ppt.htm.html.txt.zip.rar.gz.bz2");

		maxSizeMap.put("image", 5 * 1024 * 1024L);
		maxSizeMap.put("flash", 50 * 1024 * 1024L);
		maxSizeMap.put("media", 200 * 1024 * 1024L);
		maxSizeMap.put("file", 200 * 1024 * 1024L);
	}

	protected final String rootPath;
	private final String urlPrefix;

	public KindEditorUpload(String rootPath, String urlPrefix) {
		this.rootPath = rootPath;
		this.urlPrefix = urlPrefix;
		File file = new File(rootPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		if (!file.isDirectory()) {
			throw new ParameterException("这不是一个目录：" + rootPath);
		}
	}

	protected String getSavePath(ServerHttpRequest serverHttpRequest) {
		return rootPath;
	}

	protected boolean checkFileExt(String dir, String fileExt) {
		if (dir == null || fileExt == null) {
			return false;
		}

		String exts = extMap.get(dir);
		if (exts == null) {
			return false;
		}

		return exts.indexOf(fileExt) != -1;
	}

	protected boolean checkFileSize(String dir, long size) {
		if (dir == null) {
			return false;
		}

		Long maxSize = maxSizeMap.get(dir);
		if (maxSize == null) {
			return false;
		}

		return size <= maxSize;
	}

	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!ServletFileUpload.isMultipartContent(request)) {
			response.getWriter().write(getError("请选择文件"));
			return;
		}

		String dirName = request.getParameter("dir");
		if (dirName == null) {
			dirName = "image";
		}
		if (!extMap.containsKey(dirName)) {
			response.getWriter().write(getError("目录名不正确"));
			return;
		}

		StringBuilder url = new StringBuilder();
		if (!StringUtils.isNull(urlPrefix)) {
			url.append(urlPrefix);
		}

		FileItemFactory fileItemFactory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(fileItemFactory);
		upload.setHeaderEncoding(request.getCharacterEncoding());
		List<FileItem> items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
		}

		if (items == null) {
			response.getWriter().write(getError("请选择文件"));
			return;
		}

		Iterator<FileItem> iterator = items.iterator();
		while (iterator.hasNext()) {
			FileItem fileItem = iterator.next();
			if (!fileItem.isFormField()) {
				StringBuilder sb = new StringBuilder();
				sb.append(dirName);
				sb.append(File.separator);
				url.append("/");
				url.append(dirName);

				String ymd = XTime.format(System.currentTimeMillis(), "yyyyMMdd");
				sb.append(ymd);
				sb.append(File.separator);
				url.append(ymd);
				url.append("/");
				File dirFile = new File(sb.toString());
				if (!dirFile.exists()) {
					dirFile.mkdirs();
				}

				String fileName = fileItem.getName();
				long fileSize = fileItem.getSize();
				if (!checkFileSize(dirName, fileSize)) {
					response.getWriter().write(getError("上传文件大小超过限制。"));
					return;
				}

				String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
				if (!checkFileExt(dirName, fileExt)) {
					response.getWriter().write(getError("上传文件扩展名是不允许的扩展名。只允许" + extMap.get(dirName) + "格式。"));
					return;
				}

				String newFileName = XUtils.getUUID() + Base64.encode(fileName.getBytes("UTF-8"));
				sb.append(newFileName);
				sb.append(".").append(fileExt);
				url.append(newFileName);
				url.append(".").append(fileExt);

				File file = new File(sb.toString());
				try {
					fileItem.write(file);
				} catch (Exception e) {
					response.getWriter().write(getError("上传失败"));
					return;
				}

				Map<String, Object> jsonObject = new HashMap<String, Object>();
				jsonObject.put("error", 0);
				jsonObject.put("url", url.toString());
				response.getWriter().write(JSONUtils.toJSONString(jsonObject));
				return;
			}
		}
	}

	public void manager(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String dirName = request.getParameter("dir");
		if (dirName != null) {
			if (extMap.containsKey(dirName)) {
				response.getWriter().write("Invalid Directory name.");
				return;
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append(rootPath);
		sb.append("/");
		sb.append(dirName);
		sb.append("/");
		// TODO
	}

	private String getError(String message) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("error", 1);
		map.put("message", message);
		return JSONUtils.toJSONString(map);
	}
}
