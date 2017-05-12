package com.fynn.oyseckill.app.module.account.user;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.model.entity.Address;
import com.fynn.oyseckill.model.entity.City;
import com.fynn.oyseckill.model.entity.County;
import com.fynn.oyseckill.model.entity.Province;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.view.SheetUtils;
import com.fynn.oyseckill.widget.dialog.AddressSheet;
import com.fynn.oyseckill.widget.dialog.IPrompter;
import com.fynn.oyseckill.widget.dialog.Sheet;
import com.fynn.oyseckill.widget.dialog.SheetItem;

import org.appu.common.ParamMap;
import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobACL;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Fynn on 2016/6/24.
 */
public class EditAddressActivity extends BaseActivity {

    public static final int TYPE_ADD = 0x01;
    public static final int TYPE_EDIT = 0x02;

    private int type = TYPE_ADD;
    private String objectId;
    private String receiver;
    private String mobile;
    private String area;
    private String detail;

    private boolean isRequesting;

    private LinearLayout llArea;
    private EditText etReceiver;
    private EditText etMobile;
    private TextView tvArea;
    private EditText etDetail;
    private Button btnSave;

    private County county;

    @Override
    public int getContentResId() {
        return R.layout.activity_edit_address;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void handleIntent() {
        ParamMap pm = getParams();
        if (pm != null) {
            type = (int) pm.get("type");
            if (type == TYPE_EDIT) {
                objectId = (String) pm.get("objectId");
                receiver = (String) pm.get("receiver");
                mobile = (String) pm.get("mobile");
                area = (String) pm.get("area");
                detail = (String) pm.get("detail");
                county = new County();
                county.setObjectId((String) pm.get("countyObjId"));
            }
        }
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        llArea = $(R.id.ll_area);
        etReceiver = $(R.id.et_receiver);
        etMobile = $(R.id.et_mobile);
        tvArea = $(R.id.tv_area);
        etDetail = $(R.id.et_detail);
        btnSave = $(R.id.btn_save);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        if (type == TYPE_EDIT) {
            titlebar.setTitle("编辑地址");
            titlebar.setRightAction("删除", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(objectId)) {
                        SheetUtils.showCautionSheet(me, "是否删除该地址？", "删除",
                                new IPrompter.OnClickListener() {
                                    @Override
                                    public void onClick(Dialog dialog, int which) {
                                        dialog.dismiss();
                                        delete(objectId);
                                    }
                                });
                    }
                }
            });
        } else {
            titlebar.setTitle("添加地址");
            btnSave.setText("添加");
        }

        setOnClick(llArea, btnSave);

        if (!TextUtils.isEmpty(receiver)) {
            etReceiver.setText(receiver);
        }

        if (!TextUtils.isEmpty(mobile)) {
            etMobile.setText(mobile);
        }

        if (!TextUtils.isEmpty(area)) {
            tvArea.setText(area);
        }

        if (!TextUtils.isEmpty(detail)) {
            etDetail.setText(detail);
        }

        etReceiver.requestFocus();

        tvArea.addTextChangedListener(new AddressWatcher());
        etReceiver.addTextChangedListener(new AddressWatcher());
        etMobile.addTextChangedListener(new AddressWatcher());
        etDetail.addTextChangedListener(new AddressWatcher());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_area:
                queryProvince();
                break;

            case R.id.btn_save:
                if (isRequesting) {
                    return;
                }
                if (isValidInput()) {
                    if (type == TYPE_EDIT) {
                        editAddress();

                    } else {
                        queryLimit();
                    }
                }
                break;
        }
    }

    private void queryLimit() {
        if (isRequesting) {
            return;
        }
        isRequesting = true;
        showProgress();
        BmobQuery<Address> query = new BmobQuery<Address>();
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.addWhereNotEqualTo("isDeleted", true);
        query.count(me, Address.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                if (i >= 20) {
                    showShortToast("最多只能添加20个收货地址");
                    isRequesting = false;
                    hideProgress();
                } else {
                    addAddress();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                isRequesting = false;
                hideProgress();
                switch (i) {
                    case 9010:
                    case 9016:
                        showShortToast("请检查您的网络");
                        break;
                }
            }
        });
    }

    private void addAddress() {
        String receiver = etReceiver.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String detail = etDetail.getText().toString().trim();

        BmobACL acl = new BmobACL();
        acl.setReadAccess(UserHelper.getUser(), true);
        acl.setWriteAccess(UserHelper.getUser(), true);
        acl.setPublicReadAccess(true);
        //        acl.setPublicWriteAccess(false);

        Address a = new Address();
        a.setReceiver(receiver);
        a.setMobile(mobile);
        a.setDetail(detail);
        a.setAddress(county);
        a.setDefault(false);
        a.setDeleted(false);
        a.setUser(UserHelper.getUser());
//        a.setACL(acl);
        a.save(me, new SaveListener() {
            @Override
            public void onSuccess() {
                isRequesting = false;
                hideProgress();
                showShortToast("添加成功");
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                isRequesting = false;
                hideProgress();
                switch (i) {
                    case 9010:
                    case 9016:
                        showShortToast("请检查您的网络");
                        break;

                    default:
                        showShortToast("添加失败");
                        break;
                }
            }
        });
    }

    private void editAddress() {
        if (isRequesting) {
            return;
        }
        showProgress();
        isRequesting = true;

        String receiver = etReceiver.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String detail = etDetail.getText().toString().trim();

        Address a = new Address();
        a.setObjectId(objectId);
        a.setReceiver(receiver);
        a.setMobile(mobile);
        a.setDetail(detail);
        a.setAddress(county);
        a.update(me, new UpdateListener() {
            @Override
            public void onSuccess() {
                hideProgress();
                isRequesting = false;
                showShortToast("修改成功");
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                hideProgress();
                isRequesting = false;
                switch (i) {
                    case 9010:
                    case 9016:
                        showShortToast("请检查您的网络");
                        break;

                    default:
                        showShortToast("修改失败");
                        break;
                }

                LogU.e("修改地址失败", "code:" + i, "msg:" + s);
            }
        });
    }

    private void queryProvince() {
        if (isRequesting) {
            return;
        }
        BmobQuery<Province> query = new BmobQuery<Province>();
        query.setLimit(100);
        showProgress();
        isRequesting = true;
        query.findObjects(me, new FindListener<Province>() {
            @Override
            public void onSuccess(final List<Province> list) {
                isRequesting = false;
                hideProgress();
                if (list != null) {
                    final List<SheetItem> items = new ArrayList<SheetItem>();
                    Iterator<Province> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        Province p = iterator.next();
                        String pn = p.getProvinceName();
                        SheetItem item = new SheetItem(pn);
                        items.add(item);
                    }

                    new AddressSheet.Builder(me)
                            .setItems(items)
                            .setMessage("选择省份")
                            .setConfirm("取消", new IPrompter.OnClickListener() {
                                @Override
                                public void onClick(Dialog dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setConfirmTextColor(getResources().getColor(R.color.red_F85757))
                            .setOnItemClickListener(new IPrompter.OnItemStateClickListener() {
                                @Override
                                public void onItemClick(Dialog dialog, ProgressBar progressBar, int position) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    queryCity(list.get(position), dialog);
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onError(int i, String s) {
                isRequesting = false;
                hideProgress();
            }
        });
    }

    private void queryCity(Province province, final Dialog dialog) {
        if (isRequesting) {
            return;
        }
        BmobQuery<City> query = new BmobQuery<City>();
        query.setLimit(100);
        query.addWhereEqualTo("province", new BmobPointer(province));
        isRequesting = true;
        query.findObjects(me, new FindListener<City>() {
            @Override
            public void onSuccess(final List<City> list) {
                isRequesting = false;
                dialog.dismiss();

                if (list != null) {
                    final List<SheetItem> items = new ArrayList<SheetItem>();
                    Iterator<City> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        City c = iterator.next();
                        String cn = c.getCityName();
                        SheetItem item = new SheetItem(cn);
                        items.add(item);
                    }

                    new AddressSheet.Builder(me)
                            .setItems(items)
                            .setMessage("选择城市")
                            .setConfirm("取消", new IPrompter.OnClickListener() {
                                @Override
                                public void onClick(Dialog dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setConfirmTextColor(getResources().getColor(R.color.red_F85757))
                            .setOnItemClickListener(new IPrompter.OnItemStateClickListener() {
                                @Override
                                public void onItemClick(Dialog dialog, ProgressBar progressBar, int position) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    queryCounty(list.get(position), dialog);
                                }
                            })
                            .show();
                }

            }

            @Override
            public void onError(int i, String s) {
                isRequesting = false;
            }
        });
    }

    private void queryCounty(City city, final Dialog dialog) {
        if (isRequesting) {
            return;
        }
        BmobQuery<County> query = new BmobQuery<County>();
        query.setLimit(100);
        query.include("city,city.province");
        query.addWhereEqualTo("city", new BmobPointer(city));
        isRequesting = true;
        query.findObjects(me, new FindListener<County>() {
            @Override
            public void onSuccess(final List<County> list) {
                isRequesting = false;
                dialog.dismiss();
                if (list != null) {
                    final List<SheetItem> items = new ArrayList<SheetItem>();
                    Iterator<County> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        County c = iterator.next();
                        String cn = c.getCountyName();
                        SheetItem item = new SheetItem(cn);
                        items.add(item);
                    }

                    new Sheet.Builder(me)
                            .setItems(items)
                            .setMessage("选择区县")
                            .setConfirm("取消", new IPrompter.OnClickListener() {
                                @Override
                                public void onClick(Dialog dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setConfirmTextColor(getResources().getColor(R.color.red_F85757))
                            .setOnItemClickListener(new IPrompter.OnItemClickListener() {
                                @Override
                                public void onItemClick(Dialog dialog, View view, int position) {
                                    dialog.dismiss();
                                    county = list.get(position);
                                    String countyName = county.getCountyName();
                                    String cityName = county.getCity().getCityName();
                                    String pName = county.getCity().getProvince().getProvinceName();
                                    tvArea.setText(pName + " " + cityName + " " + countyName);
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onError(int i, String s) {
                isRequesting = false;
            }
        });
    }

    private boolean isValidInput() {
        String receiver = etReceiver.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String area = tvArea.getText().toString();
        String detail = etDetail.getText().toString().trim();

        if (!TextUtils.isContainChinese(receiver)) {
            showShortToast("收货人姓名必须包含中文");
            return false;
        }

        if (!TextUtils.isMobile(mobile)) {
            showShortToast("11位手机号格式不正确");
            return false;
        }

        if (TextUtils.isEmpty(area) || TextUtils.isEmpty(detail)) {
            showShortToast("所在地区或详细地址不能为空");
            return false;
        }

        if (detail.length() < 5) {
            showShortToast("详细地址字数太少了~");
            return false;
        }

        if (!TextUtils.isContainChinese(detail)) {
            showShortToast("详细地址必须包含中文");
            return false;
        }

        return true;
    }

    private void delete(String objectId) {
        if (isRequesting) {
            return;
        }

        Address a = new Address();
        a.setObjectId(objectId);
        a.setDeleted(true);
        a.update(me, new UpdateListener() {
            @Override
            public void onSuccess() {
                isRequesting = false;
                showShortToast("删除成功");
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                isRequesting = false;
                showShortToast("删除失败");
                LogU.e("地址删除失败", "code:" + i, "msg:" + s);
            }
        });
    }

    class AddressWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String receiver = etReceiver.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();
            String area = tvArea.getText().toString();
            String detail = etDetail.getText().toString().trim();

            if (!TextUtils.isEmpty(receiver) && !TextUtils.isEmpty(mobile)
                    && !TextUtils.isEmpty(area) && !TextUtils.isEmpty(detail)) {
                btnSave.setEnabled(true);
            } else {
                btnSave.setEnabled(false);
            }
        }
    }
}
