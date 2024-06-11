package com.learning.googleads.api.auth;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory; 
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collections;

@Service
public class OAuthService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
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

        return flow.newAuthorizationUrl()
            .setRedirectUri(getRedirectUri())
            .build();
    }

    private DataStoreFactory getDataStoreFactory() throws Exception {
        return MemoryDataStoreFactory.getDefaultInstance();
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
        return "http://localhost:8080/callback";
    }

    public Credential getCredential() throws Exception {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(new FileInputStream(clientSecretPath)));

        AuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, Collections.singleton(SCOPES))
                .setDataStoreFactory(getDataStoreFactory())
                .setAccessType("offline")
                .build();

        return flow.loadCredential("user");
    }

    public GoogleClientSecrets getClientSecrets() throws Exception {
        return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(new FileInputStream(clientSecretPath)));
    }
}