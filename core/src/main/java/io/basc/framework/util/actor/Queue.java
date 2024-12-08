package io.basc.framework.util.actor;

import io.basc.framework.util.Publisher;

public interface Queue<T> extends Pollable<T>, Publisher<T> {
}
