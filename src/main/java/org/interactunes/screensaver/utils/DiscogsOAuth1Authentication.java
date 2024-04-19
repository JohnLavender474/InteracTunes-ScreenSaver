package org.interactunes.screensaver.utils;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class DiscogsOAuth1Authentication {

    private static final String CONSUMER_KEY = "YOUR_CONSUMER_KEY";
    private static final String CONSUMER_SECRET = "YOUR_CONSUMER_SECRET";

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        // Step 1: Obtain a request token
        OAuth10aService service = new ServiceBuilder(CONSUMER_KEY)
                .apiSecret(CONSUMER_SECRET)
                .build(new DefaultApi10a() {
                    @Override
                    public String getRequestTokenEndpoint() {
                        return "";
                    }

                    @Override
                    public String getAccessTokenEndpoint() {
                        return "";
                    }

                    @Override
                    protected String getAuthorizationBaseUrl() {
                        return "";
                    }
                });

        OAuth1RequestToken requestToken = service.getRequestToken();
        String authUrl = service.getAuthorizationUrl(requestToken);

        // Manual step: Authorize the request token by opening the authorization URL in a web browser
        System.out.println("Authorize the request token by visiting: ");
        System.out.println(authUrl);
        System.out.println("And enter the verification code:");
        Scanner in = new Scanner(System.in);
        String verifier = in.nextLine();

        // Step 2: Exchange the authorized request token for an access token
        OAuth1AccessToken accessToken = service.getAccessToken(requestToken, verifier);

        // Print access token and token secret
        System.out.println("Access Token: " + accessToken.getToken());
        System.out.println("Access Token Secret: " + accessToken.getTokenSecret());
    }
}

