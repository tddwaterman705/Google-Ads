package com.learning.googleads.api.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.auth.oauth2.UserCredentials;

@Component
public class GoogleAdsClientFactory {
     @Value("${google.ads.developer-token}")
    private String developerToken;

    @Value("${google.ads.login-customer-id:}")
    private String loginCustomerId;

    private final OAuthService oAuthService;
    private GoogleAdsClient cachedClient;

    @Autowired
    public GoogleAdsClientFactory(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    public GoogleAdsClient createGoogleAdsClient() throws Exception {
        if(cachedClient == null){
        Credential credential = oAuthService.getCredential();
        GoogleClientSecrets clientSecrets = oAuthService.getClientSecrets();

        UserCredentials userCredentials = UserCredentials.newBuilder()
                .setClientId(clientSecrets.getDetails().getClientId())
                .setClientSecret(clientSecrets.getDetails().getClientSecret())
                .setRefreshToken(credential.getRefreshToken())
                .build();

        GoogleAdsClient.Builder builder = GoogleAdsClient.newBuilder()
                .setCredentials(userCredentials)
                .setDeveloperToken(developerToken);

        if (loginCustomerId != null && !loginCustomerId.isEmpty()) {
            builder.setLoginCustomerId(Long.parseLong(loginCustomerId));
        }

        cachedClient = builder.build();
        return cachedClient;
    }
    return cachedClient;
}
}
