package com.fynn.oyseckill.app.module.account.user;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.model.entity.OysUser;
import com.fynn.oyseckill.util.UserHelper;

import org.appu.common.utils.TextUtils;

import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by fynn on 16/6/12.
 */
public class ModifyNicknameActivity extends BaseActivity {

    private EditText etModifyNickname;
    private ImageView ivClearText;

    @Override
    public int getContentResId() {
        return R.layout.activity_modify_nickname;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        etModifyNickname = $(R.id.et_modify_nickname);
        ivClearText = $(R.id.iv_text_clear);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        titlebar.setRightActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid()) {
                    saveNickname(etModifyNickname.getText().toString());
                }
            }
        });

        setOnClick(ivClearText);

        etModifyNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ivClearText.setVisibility(View.VISIBLE);
                } else {
                    ivClearText.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etModifyNickname.setText(UserHelper.getNickname());
        etModifyNickname.setSelection(etModifyNickname.getText().toString().length());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_text_clear:
                etModifyNickname.setText("");
                break;
        }
    }

    private void saveNickname(String nick) {
        if (!UserHelper.isLogin()) {
            return;
        }
        showProgress();
        OysUser user = new OysUser();
        user.setNickname(nick);
        user.update(this, UserHelper.getUser().getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                showShortToast("昵称保存成功");
                hideProgress();
                finish();
            }

            @Override
            public void onFailure(int code, String s) {
                String tmpMsg = "更新失败";
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
                hideProgress();
            }
        });
    }

    private boolean isInputValid() {
        String nick = etModifyNickname.getText().toString();

        if (TextUtils.isEmpty(nick.trim())) {
            showShortToast("昵称不能为空");
            return false;
        }

        if (nick.length() < 4 || nick.length() > 20) {
            showShortToast("昵称长度为4~16个字符");
            return false;
        }

        if (!TextUtils.isChineseLetterNumeric(nick)) {
            showShortToast("昵称只能为中文、英文和数字");
            return false;
        }

        if (nick.equals(UserHelper.getNickname())) {
            showShortToast("新昵称与旧昵称不能相同");
            return false;
        }

        return true;
    }
}
