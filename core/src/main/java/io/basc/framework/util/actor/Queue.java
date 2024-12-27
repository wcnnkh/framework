package io.basc.framework.util.actor;

import io.basc.framework.util.exchange.Publisher;

public interface Queue<T> extends Pollable<T>, Publisher<T> {
}
