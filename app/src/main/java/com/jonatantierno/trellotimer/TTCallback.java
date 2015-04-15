package com.jonatantierno.trellotimer;

import java.util.List;

/**
 * Created by jonatan on 15/04/15.
 */
public interface TTCallback<T> {
    void success(T result);

    void failure(Throwable cause);
}
