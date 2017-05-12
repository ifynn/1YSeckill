package com.fynn.oyseckill.app.module.account.user;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.model.entity.OysUser;
import com.fynn.oyseckill.util.DirectoryUtils;
import com.fynn.oyseckill.util.DisplayImageUtils;
import com.fynn.oyseckill.util.ImageFileUtils;
import com.fynn.oyseckill.util.UploadFileHelper;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.constants.Event;
import com.fynn.oyseckill.widget.dialog.IPrompter;
import com.fynn.oyseckill.widget.dialog.Sheet;
import com.fynn.oyseckill.widget.dialog.SheetItem;

import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;
import org.appu.security.Base64Helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * Created by fynn on 16/6/10.
 */
public class UserInfoActivity extends BaseActivity {

    private static final int REQUEST_CHOOSE_PICTURE = 0x01;
    private static final int REQUEST_CROP_PICTURE = 0x02;
    private static final int REQUEST_OPEN_CAMERA = 0x03;
    private ImageView ivProfile;
    private TextView tvId;
    private TextView tvNick;
    private TextView tvMobile;
    private LinearLayout llProfile;
    private LinearLayout llId;
    private LinearLayout llNick;
    private LinearLayout llMobile;
    private LinearLayout llAddress;
    private LinearLayout llPassword;
    private String tempPath;
    private File cameraPath;
    private File cameraPic;

    @Override
    public int getContentResId() {
        return R.layout.activity_user_info;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        ivProfile = $(R.id.iv_profile);
        tvId = $(R.id.tv_id);
        tvNick = $(R.id.tv_nick);
        tvMobile = $(R.id.tv_mobile);

        llProfile = $(R.id.ll_profile);
        llId = $(R.id.ll_id);
        llNick = $(R.id.ll_nick);
        llMobile = $(R.id.ll_mobile);
        llAddress = $(R.id.ll_address);
        llPassword = $(R.id.ll_password);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        setOnClick(llProfile, llNick, llAddress, llPassword);
        showUserInfo();

        register(Event.EVENT_RESET_PASSWORD, Event.EVENT_MODIFY_PASSWORD, Event.EVENT_UPDATE_PROFILE);

        tempPath = DirectoryUtils.getExternalTempPath() + "/" +
                Base64Helper.encode(UserHelper.getUser().getObjectId()) + "_" +
                UserHelper.getUser().getUserId() + ".png";

        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        cameraPath = new File(dcim.getPath() + "/Camera");
        if (!cameraPath.exists()) {
            cameraPath.mkdirs();
        }

        DisplayImageUtils.displayProfile(ivProfile);
    }

    private void showUserInfo() {
        if (!UserHelper.isLogin()) {
            finish();
            return;
        }

        OysUser user = BmobUser.getCurrentUser(this, OysUser.class);
        Long id = user.getUserId();
        String nick = user.getNickname();
        String mobile = user.getMobilePhoneNumber();

        if (id != null) {
            tvId.setText(id.toString());
        } else {
            tvId.setText("未知");
        }

        if (!TextUtils.isEmpty(nick)) {
            tvNick.setText(nick);
        } else {
            if (id != null) {
                tvNick.setText(String.format("oys%s", id.toString()));
            } else {
                tvNick.setText("未知");
            }
        }

        String maskMobile = maskMobile(mobile);
        if (!TextUtils.isEmpty(maskMobile)) {
            tvMobile.setText(maskMobile);
        } else {
            tvMobile.setText("未知");
        }
    }

    private String maskMobile(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            return "";
        }

        if (mobile.length() < 7) {
            return mobile;
        } else {
            String maskMobile = mobile.substring(0, 3) +
                    " **** " +
                    mobile.substring(mobile.length() - 4);
            return maskMobile;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_nick:
                startActivity(ModifyNicknameActivity.class);
                break;

            case R.id.ll_password:
                startActivity(ModifyPasswordActivity.class);
                break;

            case R.id.ll_address:
                startActivity(AddressActivity.class);
                break;

            case R.id.ll_profile:
                List<SheetItem> items = new ArrayList<SheetItem>();
                items.add(new SheetItem("从相册选择"));
                items.add(new SheetItem("拍照"));
                new Sheet.Builder(this)
                        .setConfirm("取消", new IPrompter.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setItems(items)
                        .setOnItemClickListener(new IPrompter.OnItemClickListener() {
                            @Override
                            public void onItemClick(Dialog dialog, View view, int position) {
                                dialog.dismiss();
                                switch (position) {
                                    case 0:
                                        ImageFileUtils.openImageChooser(me, REQUEST_CHOOSE_PICTURE);
                                        break;

                                    case 1:
                                        cameraPic = new File(cameraPath.getPath(), "oyseckill_" + System.currentTimeMillis() + ".png");
                                        ImageFileUtils.openCamera(me, REQUEST_OPEN_CAMERA, cameraPic);
                                        break;

                                    default:
                                        break;
                                }
                            }
                        })
                        .show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSE_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    Uri output = Uri.fromFile(new File(tempPath));
                    ImageFileUtils.cropImageFile(me, data.getData(), output, 300, REQUEST_CROP_PICTURE);
                }
                break;

            case REQUEST_CROP_PICTURE:
                if (resultCode == RESULT_OK) {
                    if (!TextUtils.isEmpty(tempPath)) {
                        File file = new File(tempPath);
                        if (file.exists()) {
                            UploadFileHelper.uploadProfile(me, tempPath);
                            LogU.d("截图保存路径", tempPath);

                        } else {
                            LogU.d("截图保存失败", tempPath + "不存在");
                        }
                    }
                }
                break;

            case REQUEST_OPEN_CAMERA:
                if (resultCode == RESULT_OK) {
                    if (cameraPic != null) {
                        Uri input = Uri.fromFile(cameraPic);
                        Uri output = Uri.fromFile(new File(tempPath));

                        LogU.e("拍摄图片路径:", input);
                        LogU.e("拍摄图片裁剪保存路径:", output);

                        ImageFileUtils.cropImageFile(me, input, output, 300, REQUEST_CROP_PICTURE);
                    }
                }
                break;

            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onEvent(String action, Bundle data) {
        if (action.equals(Event.EVENT_RESET_PASSWORD) ||
                action.equals(Event.EVENT_MODIFY_PASSWORD)) {
            showShortToast("请使用新密码登录");
            finish();

        } else if (action.equals(Event.EVENT_UPDATE_PROFILE)) {
            DisplayImageUtils.displayProfile(ivProfile);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showUserInfo();
    }
}
