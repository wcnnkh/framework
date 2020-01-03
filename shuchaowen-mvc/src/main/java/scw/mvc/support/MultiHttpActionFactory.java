package scw.mvc.support;

import java.util.LinkedList;

import scw.mvc.Action;
import scw.mvc.http.HttpChannel;

public class MultiHttpActionFactory extends HttpActionFactory {
	private final LinkedList<HttpActionFactory> factoryList = new LinkedList<HttpActionFactory>();

	public final LinkedList<HttpActionFactory> getFactoryList() {
		return factoryList;
	}

	@Override
	protected Action getAction(HttpChannel httpChannel) {
		for (HttpActionFactory httpActionFactory : factoryList) {
			if (httpActionFactory == null) {
				continue;
			}

			Action action = httpActionFactory.getAction(httpChannel);
			if (action != null) {
				return action;
			}
		}
		return null;
	}

	@Override
	public void scanning(HttpAction httpAction, HttpControllerConfig httpControllerConfig) {
		for (HttpActionFactory httpActionFactory : factoryList) {
			if (httpActionFactory == null) {
				continue;
			}

			httpActionFactory.scanning(httpAction, httpControllerConfig);
		}
	}
}
