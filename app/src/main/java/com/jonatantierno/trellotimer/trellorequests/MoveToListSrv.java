package com.jonatantierno.trellotimer.trellorequests;

import retrofit.Callback;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * This Service moves a task to a different list.
 * Created by jonatan on 14/04/15.
 */
public interface MoveToListSrv {
    @PUT("/1/cards/{id}/idList")
    void moveToList(@Query("key") String appKey,
                    @Query("token") String oauthToken,
                    @Path("id") String cardId,
                    @Query("value") String listId,
                    Callback<Void> callback);

}
