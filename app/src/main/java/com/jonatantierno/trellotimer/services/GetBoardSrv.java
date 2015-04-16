package com.jonatantierno.trellotimer.services;

import com.jonatantierno.trellotimer.Item;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by jonatan on 14/04/15.
 */
public interface GetBoardSrv {
    @GET("/1/boards/{id}")
    void getBoard(@Query("key") String appKey, @Path("id") String boardId, Callback<Item> callback);
}
