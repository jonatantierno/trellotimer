package com.jonatantierno.trellotimer.services;

import com.jonatantierno.trellotimer.Item;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * This Service obtains the  board withfrom a given user.
 * Created by jonatan on 14/04/15.
 */
public interface GetBoardSrv {
    @GET("/1/lists/{listId}/cards")
    void getCards(@Query("key") String appKey,
                  @Query("token") String oauthToken,
                  @Path("listId") String listId,
                  Callback<List<Item>> callback);
}
