package com.jonatantierno.trellotimer;

import android.support.v7.app.ActionBarActivity;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.jackson.JacksonFactory;
import com.wuman.android.auth.AuthorizationFlow;
import com.wuman.android.auth.AuthorizationUIController;
import com.wuman.android.auth.DialogFragmentController;
import com.wuman.android.auth.OAuthManager;
import com.wuman.android.auth.oauth2.store.SharedPreferencesCredentialStore;

import java.io.IOException;


/**
 * Created by jonatan on 14/04/15.
 */
public class CredentialFactory {
    public static final String CLIENT_ID = "2cb6e34fcc644d0cc36f7b4751ed3a90";
    public static final String CLIENT_SECRET = "10436d346b0d223043dbca4baac8dd7a88bcbfd51194999ba80fd5d348c13f84";
    public static final String OAUTH_USER_ID = "trello";

    public static final String OAUTH_STORE_NAME = "oauth_store";
    public static final String ACCESS_TOKEN_URL = "https://trello.com/1/OAuthGetAccessToken";
    public static final String AUTHORIZE_TOKEN_URL = "https://trello.com/1/OAuthAuthorizeToken";
    public static final String REQUEST_TOKEN_URL = "https://trello.com/1/OAuthGetRequestToken";

    private OAuthManager oAuthManager;

    public void init(ActionBarActivity context) {
        // setup credential store
        SharedPreferencesCredentialStore credentialStore =
                new SharedPreferencesCredentialStore(context,
                        OAUTH_STORE_NAME, new JacksonFactory());
        // setup authorization flow
        AuthorizationFlow.Builder flowBuilder = new AuthorizationFlow.Builder(
                BearerToken.authorizationHeaderAccessMethod(),
                AndroidHttp.newCompatibleTransport(),
                new JacksonFactory(),
                new GenericUrl(ACCESS_TOKEN_URL),
                new ClientParametersAuthentication(CLIENT_ID,CLIENT_SECRET),
                CLIENT_ID,
                AUTHORIZE_TOKEN_URL)
                .setTemporaryTokenRequestUrl(REQUEST_TOKEN_URL)
                .setCredentialStore(credentialStore);

        AuthorizationUIController controller =
                new DialogFragmentController(context.getFragmentManager()) {

                    @Override
                    public String getRedirectUri() throws IOException {
                        return "http://localhost/Callback";
                    }

                    @Override
                    public boolean isJavascriptEnabledForWebView() {
                        return true;
                    }

                };

        oAuthManager = new OAuthManager(flowBuilder.build(), controller);
    }

    public Credential getCredential() throws IOException {
        return oAuthManager.authorize10a(OAUTH_USER_ID, null, null).getResult();
    }

}
