package com.jonatantierno.trellotimer;

import android.util.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.jonatantierno.trellotimer.services.GetAllBoardsSrv;

import java.io.IOException;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Performs the connections to the Trello API via Retrofit.
 * Created by jonatan on 15/04/15.
 */
public class TTConnections {
    public static final String BASE_TRELLO_URL = "https://api.trello.com";

    public void getBoards(final CredentialFactory credentialFactory, final TTCallback<List<Board>> callback){
        new Thread(new Runnable(){
            @Override
            public void run() {
                Credential credential = null;
                try {
                    credential = credentialFactory.getCredential();
                } catch (IOException e) {
                    callback.failure(e);
                }
                assert credential !=null;

                RestAdapter boardRestAdapter = new RestAdapter.Builder().setEndpoint(BASE_TRELLO_URL).build();
                GetAllBoardsSrv getAllBoardsSrv = boardRestAdapter.create(GetAllBoardsSrv.class);

                getAllBoardsSrv.listBoards(CredentialFactory.CLIENT_ID, credential.getAccessToken(), new Callback<List<Board>>() {
                    @Override
                    public void success(List<Board> list, Response response) {
                        callback.success(list);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error.getCause());
                    }
                });
            }
        }).start();

    }
}
