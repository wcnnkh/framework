package scw.http.multipart;

import java.io.IOException;
import java.util.List;

import scw.http.HttpInputMessage;

public interface FileItemParser {
	List<FileItem> parse(HttpInputMessage httpInputMessage) throws IOException;
}