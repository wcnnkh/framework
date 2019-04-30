package scw.core.net.response;

import java.io.InputStream;
import java.net.URLConnection;

import scw.core.io.ByteArray;
import scw.core.net.AbstractResponse;
import scw.core.utils.IOUtils;

public final class ByteArrayResponse extends AbstractResponse<ByteArray> {

	@Override
	protected ByteArray doInput(URLConnection urlConnection, InputStream is) throws Throwable {
		return IOUtils.read(is, 1024, -1);
	}

}
