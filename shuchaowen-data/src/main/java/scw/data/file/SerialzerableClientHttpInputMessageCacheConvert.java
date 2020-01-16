package scw.data.file;

import scw.core.Converter;
import scw.net.http.HttpUtils;
import scw.net.http.client.SerialzerableClientHttpInputMessage;

public class SerialzerableClientHttpInputMessageCacheConvert implements Converter<String, SerialzerableClientHttpInputMessage> {

	public SerialzerableClientHttpInputMessage convert(String url) throws Exception {
		return HttpUtils.getHttpClient().getToSerialzerableInputMessage(url);
	}
}
