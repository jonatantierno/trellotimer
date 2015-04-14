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
    @GET("/1/boards")
    void listBoards(@Query("key")String appKey, Callback<List<Board>> callback);
}
