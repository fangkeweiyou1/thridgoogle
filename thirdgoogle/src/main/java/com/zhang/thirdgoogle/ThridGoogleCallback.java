package com.zhang.thirdgoogle;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by zhangyuncai on 2019/8/8.
 */
public interface ThridGoogleCallback {
    void result(GoogleSignInAccount acct);
}
