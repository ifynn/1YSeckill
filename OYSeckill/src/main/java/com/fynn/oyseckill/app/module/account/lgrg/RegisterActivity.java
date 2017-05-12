package com.fynn.oyseckill.app.module.account.lgrg;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.model.entity.Agreement;
import com.fynn.oyseckill.model.entity.OysUser;
import com.fynn.oyseckill.util.constants.Event;
import com.fynn.oyseckill.web.WebActivity;
import com.fynn.oyseckill.widget.Switcher;

import org.appu.common.AppHelper;
import org.appu.common.ParamMap;
import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;
import org.appu.model.Result;
import org.appu.security.Base64Helper;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.smssdk.SMSSDK;

import static cn.bmob.v3.BmobQuery.CachePolicy.CACHE_ELSE_NETWORK;

/**
 * Created by fynn on 16/6/5.
 */
public class RegisterActivity extends BaseActivity {

    private EditText etMobile;
    private EditText etSmsCode;
    private EditText etPassword;
    private TextView tvSendSmsCode;
    private CheckBox cbAgreeLicense;
    private TextView tvAgreement;
    private Button btnRegister;

    private ImageView ivClearMobile;
    private ImageView ivClearPassword;
    private Switcher switcher;

    private SmsTimer smsTimer;
    private boolean isSendingSmsCode;
    private boolean isRegistering;

    private boolean isLoading;

    @Override
    public int getContentResId() {
        return R.layout.activity_register;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        etMobile = $(R.id.et_mobile);
        etSmsCode = $(R.id.et_sms_code);
        etPassword = $(R.id.et_password);
        tvSendSmsCode = $(R.id.tv_send_sms_code);
        cbAgreeLicense = $(R.id.cb_agree_license);
        tvAgreement = $(R.id.tv_agreement);
        btnRegister = $(R.id.btn_register);
        ivClearMobile = $(R.id.iv_clear_mobile);
        ivClearPassword = $(R.id.iv_clear_password);
        switcher = $(R.id.switcher_toggle_password);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        setOnClick(tvAgreement, btnRegister, tvSendSmsCode, ivClearMobile, ivClearPassword);

        cbAgreeLicense.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateRegisterBtn();
            }
        });

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
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                isSendingSmsCode = false;
                Result<Object> smsRst = (Result<Object>) msg.obj;
                if (smsRst.isOk()) {
                    showShortToast("发送成功");
                    smsTimer = new SmsTimer(61000, 1000);
                    smsTimer.tick();
                    etSmsCode.requestFocus();

                } else {
                    String message = smsRst.getMessage();
                    if (!TextUtils.isEmpty(message)) {
                        showShortToast(message);
                    } else {
                        showShortToast("验证码发送失败");
                    }
                }
                break;

            case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                Result<HashMap<String, Object>> submitRst = (Result<HashMap<String, Object>>) msg.obj;
                if (submitRst.isOk()) {
                    if (submitRst.hasData()) {
                        registerUser();
                    } else {
                        isRegistering = false;
                        hideProgress();
                    }
                } else {
                    isRegistering = false;
                    hideProgress();
                    String message = submitRst.getMessage();
                    if (!TextUtils.isEmpty(message)) {
                        showShortToast(message);
                    } else {
                        showShortToast("验证码验证失败");
                    }
                }
                break;
        }

    }

    private void registerUser() {
        final String mobile = etMobile.getText().toString();
        final String password = etPassword.getText().toString();

        final String secPsw = Base64Helper.encode(password.toLowerCase());

        BmobUser bu = new BmobUser();
        bu.setUsername(mobile);
        bu.setPassword(secPsw);
        bu.setMobilePhoneNumber(mobile);
        bu.setMobilePhoneNumberVerified(true);
        bu.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
                isRegistering = false;
                hideProgress();
                showShortToast("注册成功");
                Intent intent = new Intent();
                intent.putExtra("mobile", mobile);
                intent.putExtra("secPassword", secPsw);
                setResult(RESULT_OK, intent);
                AppHelper.sendLocalEvent(Event.EVENT_REGISTER);
                finish();
            }

            @Override
            public void onFailure(int code, String msg) {
                isRegistering = false;
                hideProgress();
                String message = "注册失败";
                switch (code) {
                    case 202:
                        message = "该手机号已被其他用户注册";
                        break;

                    case 9010:
                    case 9016:
                        message = "网络不给力啊~";
                        break;

                    default:
                        break;
                }
                showShortToast(message);
                LogU.e("注册失败", "code:" + code, "msg:" + msg);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_agreement:
                if (!isLoading) {
                    queryAgreement();
                }
                break;

            case R.id.btn_register:
                if (isValidInput()) {
                    if (isRegistering) {
                        return;
                    }
                    isRegistering = true;
                    showProgress();
                    String mobile = etMobile.getText().toString();
                    String smsCode = etSmsCode.getText().toString();
                    SMSSDK.submitVerificationCode("86", mobile, smsCode);
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

    private void sendSmsCode() {
        SMSSDK.getVerificationCode("86", etMobile.getEditableText().toString());
    }

    private boolean isValidInput() {    //登陆次数限制要考虑
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

    public void checkPhoneExisted(String phone) {
        BmobQuery<OysUser> query = new BmobQuery<OysUser>();
        query.addWhereEqualTo("mobilePhoneNumber", phone);
        query.findObjects(this, new FindListener<OysUser>() {

            @Override
            public void onError(int code, String msg) {
                isSendingSmsCode = false;
                String tmpMsg = "操作失败";
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
                    isSendingSmsCode = false;
                    showShortToast("该手机号已被其他用户注册");
                } else {
                    showShortToast("请稍候...");
                    sendSmsCode();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterSms();
    }

    private void updateRegisterBtn() {
        String mobile = etMobile.getText().toString();
        String password = etPassword.getText().toString();
        String smsCode = etSmsCode.getText().toString();
        boolean isAgreeLicense = cbAgreeLicense.isChecked();

        if (!TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(smsCode) && isAgreeLicense) {
            btnRegister.setEnabled(true);
        } else {
            btnRegister.setEnabled(false);
        }
    }

    private void queryAgreement() {
        isLoading = true;
        BmobQuery<Agreement> query = new BmobQuery<>();
        query.order("-createdAt");
        query.setLimit(1);
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(30)); //30 days
        query.setCachePolicy(CACHE_ELSE_NETWORK);
        query.findObjects(me, new FindListener<Agreement>() {
            @Override
            public void onSuccess(List<Agreement> list) {
                isLoading = false;

                if (list != null && !list.isEmpty()) {
                    ParamMap<String, Object> params = new ParamMap<String, Object>();
                    params.put("url", list.get(0).getUrl());
                    params.put("title", "用户协议");
                    startActivity(WebActivity.class, params);
                } else {
                    showShortToast("error");
                }
            }

            @Override
            public void onError(int i, String s) {
                isLoading = false;

                switch (i) {
                    case 9010:
                    case 9016:
                        showShortToast("无网络连接");
                        break;
                }
            }
        });
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
            updateRegisterBtn();

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
