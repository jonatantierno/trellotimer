package com.jonatantierno.trellotimer.services;

import com.jonatantierno.trellotimer.Board;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by jonatan on 14/04/15.
 */
public interface GetAllBoardsSrv {
    @GET("/1/members/me/boards?fields=name")
    void listBoards(@Query("key")String appKey, @Query("token") String oauthToken, Callback<List<Board>> callback);
}
