package com.fynn.oyseckill.app.module.account.user;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.model.entity.OysUser;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.constants.Bmob;
import com.fynn.oyseckill.util.constants.Event;
import com.fynn.oyseckill.widget.Switcher;

import org.appu.common.AppHelper;
import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;
import org.appu.security.Base64Helper;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;

/**
 * Created by Fynn on 2016/6/14.
 */
public class ResetPasswordActivity extends BaseActivity {

    private EditText etMobile;
    private EditText etSmsCode;
    private EditText etPassword;
    private TextView tvSendSmsCode;
    private Button btnSubmit;

    private ImageView ivClearMobile;
    private ImageView ivClearPassword;
    private Switcher switcher;

    private SmsTimer smsTimer;
    private boolean isSendingSmsCode;
    private boolean isSubmitting;

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_reset_password;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        etMobile = $(R.id.et_mobile);
        etSmsCode = $(R.id.et_sms_code);
        etPassword = $(R.id.et_password);
        tvSendSmsCode = $(R.id.tv_send_sms_code);
        btnSubmit = $(R.id.btn_submit);
        ivClearMobile = $(R.id.iv_clear_mobile);
        ivClearPassword = $(R.id.iv_clear_password);
        switcher = $(R.id.switcher_toggle_password);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        setOnClick(btnSubmit, tvSendSmsCode, ivClearMobile, ivClearPassword);

        etMobile.addTextChangedListener(new InputWatcher(etMobile));
        etPassword.addTextChangedListener(new InputWatcher(etPassword));
        etSmsCode.addTextChangedListener(new InputWatcher(etSmsCode));

        etMobile.setOnFocusChangeListener(new InputFcListener());
        etPassword.setOnFocusChangeListener(new InputFcListener());

        switcher.setOnCheckedChangeListener(new Switcher.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switcher imageView, boolean isChecked) {
                if (isChecked) {
                    //显示密码
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    //隐藏密码
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                etPassword.setSelection(etPassword.length());
            }
        });

        switcher.setChecked(true);

        registerSms();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (isValidInput()) {
                    if (isSubmitting) {
                        return;
                    }

                    resetPassword();
                }
                break;

            case R.id.tv_send_sms_code:
                if (isSendingSmsCode) {
                    showShortToast("正在发送，莫急...");
                    return;
                }
                String phone = etMobile.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    if (TextUtils.isMobile(phone)) {
                        isSendingSmsCode = true;
                        checkPhoneExisted(phone);
                    } else {
                        showShortToast("手机号码格式不正确");
                    }

                } else {
                    showShortToast("手机号码不能为空");
                }
                break;

            case R.id.iv_clear_mobile:
                etMobile.setText("");
                break;

            case R.id.iv_clear_password:
                etPassword.setText("");
                break;

            default:
                break;
        }
    }

    private void resetPassword() {
        isSubmitting = true;
        showProgress();

        String password = etPassword.getText().toString();
        String smsCode = etSmsCode.getText().toString();

        BmobUser.resetPasswordBySMSCode(this, smsCode, Base64Helper.encode(password),
                new ResetPasswordByCodeListener() {

                    @Override
                    public void done(BmobException ex) {
                        isSubmitting = false;
                        hideProgress();

                        if (ex == null) {
                            showShortToast("密码重置成功");
                            UserHelper.logout();
                            AppHelper.sendLocalEvent(Event.EVENT_RESET_PASSWORD);
                            finish();

                        } else {
                            String message = "密码重置失败";
                            switch (ex.getErrorCode()) {
                                case 9016:
                                    message = "网络无连接";
                                    break;

                                case 9010:
                                    message = "网络超时";
                                    break;

                                case 207:
                                    message = "验证码错误";
                                    break;

                                default:
                                    break;
                            }
                            showShortToast(message);

                            LogU.e("重置密码 - 提交失败", ex);
                        }
                    }
                });
    }

    private boolean isValidInput() {
        String mobile = etMobile.getText().toString();
        String password = etPassword.getText().toString();
        String smsCode = etSmsCode.getText().toString();

        if (!TextUtils.isMobile(mobile)) {
            showShortToast("手机号码格式不正确");
            return false;
        }

        if (TextUtils.isEmpty(smsCode)) {
            showShortToast("验证码不能为空");
            return false;
        }

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

    public void checkPhoneExisted(final String phone) {
        BmobQuery<OysUser> query = new BmobQuery<OysUser>();
        query.addWhereEqualTo("mobilePhoneNumber", phone);
        query.findObjects(this, new FindListener<OysUser>() {

            @Override
            public void onError(int code, String msg) {
                isSendingSmsCode = false;
                String tmpMsg = "验证码发送失败";
                switch (code) {
                    case 9010:
                        tmpMsg = "网络超时";
                        break;

                    case 9016:
                        tmpMsg = "请检查您的网络";
                        break;

                    default:
                        break;
                }
                showShortToast(tmpMsg);
            }

            @Override
            public void onSuccess(List<OysUser> users) {
                if (users != null && !users.isEmpty()) {
                    showShortToast("请稍候...");
                    sendSmsCode(phone);
                } else {
                    isSendingSmsCode = false;
                    showShortToast("账号不存在");
                }
            }
        });
    }

    private void sendSmsCode(String mobile) {
        BmobSMS.requestSMSCode(this, mobile, Bmob.SMS_TEMPLATE_RESET_PASSWORD, new RequestSMSCodeListener() {

            @Override
            public void done(Integer smsId, BmobException ex) {
                isSendingSmsCode = false;
                if (ex == null) {   //验证码发送成功
                    showShortToast("发送成功");
                    smsTimer = new SmsTimer(61000, 1000);
                    smsTimer.tick();
                    etSmsCode.requestFocus();

                } else {
                    String msg = "发送失败，请重试";
                    switch (ex.getErrorCode()) {
                        case 10010:
                            msg = "发送次数过多";
                            break;

                        case 9010:
                            msg = "网络超时";
                            break;

                        case 9016:
                            msg = "网络无连接";
                            break;

                        default:
                            break;
                    }
                    showShortToast(msg);

                    LogU.e("重置密码 - 验证码发送失败", ex);
                }
            }
        });
    }

    private void updateSubmitBtn() {
        String mobile = etMobile.getText().toString();
        String password = etPassword.getText().toString();
        String smsCode = etSmsCode.getText().toString();

        if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(smsCode)) {
            btnSubmit.setEnabled(true);
        } else {
            btnSubmit.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterSms();
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
            updateSubmitBtn();

            if (mEditText.getId() == R.id.et_mobile) {
                if (mEditText.isFocused()) {
                    if (s.length() <= 0) {
                        ivClearMobile.setVisibility(View.GONE);
                    } else {
                        ivClearMobile.setVisibility(View.VISIBLE);
                    }
                } else {
                    ivClearMobile.setVisibility(View.GONE);
                }

            } else if (mEditText.getId() == R.id.et_password) {
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
                ivClearMobile.setVisibility(View.GONE);
                ivClearPassword.setVisibility(View.GONE);
                return;
            }

            if (v.getId() == R.id.et_mobile) {
                if (hasFocus && ((EditText) v).getText().toString().length() > 0) {
                    ivClearMobile.setVisibility(View.VISIBLE);
                } else {
                    ivClearMobile.setVisibility(View.GONE);
                }

            } else if (v.getId() == R.id.et_password) {
                if (hasFocus && ((EditText) v).getText().toString().length() > 0) {
                    ivClearPassword.setVisibility(View.VISIBLE);
                } else {
                    ivClearPassword.setVisibility(View.GONE);
                }

            } else {
                ivClearMobile.setVisibility(View.GONE);
                ivClearPassword.setVisibility(View.GONE);
            }
        }
    }

    class SmsTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public SmsTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            tvSendSmsCode.setEnabled(false);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            String text = "重新发送(" + (millisUntilFinished / 1000) + ")";
            tvSendSmsCode.setText(text);
        }

        @Override
        public void onFinish() {
            tvSendSmsCode.setText("重新发送");
            tvSendSmsCode.setEnabled(true);
        }

        public void tick() {
            start();
        }

        public void stop() {
            this.cancel();
            onFinish();
        }
    }
}
