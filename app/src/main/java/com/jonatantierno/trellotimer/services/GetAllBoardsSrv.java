package com.jonatantierno.trellotimer.services;

import com.jonatantierno.trellotimer.Item;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * This service obtains all the boards of an user.
 * Created by jonatan on 14/04/15.
 */
public interface GetAllBoardsSrv {
    @GET("/1/members/me/boards?fields=name")
    void getBoards(@Query("key") String appKey, @Query("token") String oauthToken, Callback<List<Item>> callback);
}
