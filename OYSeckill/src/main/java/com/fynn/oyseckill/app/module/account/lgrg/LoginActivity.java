package com.fynn.oyseckill.app.module.account.lgrg;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.app.module.account.user.ResetPasswordActivity;
import com.fynn.oyseckill.db.UserDb;
import com.fynn.oyseckill.model.entity.OysUser;
import com.fynn.oyseckill.model.entity.UserAccess;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.constants.Event;

import org.appu.common.AppHelper;
import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;
import org.appu.security.Base64Helper;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by fynn on 16/6/5.
 */
public class LoginActivity extends BaseActivity {

    private Button btnLogin;
    private EditText etUsername;
    private EditText etPassword;
    private TextView tvForgotPassword;

    private ImageView ivClearMobile;
    private ImageView ivClearPassword;

    private boolean isLogin;

    private String target;

    @Override
    public int getContentResId() {
        return R.layout.activity_login;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void handleIntent() {
        target = (String) getParams().get(AppHelper.ACTIVITY_TARGET);
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        btnLogin = $(R.id.btn_login);
        etUsername = $(R.id.et_username);
        etPassword = $(R.id.et_user_password);
        ivClearMobile = $(R.id.iv_clear_mobile);
        ivClearPassword = $(R.id.iv_clear_password);
        tvForgotPassword = $(R.id.tv_forgot_password);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        setOnClick(btnLogin, ivClearMobile, ivClearPassword, tvForgotPassword);

        titlebar.setRightActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(me, RegisterActivity.class);
                startActivityForResult(intent, 0x10);
            }
        });

        etUsername.addTextChangedListener(new InputWatcher(etUsername));
        etPassword.addTextChangedListener(new InputWatcher(etPassword));

        etUsername.setOnFocusChangeListener(new InputFcListener());
        etPassword.setOnFocusChangeListener(new InputFcListener());

        etUsername.setText(UserDb.getLastSuccessLoginUsername());
        etUsername.setSelection(etUsername.getText().toString().length());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (isLogin) {
                    showShortToast("正在登录...");
                    return;
                }

                String mobile = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim().toLowerCase();

                if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(password)) {
                    login(mobile, password);

                } else {
                    showShortToast("用户名或密码不能为空");
                }
                break;

            case R.id.iv_clear_mobile:
                etUsername.setText("");
                break;

            case R.id.iv_clear_password:
                etPassword.setText("");
                break;

            case R.id.tv_forgot_password:
                startActivity(ResetPasswordActivity.class);
                break;

            default:
                break;
        }
    }

    private void login(final String username, final String password) {
        showProgress();
        isLogin = true;
        BmobUser.loginByAccount(this, username, Base64Helper.encode(password), new LogInListener<OysUser>() {
            @Override
            public void done(OysUser user, BmobException e) {
                if (user != null) {
                    checkLocked(user, username, password);

                } else {
                    isLogin = false;
                    hideProgress();
                    String message = "登录失败";
                    if (e != null) {
                        switch (e.getErrorCode()) {
                            case 9010:
                            case 9016:
                                message = "请检查您的网络";
                                break;

                            case 101:
                                message = "用户名或密码错误";
                                break;
                        }
                    }
                    showLongToast(message);
                    LogU.e("登录失败", e);
                }
            }
        });
    }

    private void checkLocked(OysUser user, final String username, final String password) {
        BmobQuery<UserAccess> query = new BmobQuery<>();
        query.addWhereEqualTo("user", new BmobPointer(user));
        query.addWhereEqualTo("isLocked", true);
        query.findObjects(me, new FindListener<UserAccess>() {
            @Override
            public void onSuccess(List<UserAccess> list) {
                isLogin = false;
                hideProgress();
                if (list != null && !list.isEmpty()) {
                    UserHelper.logout();
                    showShortToast("登录受限");

                } else {
                    showLongToast("登录成功");
                    AppHelper.sendLocalEvent(Event.EVENT_LOGIN);
                    UserDb.putUn(username);
                    UserDb.putPP(password);
                    UserDb.putLastSuccessLoginUsername(username);
                    dispatchIntent();
                }
            }

            @Override
            public void onError(int i, String s) {
                isLogin = false;
                hideProgress();
                String message = "登录失败";
                switch (i) {
                    case 9010:
                    case 9016:
                        message = "请检查您的网络";
                        break;

                    case 101:
                        message = "用户名或密码错误";
                        break;
                }
                showLongToast(message);
                LogU.e("登录检查锁定失败", i, s);
            }
        });
    }

    private void dispatchIntent() {
        if (target != null) {
            try {
                Class clazz = Class.forName(target);
                startActivity(clazz, getParams());
            } catch (Exception e) {
                LogU.e(e);
            }
            target = null;
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0x10:
                if (resultCode == RESULT_OK && data != null) {
                    String mobile = data.getStringExtra("mobile");
                    String secPassword = data.getStringExtra("secPassword");
                    if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(secPassword)) {
                        etUsername.setText(mobile);
                        etPassword.setText(Base64Helper.decode(secPassword));
                        etUsername.setSelection(etUsername.getText().toString().length());
                        etPassword.setSelection(etPassword.getText().toString().length());
                        login(mobile, Base64Helper.decode(secPassword));
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class InputWatcher implements TextWatcher {

        private EditText mEditText;

        public InputWatcher(EditText mEditText) {
            this.mEditText = mEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mEditText.getId() == R.id.et_username) {
                if (mEditText.isFocused()) {
                    if (s.length() <= 0) {
                        ivClearMobile.setVisibility(View.GONE);
                    } else {
                        ivClearMobile.setVisibility(View.VISIBLE);
                    }
                } else {
                    ivClearMobile.setVisibility(View.GONE);
                }

            } else if (mEditText.getId() == R.id.et_user_password) {
                if (mEditText.isFocused()) {
                    if (s.length() <= 0) {
                        ivClearPassword.setVisibility(View.GONE);
                    } else {
                        ivClearPassword.setVisibility(View.VISIBLE);
                    }
                } else {
                    ivClearPassword.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    class InputFcListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!(v instanceof EditText)) {
                return;
            }

            if (v.getId() == R.id.et_username) {
                if (hasFocus && ((EditText) v).getText().toString().length() > 0) {
                    ivClearMobile.setVisibility(View.VISIBLE);
                } else {
                    ivClearMobile.setVisibility(View.GONE);
                }
            }

            if (v.getId() == R.id.et_user_password) {
                if (hasFocus && ((EditText) v).getText().toString().length() > 0) {
                    ivClearPassword.setVisibility(View.VISIBLE);
                } else {
                    ivClearPassword.setVisibility(View.GONE);
                }
            }
        }
    }
}
