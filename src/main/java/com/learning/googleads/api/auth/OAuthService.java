package com.learning.googleads.api.auth;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collections;

@Service
public class OAuthService {

    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String SCOPES = "https://www.googleapis.com/auth/adwords";

    @Value("${google.client.secret.path}")
    private String clientSecretPath;

    public String getAuthorizationUrl() throws Exception {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(new FileInputStream(clientSecretPath)));

        AuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, Collections.singleton(SCOPES))
                .setDataStoreFactory(getDataStoreFactory())
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(3000).build();
        AuthorizationCodeInstalledApp app = new AuthorizationCodeInstalledApp(flow, receiver);
        Credential credential = app.authorize("user");

        return receiver.getRedirectUri();
    }

    private DataStoreFactory getDataStoreFactory() throws Exception {
        File tokensDirectory = new File(TOKENS_DIRECTORY_PATH);
        return new FileDataStoreFactory(tokensDirectory);
    }

    public String getRefreshToken(String code) throws Exception {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(new FileInputStream(clientSecretPath)));

        AuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, Collections.singleton(SCOPES))
                .setDataStoreFactory(getDataStoreFactory())
                .setAccessType("offline")
                .build();

        TokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(getRedirectUri()).execute();
        Credential credential = flow.createAndStoreCredential(tokenResponse, "user");

        return credential.getRefreshToken();
    }

    private String getRedirectUri() {
        return "http://localhost:3000/";
    }
}
