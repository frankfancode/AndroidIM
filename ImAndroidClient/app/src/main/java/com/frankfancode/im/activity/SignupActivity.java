package com.frankfancode.im.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.frankfancode.im.ImAPPlication;
import com.frankfancode.im.R;
import com.frankfancode.im.api.NetApi;
import com.frankfancode.im.bean.Result;
import com.frankfancode.im.bean.UserInfo;
import com.frankfancode.im.net.RequestManager;
import com.frankfancode.im.utils.FLog;
import com.frankfancode.im.utils.StringUtils;
import com.frankfancode.im.utils.Utils;
import com.github.lazylibrary.util.ToastUtils;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Frank on 2016/1/5.
 */
public class SignupActivity extends BaseActivity {
    private static final String TAG = "SignupActivity";
    @Bind(R.id.input_name)
    EditText input_name;
    @Bind(R.id.et_password)
    EditText input_password;
    @Bind(R.id.btn_signup)
    Button btn_signup;
    @Bind(R.id.link_login)
    TextView link_login;
    private Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

    }


    @OnClick(R.id.link_login)
    public void gotoLogin() {
        finish();
    }


    @OnClick(R.id.btn_signup)
    public void signUp() {
        FLog.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }


        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.Base_Theme_AppCompat_Dialog);


        final String username = input_name.getText().toString();
        String password = input_password.getText().toString();

        Map<String, String> param = new HashMap<>();
        param.put("action", "signup");
        param.put("username", username);
        param.put("password", password);


        Request request = NetApi.getResultRequest(param, UserInfo.class, new NetApi.NetListener<Result>() {
            @Override
            public void onPreStart() {
                btn_signup.setEnabled(false);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Creating Account...");
                progressDialog.show();
            }

            @Override
            public void onPreResponse() {
                btn_signup.setEnabled(true);
                progressDialog.dismiss();
            }


            @Override
            public void onSuccess(Result result) {
                Logger.i(result.toString());
                if (null != result) {
                    if (1 == result.code) {
                        //注册成功就把当前用户的账号和密码存储到配置文件中
                        try {
                            UserInfo userinfo = (UserInfo) result.data;
                            ImAPPlication.getSelf().setUserInfo(userinfo);
                            setResult(RESULT_OK);
                            finish();
                        } catch (Exception e) {
                            Logger.e(e.getMessage());
                        }


                    } else {

                    }

                    ToastUtils.showToast(context, result.message);
                } else {
                    ToastUtils.showToast(context, getString(R.string.error_signup_failed));
                }
            }

            @Override
            public void onJustResponse(String respones) {
                Logger.e(respones);
                ToastUtils.showToast(context, getString(R.string.error_signup_failed));
                ToastUtils.showToast(context, "");
            }

            @Override
            public void onError(Exception e) {
                Logger.e(e.getMessage());
                ToastUtils.showToast(context, "连接服务器失败");
            }


        });


        RequestManager.getInstance(context).addRequest(request, this);

    }


    public void onSignupSuccess() {
        btn_signup.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Utils.toast(this, getString(R.string.error_signup_failed));
        btn_signup.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = input_name.getText().toString().trim();
        String password = input_password.getText().toString();

        if (username.isEmpty() || !StringUtils.checkUsername(username)) {
            input_name.setError(getString(R.string.error_invalid_username));
            valid = false;
        } else {
            input_name.setError(null);
        }

        if (password.isEmpty() || !StringUtils.checkPassword(password)) {
            input_password.setError(getString(R.string.error_invalid_password));
            valid = false;
        } else {
            input_password.setError(null);
        }

        return valid;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RequestManager.getInstance(context).cancelAll(this);


    }
}