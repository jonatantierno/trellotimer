package com.jonatantierno.trellotimer.services;

import retrofit.Callback;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * This Service adds a comment to a task.
 * Created by jonatan on 14/04/15.
 */
public interface AddCommentSrv {
    @POST("/1/cards/{id}/actions/comments")
    void addComment(@Query("key") String appKey,
                    @Query("token") String oauthToken,
                    @Path("id") String cardId,
                    @Query("text") String comment,
                    Callback<Void> callback);
}
