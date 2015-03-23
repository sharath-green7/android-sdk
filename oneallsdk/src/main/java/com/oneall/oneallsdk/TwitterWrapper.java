package com.oneall.oneallsdk;

import android.app.Activity;
import android.content.Intent;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

/**
 * Class used to take care of Twitter authentication
 */
public class TwitterWrapper {

    // region Helper classes and interfaces

    public interface LoginComplete {
        void success(String accessToken, String secret);
        void failure(OAError error);
    }

    // endregion

    // region Properties

    private TwitterAuthClient client;

    // endregion

    // region Lifecycle

    private static TwitterWrapper mInstance = null;

    private TwitterWrapper() {
        client = new TwitterAuthClient();
    }

    public static TwitterWrapper getInstance() {
        if (mInstance == null) {
            synchronized (TwitterWrapper.class) {
                if (mInstance == null) {
                    mInstance = new TwitterWrapper();
                }
            }
        }
        return mInstance;
    }

    // endregion

    // region Interface methods

    public void login(Activity activity, final LoginComplete callback) {
        client.authorize(activity, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                if (callback != null) {
                    callback.success(
                            twitterSessionResult.data.getAuthToken().token,
                            twitterSessionResult.data.getAuthToken().secret);
                }
            }

            @Override
            public void failure(TwitterException e) {
                if (callback != null) {
                    callback.failure(
                            new OAError(OAError.ErrorCode.OA_ERROR_AUTH_FAIL, e.getMessage()));
                }
            }
        });
    }

    /**
     * should be called by the user to process response callbacks from Twitter window
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        client.onActivityResult(requestCode, resultCode, data);
    }

    // endregion
}