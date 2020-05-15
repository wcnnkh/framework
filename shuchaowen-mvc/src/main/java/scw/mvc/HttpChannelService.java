package scw.mvc;

import scw.net.http.server.mvc.HttpChannel;

public interface HttpChannelService{
	void service(HttpChannel httpChannel);
}
