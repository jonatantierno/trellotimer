package com.jonatantierno.trellotimer.services;

import com.jonatantierno.trellotimer.Item;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * This Service obtains all the tasks in a list.
 * Created by jonatan on 14/04/15.
 */
public interface GetTasksSrv {
    @GET("/1/lists/{listId}/cards")
    void getCards(@Query("key") String appKey,
                  @Query("token") String oauthToken,
                  @Path("listId") String listId,
                  Callback<List<Item>> callback);
}
