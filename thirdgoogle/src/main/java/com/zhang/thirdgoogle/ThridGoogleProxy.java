package com.zhang.thirdgoogle;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Created by zhangyuncai on 2019/7/22.
 */
public class ThridGoogleProxy implements GoogleApiClient.OnConnectionFailedListener {
    private FragmentActivity activity;
    private static ThridGoogleProxy proxy;
    private static ThridGoogleCallback callback;

    public static void loginGoogle(FragmentActivity activity, ThridGoogleCallback callback) {
        if (proxy == null) {
            proxy = new ThridGoogleProxy(activity, callback);
        }
        proxy.signOut();
        proxy.signIn();
    }

    private ThridGoogleProxy(FragmentActivity activity, ThridGoogleCallback callback) {
        this.activity = activity;
        this.callback = callback;
        iniGoogleApiClient();
    }

    /*---------------------->>>>  三方登录/谷歌 start <<<<<<<<<<-----------------*/
    private GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 10001;

    private void iniGoogleApiClient() {
        if (mGoogleApiClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestId()
                    //签名前先屏蔽，因为导致登录失败，然后也会导致签名后goole登录失败
                    //                .            requestIdToken(getString(R.string.default_web_client_id))  //获取token
                    .requestProfile()
                    .build();

            mGoogleApiClient = new GoogleApiClient
                    .Builder(activity)
                    .enableAutoManage(activity, this)/* FragmentActivity *//* OnConnectionFailedListener */
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
    }

    private void signIn() {
//        showLoadingBar("正在登陆中...");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * 退出
     */
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
//        dismissLoadingBar();
//        Timber.d("----------->>>>>>>>-----------" + "handleSignInResult:" + result.isSuccess());
        Log.i("robin", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            Log.e("robin", "成功");
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                //登陆成功后就开始注销
                signOut();
                if (callback != null) {
                    callback.result(acct);
                } else {
                    String userName = String.format("用户名是:%s", acct.getDisplayName());
                    String email = String.format("用户email是:%s", acct.getEmail());
                    String photoUrl = String.format("用户头像是:%s", acct.getPhotoUrl());
                    String userId = String.format("用户Id是:%s", acct.getId());
                    String userIdToken = String.format("用户IdToken是", acct.getIdToken());
                    System.out.println(userName + email + photoUrl + userId + userIdToken);
                }
//                Timber.d("----------->>>>>>>>-----------" + "用户名是:" + acct.getDisplayName());
//                Timber.d("----------->>>>>>>>-----------" + "用户email是:" + acct.getEmail());
//                Timber.d("----------->>>>>>>>-----------" + "用户头像是:" + acct.getPhotoUrl());
//                Timber.d("----------->>>>>>>>-----------" + "用户Id是:" + acct.getId());
//                Timber.d("----------->>>>>>>>-----------" + "用户IdToken是:" + acct.getIdToken());
//                ThirdInfoModel thirdInfoModel = new ThirdInfoModel();
//                thirdInfoModel.setName(acct.getDisplayName());
//                if (null != acct.getPhotoUrl()) {
//                    thirdInfoModel.setIconurl(acct.getPhotoUrl().toString());
//                }
//                thirdInfoModel.setGender(data.get("gender"));
//                thirdInfoModel.setOpenid(acct.getId());
//                thirdInfoModel.setUid(acct.getId());
//                thirdInfoModel.setType("2");
//                thirdInfoModel.setEmail(acct.getEmail());
//                getPresenter().loginByThird(thirdInfoModel, LoginActivity.this);

            }
        } else {
//            ToastUtils.showToast("登录失败" + result.getStatus());
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        dismissLoadingBar();
    }
    /*---------------------->>>>  三方登录/谷歌 end <<<<<<<<<<-----------------*/

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        //谷歌登录成功回调
        if (requestCode == RC_SIGN_IN) {
//            dismissLoadingBar();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            proxy.handleSignInResult(result);
        }
    }
}
