package com.jonatantierno.trellotimer.trellorequests;

import java.util.List;

/**
 * Callback used to return the results of TTConnection methods.
 * Created by jonatan on 15/04/15.
 */
public interface TTCallback<T> {
    void success(T result);

    void failure(Throwable cause);
}
