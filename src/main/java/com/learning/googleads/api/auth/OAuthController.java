package com.learning.googleads.api.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;

    @GetMapping("/auth")
    public String auth() throws Exception {
        return oAuthService.getAuthorizationUrl();
    }

    @GetMapping("/callback")
    public String oauth2Callback(@RequestParam("code") String code) throws Exception {
        String refreshToken = oAuthService.getRefreshToken(code);
        return "Refresh Token: " + refreshToken;
    }
}