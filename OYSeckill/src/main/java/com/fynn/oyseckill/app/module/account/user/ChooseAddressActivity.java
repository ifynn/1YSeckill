package com.fynn.oyseckill.app.module.account.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.model.entity.Address;
import com.fynn.oyseckill.model.entity.City;
import com.fynn.oyseckill.model.entity.County;
import com.fynn.oyseckill.model.entity.Province;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.constants.Result;

import org.appu.common.ParamMap;
import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;
import org.appu.common.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by fynn on 16/7/30.
 */
public class ChooseAddressActivity extends BaseActivity {

    private TextView tvAddAdr;
    private LinearLayout llEmpty;
    private ListView lvAddress;

    private List<Address> addresses;
    private AddressAdapter aAdapter;

    private String addressId;

    @Override
    public void handleIntent() {
        ParamMap<String, Object> params = getParams();
        if (params != null) {
            addressId = (String) params.get("addressId");
        }
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_address_choose;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        tvAddAdr = $(R.id.tv_add_adr);
        llEmpty = $(R.id.ll_empty);
        lvAddress = $(R.id.lv_address);

        addresses = new ArrayList<Address>();
        aAdapter = new AddressAdapter();
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        setOnClick(tvAddAdr);
        titlebar.setRightActionClickListener(this);

        lvAddress.setEmptyView(llEmpty);
        lvAddress.setAdapter(aAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_titlebar_right_action:
            case R.id.tv_add_adr:
                addAddress();
                break;
        }
    }

    private void addAddress() {
        if (addresses.size() >= 20) {
            showShortToast("最多只能添加20个收货地址");
        } else {
            ParamMap<String, Object> pm = new ParamMap<String, Object>();
            pm.put("type", EditAddressActivity.TYPE_ADD);
            startActivity(EditAddressActivity.class, pm);
        }
    }

    private void queryAddress(boolean showProgress) {
        if (showProgress) {
            showProgress();
        }
        BmobQuery<Address> query = new BmobQuery<Address>();
        query.include("address,address.city,address.city.province");
        query.setLimit(20);
        query.order("-isDefault,-updatedAt");
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.addWhereNotEqualTo("isDeleted", true);
        query.findObjects(this, new FindListener<Address>() {
            @Override
            public void onSuccess(List<Address> list) {
                hideProgress();
                addresses.clear();
                if (list != null && !list.isEmpty()) {
                    addresses.addAll(list);
                }
                aAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                hideProgress();

                switch (i) {
                    case 9010:
                    case 9016:
                        showShortToast("请检查您的网络");
                        break;
                }

                LogU.e("获取收货地址失败：", "code:" + i, "message:" + s);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryAddress(false);
    }

    private CharSequence genDetailAddress(County address, String detail) {
        if (address == null || TextUtils.isEmpty(detail)) {
            return "";
        }

        City city = address.getCity();
        Province province = city.getProvince();

        if (city == null || province == null) {
            return "";
        }

        String provinceName = province.getProvinceName();
        String cityName = city.getCityName();
        String countyName = address.getCountyName();

        if (TextUtils.isEmpty(provinceName) ||
                TextUtils.isEmpty(cityName) ||
                TextUtils.isEmpty(countyName)) {
            return "";
        }

        return String.format("%s %s %s %s", provinceName, cityName, countyName, detail);
    }

    private CharSequence genAreaAddress(County address) {
        City city = address.getCity();
        Province province = city.getProvince();

        if (city == null || province == null) {
            return "";
        }

        String provinceName = province.getProvinceName();
        String cityName = city.getCityName();
        String countyName = address.getCountyName();

        if (TextUtils.isEmpty(provinceName) ||
                TextUtils.isEmpty(cityName) ||
                TextUtils.isEmpty(countyName)) {
            return "";
        }

        return String.format("%s %s %s", provinceName, cityName, countyName);
    }

    private String maskMobile(String mobile) {
        if (TextUtils.isEmpty(mobile)) {
            return "";
        }

        if (mobile.length() < 7) {
            return mobile;
        }

        String left = mobile.substring(0, 3);
        String right = mobile.substring(mobile.length() - 4);

        return String.format("%s **** %s", left, right);
    }

    class AddressAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return addresses.size();
        }

        @Override
        public Object getItem(int position) {
            return addresses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(me).inflate(
                        R.layout.layout_address_choose_item, null);

                holder = new ViewHolder(convertView);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final Address address = addresses.get(position);

            holder.tvReceiver.setText(address.getReceiver());
            holder.tvMobile.setText(maskMobile(address.getMobile()));
            holder.tvAddress.setText(genDetailAddress(address.getAddress(), address.getDetail()));

            if (address.getObjectId().equals(addressId)) {
                holder.ivSelected.setVisibility(View.VISIBLE);
            } else {
                holder.ivSelected.setVisibility(View.GONE);
            }

            holder.tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParamMap<String, Object> pm = new ParamMap<String, Object>();
                    pm.put("type", EditAddressActivity.TYPE_EDIT);
                    pm.put("objectId", address.getObjectId());
                    pm.put("receiver", address.getReceiver());
                    pm.put("mobile", address.getMobile());
                    pm.put("area", genAreaAddress(address.getAddress()));
                    pm.put("detail", address.getDetail());
                    pm.put("countyObjId", address.getAddress().getObjectId());
                    startActivity(EditAddressActivity.class, pm);
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addressId = address.getObjectId();
                    notifyDataSetChanged();

                    Intent data = new Intent();
                    data.putExtra("addressId", addressId);
                    data.putExtra("receiver", address.getReceiver());
                    data.putExtra("mobile", address.getMobile());
                    data.putExtra("address", genDetailAddress(address.getAddress(), address.getDetail()));

                    setResult(Result.RESULT_SELECTED_ADDRESS, data);
                    finish();
                }
            });

            return convertView;
        }

        class ViewHolder {

            private TextView tvReceiver;
            private TextView tvMobile;
            private TextView tvAddress;
            private ImageView ivSelected;
            private TextView tvEdit;

            public ViewHolder(View convertView) {
                tvReceiver = ViewUtils.findViewById(convertView, R.id.tv_receiver);
                tvMobile = ViewUtils.findViewById(convertView, R.id.tv_mobile);
                tvAddress = ViewUtils.findViewById(convertView, R.id.tv_address);
                ivSelected = ViewUtils.findViewById(convertView, R.id.iv_selected);
                tvEdit = ViewUtils.findViewById(convertView, R.id.tv_edit);
            }
        }
    }
}
