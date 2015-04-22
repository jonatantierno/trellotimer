package com.jonatantierno.trellotimer;

import com.google.api.client.auth.oauth2.Credential;
import com.jonatantierno.trellotimer.services.AddCommentSrv;
import com.jonatantierno.trellotimer.services.GetAllBoardsSrv;
import com.jonatantierno.trellotimer.services.GetAllListsSrv;
import com.jonatantierno.trellotimer.services.GetTasksSrv;
import com.jonatantierno.trellotimer.services.MoveToListSrv;

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

    public final StatusStore store;
    public TTConnections(StatusStore statusStore) {
        store = statusStore;
    }

    public void getBoards(final CredentialFactory credentialFactory, final TTCallback<List<Item>> callback){
        new Thread(new Runnable(){
            @Override
            public void run() {
                Credential credential = getCredential(credentialFactory, callback);

                RestAdapter boardRestAdapter = buildAdapter();
                GetAllBoardsSrv getAllBoardsSrv = boardRestAdapter.create(GetAllBoardsSrv.class);

                getAllBoardsSrv.getBoards(CredentialFactory.CLIENT_ID, credential.getAccessToken(), new Callback<List<Item>>() {
                    @Override
                    public void success(List<Item> list, Response response) {
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


    public void getLists(final CredentialFactory credentialFactory, final TTCallback<List<Item>> callback) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                Credential credential = getCredential(credentialFactory, callback);

                RestAdapter boardRestAdapter = buildAdapter();
                GetAllListsSrv getAllListsSrv = boardRestAdapter.create(GetAllListsSrv.class);

                final String boardId = store.getBoardId();

                assert boardId != null;

                getAllListsSrv.getLists(CredentialFactory.CLIENT_ID, credential.getAccessToken(), boardId, new Callback<List<Item>>() {
                    @Override
                    public void success(List<Item> list, Response response) {
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

    public void getTasks(final ListType listType, final CredentialFactory credentialFactory, final TTCallback<List<Item>> callback) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                Credential credential = getCredential(credentialFactory, callback);

                RestAdapter boardRestAdapter = buildAdapter();
                GetTasksSrv getTasksSrv = boardRestAdapter.create(GetTasksSrv.class);

                final String listId = store.getListId(listType);

                assert listId != null;

                getTasksSrv.getCards(CredentialFactory.CLIENT_ID, credential.getAccessToken(), listId, new Callback<List<Item>>() {
                    @Override
                    public void success(List<Item> list, Response response) {
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


    public void moveToList(final String cardId, final ListType listType, final CredentialFactory credentialFactory, final TTCallback<List<Item>> callback) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                Credential credential = getCredential(credentialFactory, callback);

                RestAdapter boardRestAdapter = buildAdapter();
                MoveToListSrv srv = boardRestAdapter.create(MoveToListSrv.class);

                final String listId = store.getListId(listType);

                assert listId != null;

                srv.moveToList(CredentialFactory.CLIENT_ID, credential.getAccessToken(), cardId, listId, new Callback<Void>() {
                    @Override
                    public void success(Void v, Response response2) {
                        callback.success(null);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                });
            }
        }).start();
    }

    public void addComment(final String cardId, final String comment, final CredentialFactory credentialFactory, final TTCallback<List<Item>> callback) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                Credential credential = getCredential(credentialFactory, callback);

                RestAdapter boardRestAdapter = buildAdapter();
                AddCommentSrv srv = boardRestAdapter.create(AddCommentSrv.class);

                srv.addComment(CredentialFactory.CLIENT_ID, credential.getAccessToken(), cardId, comment, new Callback<Void>() {
                    @Override
                    public void success(Void v, Response response2) {
                        callback.success(null);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                });
            }
        }).start();
    }

    private Credential getCredential(CredentialFactory credentialFactory, TTCallback<List<Item>> callback) {
        Credential credential = null;
        try {
            credential = credentialFactory.getCredential();
        } catch (IOException e) {
            callback.failure(e);
        }
        assert credential !=null;
        return credential;
    }

    private RestAdapter buildAdapter() {
        return new RestAdapter.Builder().setEndpoint(BASE_TRELLO_URL).build();
    }

}
