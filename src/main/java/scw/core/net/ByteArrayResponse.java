package scw.core.net;

import java.io.InputStream;
import java.net.URLConnection;

import scw.core.io.ByteArray;
import scw.core.io.IOUtils;

public final class ByteArrayResponse extends AbstractResponse<ByteArray> {

	@Override
	protected ByteArray doInput(URLConnection urlConnection, InputStream is) throws Throwable {
		return IOUtils.read(is, 1024, -1);
	}

}
