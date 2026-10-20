package com.vinicius.payments.payments_api.shared.application.port;

import java.util.function.Supplier;

/**
 * Runs a unit of work atomically. Implementations may re-run the whole unit of work
 * when it fails because of a concurrent update, so it must be safe to repeat.
 */
public interface TransactionRunner {

    <T> T run(Supplier<T> work);
}
