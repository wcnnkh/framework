package io.basc.framework.util.actor;

import io.basc.framework.util.Publisher;

public interface Pipeline<T> extends Pollable<T>, Publisher<T> {
}
