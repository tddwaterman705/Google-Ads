package com.learning.googleads.api.auth;

import com.learning.googleads.api.auth.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;

    @GetMapping("/connect")
    public String connect() throws Exception {
        return oAuthService.getAuthorizationUrl();
    }

    @GetMapping("/oauth2callback")
    public String oauth2Callback(@RequestParam("code") String code) throws Exception {
        String refreshToken = oAuthService.getRefreshToken(code);
        return "Refresh Token: " + refreshToken;
    }
}