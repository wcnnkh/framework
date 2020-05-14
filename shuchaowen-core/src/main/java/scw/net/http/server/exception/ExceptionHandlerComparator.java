package scw.net.http.server.exception;

import java.util.Comparator;

public class ExceptionHandlerComparator implements Comparator<HttpServerExceptionHandler> {

	public int compare(HttpServerExceptionHandler o1, HttpServerExceptionHandler o2) {
		for (Class<? extends Throwable> type1 : o1.getSupports()) {
			for (Class<? extends Throwable> type2 : o2.getSupports()) {
				if (type1.isAssignableFrom(type2)) {
					return 1;
				}
			}
		}
		return -1;
	}

}
