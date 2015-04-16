package com.jonatantierno.trellotimer.services;

import com.jonatantierno.trellotimer.Item;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by jonatan on 14/04/15.
 */
public interface GetAllListsSrv {
    @GET("/1/boards/{boardId}/lists?fields=name")
    void listBoards(@Query("key") String appKey,
                    @Query("token") String oauthToken,
                    @Path("boardId") String boardId, Callback<List<Item>> callback);
}
