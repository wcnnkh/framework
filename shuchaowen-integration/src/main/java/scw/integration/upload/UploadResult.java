package scw.integration.upload;

import java.io.Serializable;

public interface UploadResult extends Serializable{
	String getUrl();
	
	long getSize();
	
	String getFileName();
}
 