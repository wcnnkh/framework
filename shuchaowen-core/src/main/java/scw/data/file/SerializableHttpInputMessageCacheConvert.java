package scw.data.file;

import scw.core.Converter;
import scw.http.HttpUtils;
import scw.net.message.SerializableInputMessage;

public class SerializableHttpInputMessageCacheConvert implements
		Converter<String, SerializableInputMessage> {

	public SerializableInputMessage convert(String url) {
		return HttpUtils.getHttpClient().getSerializableHttpInputMessage(url,
				null);
	}
}
