package scw.integration.sms;

import java.util.concurrent.Future;

import scw.beans.annotation.AutoImpl;

@AutoImpl({ DefaultAsyncShortMessageService.class })
public interface AsyncShortMessageService<M, V> extends ShortMessageService<M, Future<V>> {
}