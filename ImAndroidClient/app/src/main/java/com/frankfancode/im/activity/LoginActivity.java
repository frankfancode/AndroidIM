package com.frankfancode.im.activity;

/**
 * Created by Frank on 2016/1/5.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.frankfancode.im.BuildConfig;
import com.frankfancode.im.ImAPPlication;
import com.frankfancode.im.R;
import com.frankfancode.im.api.NetApi;
import com.frankfancode.im.bean.Result;
import com.frankfancode.im.bean.UserInfo;
import com.frankfancode.im.net.RequestManager;
import com.frankfancode.im.utils.DialogUtils;
import com.frankfancode.im.utils.StringUtils;
import com.github.lazylibrary.util.ToastUtils;
import com.orhanobut.logger.Logger;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    @Bind(R.id.et_username)
    EditText et_username;
    @Bind(R.id.et_password)
    EditText et_password;
    @Bind(R.id.btn_login)
    Button btn_login;
    @Bind(R.id.link_signup)
    TextView link_signup;
    private Context context = this;
    private Activity activity = this;
    private Dialog mLoginingDlg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initViews();
        initDatas();

    }

    private void initDatas() {
        UserInfo userinfo = ImAPPlication.getSelf().getUserInfo();
        if (TextUtils.isEmpty(userinfo.userid)) {
            et_username.setText(userinfo.username);
            et_password.setText(userinfo.password);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                UserInfo userInfo = ImAPPlication.getSelf().getUserInfo();
                et_username.setText(userInfo.username);
                et_password.setText(userInfo.password);

                login();
            }
        }
    }

    private void initViews() {

    }


    @OnClick(R.id.btn_login)
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }



        /*final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Base_Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.logging));
        progressDialog.show();*/

        String username = et_username.getText().toString();
        String password = et_password.getText().toString();

        HashMap params = new HashMap();
        params.put("action", "login");
        params.put("username", username);
        params.put("password", password);


        Request loginRequest = NetApi.getResultRequest(params, UserInfo.class, new NetApi.NetListener<Result>() {
            @Override
            public void onPreStart() {
                btn_login.setEnabled(false);
                DialogUtils.showProcessDialog(activity, getString(R.string.logging));
            }

            @Override
            public void onPreResponse() {
                btn_login.setEnabled(true);
                DialogUtils.dismissProcessDialog();
                if (BuildConfig.DEBUG) {
                    String username = et_username.getText().toString();
                    String password = et_password.getText().toString();
                    UserInfo userinfo = new UserInfo() ;
                    userinfo.userid="100001";
                    userinfo.username=username;
                    userinfo.password=password;

                    ImAPPlication.getSelf().setUserInfo(userinfo);
                    Intent intent = new Intent(context, ChatHomeActivity.class);
                    startActivity(intent);
                    finish();
                }


            }

            @Override
            public void onSuccess(Result response) {
                Result result = response;


                if (1 == result.code) {

                    try {
                        UserInfo userinfo = (UserInfo) result.data;
                        ImAPPlication.getSelf().setUserInfo(userinfo);
                        Intent intent = new Intent(context, ChatHomeActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        Logger.e(e.getMessage());
                    }
                } else {
                    ToastUtils.showToast(context, result.message);
                }


            }


            @Override
            public void onJustResponse(String response) {

            }

            @Override
            public void onError(Exception e) {
                Logger.e(e.getMessage());
                ToastUtils.showToast(context, "连接服务器失败");
            }


        });

        RequestManager.getInstance().addRequest(loginRequest, context);

/*        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);*/
    }

    @OnClick(R.id.link_signup)
    public void signUp() {
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);

    }


    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        btn_login.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        btn_login.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (username.isEmpty() || !StringUtils.checkUsername(username)) {
            et_username.setError(getString(R.string.error_invalid_username));
            valid = false;
        } else {
            et_username.setError(null);
        }

        if (password.isEmpty() || !StringUtils.checkPassword(password)) {
            et_password.setError(getString(R.string.error_invalid_password));
            valid = false;
        } else {
            et_password.setError(null);
        }

        return valid;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RequestManager.getInstance().cancelAll(this);
    }
}