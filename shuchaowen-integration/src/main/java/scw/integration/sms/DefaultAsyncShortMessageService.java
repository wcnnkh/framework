package scw.integration.sms;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import scw.util.PhoneNumber;

public final class DefaultAsyncShortMessageService<M, V> implements AsyncShortMessageService<M, V> {
	private final ShortMessageService<M, V> shortMessageService;
	private final ExecutorService executorService;

	public DefaultAsyncShortMessageService(ShortMessageService<M, V> texted, ExecutorService executorService) {
		this.shortMessageService = texted;
		this.executorService = executorService;
	}

	public Future<V> send(M message, Collection<PhoneNumber> phoneNumbers) throws SmsException {
		return executorService.submit(new AsyncBatchExecute(message, phoneNumbers));
	}

	private final class AsyncBatchExecute implements Callable<V> {
		private M message;
		private Collection<PhoneNumber> phoneNumbers;

		public AsyncBatchExecute(M message, Collection<PhoneNumber> phoneNumbers) {
			this.message = message;
			this.phoneNumbers = phoneNumbers;
		}

		public V call() throws Exception {
			return shortMessageService.send(message, phoneNumbers);
		}
	}
}
