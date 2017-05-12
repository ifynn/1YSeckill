package com.fynn.oyseckill.app.module.account.user;

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
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.constants.Event;

import org.appu.common.AppHelper;
import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;
import org.appu.security.Base64Helper;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Fynn on 2016/6/15.
 */
public class ModifyPasswordActivity extends BaseActivity {

    private Button btnOk;
    private EditText etOldPsw;
    private EditText etNewPsw;
    private TextView tvForgotPassword;

    private ImageView ivClearOldPsw;
    private ImageView ivClearNewPsw;

    private boolean isSubmitting;

    @Override
    public int getContentResId() {
        return R.layout.activity_modify_password;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        btnOk = $(R.id.btn_ok);
        etOldPsw = $(R.id.et_password_old);
        etNewPsw = $(R.id.et_password_new);
        ivClearOldPsw = $(R.id.iv_clear_old_psw);
        ivClearNewPsw = $(R.id.iv_clear_new_psw);
        tvForgotPassword = $(R.id.tv_forgot_password);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        setOnClick(btnOk, ivClearOldPsw, ivClearNewPsw, tvForgotPassword);

        etOldPsw.addTextChangedListener(new InputWatcher(etOldPsw));
        etNewPsw.addTextChangedListener(new InputWatcher(etNewPsw));

        etOldPsw.setOnFocusChangeListener(new InputFcListener());
        etNewPsw.setOnFocusChangeListener(new InputFcListener());

        register(Event.EVENT_RESET_PASSWORD);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                if (isSubmitting) {
                    showShortToast("请稍候...");
                    return;
                }

                if (isValidInput()) {
                    updatePassword();
                }
                break;

            case R.id.iv_clear_old_psw:
                etOldPsw.setText("");
                break;

            case R.id.iv_clear_new_psw:
                etNewPsw.setText("");
                break;

            case R.id.tv_forgot_password:
                startActivity(ResetPasswordActivity.class);
                break;

            default:
                break;
        }
    }

    private void updatePassword() {
        String op = etOldPsw.getText().toString();
        String np = etNewPsw.getText().toString();

        isSubmitting = true;
        showProgress();

        BmobUser.updateCurrentUserPassword(
                this, Base64Helper.encode(op), Base64Helper.encode(np), new UpdateListener() {

                    @Override
                    public void onSuccess() {
                        isSubmitting = false;
                        hideProgress();
                        showShortToast("密码修改成功");
                        AppHelper.sendLocalEvent(Event.EVENT_MODIFY_PASSWORD);
                        UserHelper.logout();
                        finish();
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        isSubmitting = false;
                        hideProgress();

                        String message = "密码修改失败";
                        switch (code) {
                            case 9016:
                                message = "网络无连接";
                                break;

                            case 9010:
                                message = "网络超时";
                                break;

                            case 210:
                                message = "旧密码错误";
                                break;

                            default:
                                break;
                        }
                        showShortToast(message);

                        LogU.e("密码修改失败", "code:" + code, "msg:" + msg);
                    }
                });
    }

    private boolean isValidInput() {
        String password = etNewPsw.getText().toString();

        if (password.length() < 6 || password.length() > 20) {
            showShortToast("密码必须为6-20位字符");
            return false;
        }

        if (TextUtils.isContainBlank(password)) {
            showShortToast("密码不能包含空格");
            return false;
        }

        if (TextUtils.isDigitsOnly(password)) {
            showShortToast("密码不能为纯数字");
            return false;
        }

        if (TextUtils.isLetterOnly(password)) {
            showShortToast("密码不能为纯英文字母");
            return false;
        }

        if (TextUtils.isSpecialCharsOnly(password)) {
            showShortToast("密码不能仅为特殊字符");
            return false;
        }

        if (!TextUtils.isContainNumeric(password)) {
            showShortToast("密码必须包含数字");
            return false;
        }

        if (TextUtils.isContainChinese(password)) {
            showShortToast("密码不能包含中文字符");
            return false;
        }

        return true;
    }

    @Override
    public void onEvent(String action, Bundle data) {
        if (action.equals(Event.EVENT_RESET_PASSWORD)) {
            finish();
        }
    }

    private void updateBtnState() {
        String op = etOldPsw.getText().toString();
        String np = etNewPsw.getText().toString();

        if (op.length() <= 0 || np.length() <= 0) {
            btnOk.setEnabled(false);
        } else {
            btnOk.setEnabled(true);
        }
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
            if (mEditText.getId() == R.id.et_password_old) {
                if (mEditText.isFocused()) {
                    if (s.length() <= 0) {
                        ivClearOldPsw.setVisibility(View.GONE);
                    } else {
                        ivClearOldPsw.setVisibility(View.VISIBLE);
                    }
                } else {
                    ivClearOldPsw.setVisibility(View.GONE);
                }

            } else if (mEditText.getId() == R.id.et_password_new) {
                if (mEditText.isFocused()) {
                    if (s.length() <= 0) {
                        ivClearNewPsw.setVisibility(View.GONE);
                    } else {
                        ivClearNewPsw.setVisibility(View.VISIBLE);
                    }
                } else {
                    ivClearNewPsw.setVisibility(View.GONE);
                }
            }

            updateBtnState();
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

            if (v.getId() == R.id.et_password_old) {
                if (hasFocus && ((EditText) v).getText().toString().length() > 0) {
                    ivClearOldPsw.setVisibility(View.VISIBLE);
                } else {
                    ivClearOldPsw.setVisibility(View.GONE);
                }
            }

            if (v.getId() == R.id.et_password_new) {
                if (hasFocus && ((EditText) v).getText().toString().length() > 0) {
                    ivClearNewPsw.setVisibility(View.VISIBLE);
                } else {
                    ivClearNewPsw.setVisibility(View.GONE);
                }
            }
        }
    }
}
