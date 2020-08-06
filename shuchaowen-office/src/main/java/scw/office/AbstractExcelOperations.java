package scw.office;

import java.io.IOException;
import java.io.OutputStream;

import scw.core.utils.StringUtils;
import scw.net.FileMimeTypeUitls;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.message.OutputMessage;

public abstract class AbstractExcelOperations implements ExcelOperations {
	private static final MimeType DEFAULT_CONTENT_TYPE = MimeTypeUtils.parseMimeType("application/vnd.ms-excel");

	public ExcelExport createExport(OutputStream outputStream) throws IOException, ExcelException {
		return createExport(outputStream, 0, 0);
	}

	public ExcelExport createExport(OutputStream outputStream, int sheetIndex, int beginRowIndex)
			throws IOException, ExcelException {
		return DefaultExcelExport.createExcelExport(create(outputStream), sheetIndex, beginRowIndex);
	}

	public ExcelExport createExport(OutputMessage outputMessage, String fileName) throws IOException {
		String fileNameToUse = StringUtils.containsChinese(fileName) ? new String(fileName.getBytes(), "iso-8859-1")
				: fileName;
		MimeType mimeType = FileMimeTypeUitls.getMimeType(fileName);
		if (mimeType == null) {
			outputMessage.setContentType(DEFAULT_CONTENT_TYPE);
		} else {
			outputMessage.setContentType(mimeType);
		}
		outputMessage.getHeaders().set("Content-Disposition", "attachment;filename=" + fileNameToUse);
		return createExport(outputMessage.getBody());
	}
}
