package com.fynn.oyseckill.app.module.account.user;

import android.app.Dialog;
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
import com.fynn.oyseckill.app.module.find.OrderShareDetailActivity;
import com.fynn.oyseckill.model.OrderState;
import com.fynn.oyseckill.model.OrderStatusExtra;
import com.fynn.oyseckill.model.ProductType;
import com.fynn.oyseckill.model.entity.Address;
import com.fynn.oyseckill.model.entity.City;
import com.fynn.oyseckill.model.entity.County;
import com.fynn.oyseckill.model.entity.LuckOrder;
import com.fynn.oyseckill.model.entity.OrderStatus;
import com.fynn.oyseckill.model.entity.Province;
import com.fynn.oyseckill.util.ImageUtils;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.constants.Event;
import com.fynn.oyseckill.util.constants.Request;
import com.fynn.oyseckill.util.constants.Result;
import com.fynn.oyseckill.util.view.SheetUtils;
import com.fynn.oyseckill.widget.dialog.IPrompter;

import org.appu.common.AppHelper;
import org.appu.common.ParamMap;
import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;
import org.appu.common.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Fynn on 2016/7/28.
 */
public class OrderStatusActivity extends BaseActivity {

    private ListView lvStatus;
    private TextView tvEmpty;

    private View headView;
    private ImageView ivPic;
    private TextView tvProName;
    private TextView tvIssueNo;
    private TextView tvLuckNo;
    private TextView tvTotalPt;
    private TextView tvMyPt;
    private TextView tvAnnTime;
    private LinearLayout llProductInfo;
    private LinearLayout llDeliveryInfo;
    private TextView tvDeliveryInfo;
    private LinearLayout llDeliveryNo;
    private TextView tvDeliveryNo;
    private LinearLayout llExtraInfo;
    private TextView tvExtraInfo;
    private LinearLayout llAction;
    private TextView tvAction;

    private List<OrderStatusExtra> oStatuses;
    private StatusAdapter adapter;
    private LuckOrder order;

    private String orderId;
    private String picUrl;
    private String productName;
    private String issueNo;
    private String luckNo;
    private String totalPt;
    private String myPt;
    private String annTime;
    private String productObjId;
    private String issueObjId;
    private boolean isVirtual;
    /**
     * 更新状态后，幸运记录界面需刷新！！！
     *
     * @param receiver
     * @param mobile
     * @param detailAdr
     */
    private boolean isConfirmingAdr = false;

    @Override
    public void handleIntent() {
        ParamMap<String, Object> params = getParams();
        if (params != null) {
            orderId = (String) params.get("orderId");
            picUrl = (String) params.get("picUrl");
            productName = (String) params.get("productName");
            issueNo = (String) params.get("issueNo");
            luckNo = (String) params.get("luckNo");
            totalPt = (String) params.get("totalPt");
            myPt = (String) params.get("myPt");
            annTime = (String) params.get("annTime");
            productObjId = (String) params.get("productObjId");
            issueObjId = (String) params.get("issueObjId");
            isVirtual = (boolean) params.get("isVirtual");
        }
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_order_status;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        oStatuses = new ArrayList<>();
        adapter = new StatusAdapter();

        lvStatus = $(R.id.lv_status);
        tvEmpty = $(R.id.tv_empty);
        headView = LayoutInflater.from(me).inflate(R.layout.layout_order_status_top, null);
        ivPic = ViewUtils.findViewById(headView, R.id.iv_pic);
        tvProName = ViewUtils.findViewById(headView, R.id.tv_product_name);
        tvIssueNo = ViewUtils.findViewById(headView, R.id.tv_issue_no);
        tvLuckNo = ViewUtils.findViewById(headView, R.id.tv_luck_no);
        tvTotalPt = ViewUtils.findViewById(headView, R.id.tv_total_pt);
        tvMyPt = ViewUtils.findViewById(headView, R.id.tv_my_pt);
        tvAnnTime = ViewUtils.findViewById(headView, R.id.tv_finish_time);
        llProductInfo = ViewUtils.findViewById(headView, R.id.ll_product_info);
        llDeliveryInfo = ViewUtils.findViewById(headView, R.id.ll_delivery_info);
        tvDeliveryInfo = ViewUtils.findViewById(headView, R.id.tv_delivery_info);
        llDeliveryNo = ViewUtils.findViewById(headView, R.id.ll_delivery_no);
        tvDeliveryNo = ViewUtils.findViewById(headView, R.id.tv_delivery_no);
        llExtraInfo = ViewUtils.findViewById(headView, R.id.ll_extra_info);
        tvExtraInfo = ViewUtils.findViewById(headView, R.id.tv_extra_info);
        llAction = ViewUtils.findViewById(headView, R.id.ll_action);
        tvAction = ViewUtils.findViewById(headView, R.id.tv_action);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        lvStatus.addHeaderView(headView, null, false);
        lvStatus.setAdapter(adapter);
        lvStatus.setEmptyView(tvEmpty);

        ImageUtils.getInstance().display(picUrl, ivPic, R.drawable.pic_default_square_gray);
        tvProName.setText(productName);
        tvIssueNo.setText(issueNo);
        tvLuckNo.setText(luckNo);
        tvTotalPt.setText(totalPt);
        tvMyPt.setText(myPt);
        tvAnnTime.setText(annTime);

        if (!TextUtils.isEmpty(orderId)) {
            queryData();
        }

        updateDeliveryInfo();

        register(Event.EVENT_ORDER_SHARED);
    }

    private void refresh() {
        oStatuses.clear();
        queryData();
    }

    private void queryData() {
        final BmobQuery<OrderStatus> query = new BmobQuery<>();
        query.addWhereEqualTo("order", new BmobPointer(new LuckOrder(orderId)));
        query.order("createdAt");
        query.setLimit(100);
        query.include("order,order.issue,order.issue.product");
        query.findObjects(me, new FindListener<OrderStatus>() {
            @Override
            public void onSuccess(List<OrderStatus> list) {
                if (list != null && !list.isEmpty()) {
                    order = list.get(0).getOrder();
                    int state = order.getState() == null ?
                            OrderState.STATE_GAIN_PRODUCT : order.getState();

                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        OrderStatusExtra ose = new OrderStatusExtra(list.get(i));
                        oStatuses.add(ose);
                    }

                    boolean isVirtual = ProductType.TYPE_VIRTUAL_PRODUCT.equalsIgnoreCase(
                            order.getIssue().getProduct().getType());

                    if (isVirtual) {
                        switch (state) {
                            case OrderState.STATE_RECEIVED:
                            case OrderState.STATE_DELIVERED:
                                llAction.setVisibility(View.VISIBLE);
                                tvAction.setText("晒单评价");
                                tvAction.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        gotoOrderShareEdit();
                                    }
                                });
                                break;

                            case OrderState.STATE_SHARED:
                                llAction.setVisibility(View.VISIBLE);
                                tvAction.setText("查看晒单");
                                tvAction.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ParamMap<String, Object> params = new ParamMap<String, Object>();
                                        params.put("issueObjId", issueObjId);
                                        startActivity(OrderShareDetailActivity.class, params);
                                    }
                                });
                                break;

                            default:
                                llAction.setVisibility(View.GONE);
                                break;
                        }
                    } else {
                        switch (state) {
                            case OrderState.STATE_RECEIVED:
                                llAction.setVisibility(View.VISIBLE);
                                tvAction.setText("晒单评价");
                                tvAction.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        gotoOrderShareEdit();
                                    }
                                });
                                break;

                            case OrderState.STATE_SHARED:
                                llAction.setVisibility(View.VISIBLE);
                                tvAction.setText("查看晒单");
                                tvAction.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ParamMap<String, Object> params = new ParamMap<String, Object>();
                                        params.put("issueObjId", issueObjId);
                                        startActivity(OrderShareDetailActivity.class, params);
                                    }
                                });
                                break;

                            default:
                                llAction.setVisibility(View.GONE);
                                break;
                        }
                    }

                    if (state == OrderState.STATE_WAIT_FOR_CONFIRM_ADDRESS) {
                        queryAddress();
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    adapter.notifyDataSetChanged();
                }

                updateDeliveryInfo();
            }

            @Override
            public void onError(int i, String s) {
                LogU.e("获取商品状态失败", "code:" + i, "msg:" + s);

                showShortToast("获取商品状态失败");
            }
        });
    }

    private void gotoOrderShareEdit() {
        ParamMap<String, Object> params = new ParamMap<String, Object>();
        params.put("picUrl", picUrl);
        params.put("productName", productName);
        params.put("issueNo", order.getIssue().getIssueNumber());
        params.put("isVirtual", isVirtual);
        params.put("issueObjId", issueObjId);
        params.put("productObjId", productObjId);
        params.put("orderObjId", orderId);
        startActivity(OrderShareEditActivity.class, params);
    }

    private void queryAddress() {
        final BmobQuery<Address> query = new BmobQuery<>();
        query.addWhereEqualTo("isDefault", true);
        query.addWhereEqualTo("isDeleted", false);
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.include("address,address.city,address.city.province");
        query.findObjects(me, new FindListener<Address>() {
            @Override
            public void onSuccess(List<Address> list) {
                OrderStatus os = new OrderStatus();
                os.setOrder(oStatuses.get(0).getOrderStatus().getOrder());
                os.setStatus(OrderState.STATE_WAIT_FOR_CONFIRM_ADDRESS);
                os.setTitle("待确认收货地址");
                OrderStatusExtra ose = new OrderStatusExtra(os);

                if (list != null && !list.isEmpty()) {
                    ose.setReceiver(list.get(0).getReceiver());
                    ose.setMobile(list.get(0).getMobile());
                    ose.setAddress(genDetailAddress(list.get(0).getAddress(),
                            list.get(0).getDetail()));
                    ose.setAddressId(list.get(0).getObjectId());
                }

                oStatuses.add(ose);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                LogU.e("获取默认收货地址失败", "code:" + i, "msg:" + s);

                showShortToast("获取商品状态失败");
            }
        });
    }

    private void updateDeliveryInfo() {
        if (order != null) {
            String lc = order.getLogisticsCompany();
            String tn = order.getTrackingNumber();
            List<String> extras = order.getExtras();

            if (TextUtils.isEmpty(lc) && TextUtils.isEmpty(tn) &&
                    (extras == null || extras.isEmpty())) {
                llProductInfo.setVisibility(View.GONE);
            } else {
                llProductInfo.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(lc)) {
                llDeliveryInfo.setVisibility(View.VISIBLE);
                tvDeliveryInfo.setText(lc);
            } else {
                llDeliveryInfo.setVisibility(View.GONE);
            }


            if (!TextUtils.isEmpty(tn)) {
                llDeliveryNo.setVisibility(View.VISIBLE);
                tvDeliveryNo.setText(tn);
            } else {
                llDeliveryNo.setVisibility(View.GONE);
            }


            if (extras != null && !extras.isEmpty()) {
                llExtraInfo.setVisibility(View.VISIBLE);

                int size = extras.size();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < size; i++) {
                    if (i == size - 1) {
                        sb.append(extras.get(i));
                    } else {
                        sb.append(extras.get(i)).append("\n");
                    }
                }
                tvExtraInfo.setText(sb);

            } else {
                llExtraInfo.setVisibility(View.GONE);
            }

        } else {
            llProductInfo.setVisibility(View.GONE);
        }
    }

    private String genDetailAddress(County address, String detail) {
        if (address == null || org.appu.common.utils.TextUtils.isEmpty(detail)) {
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

        if (org.appu.common.utils.TextUtils.isEmpty(provinceName) ||
                org.appu.common.utils.TextUtils.isEmpty(cityName) ||
                org.appu.common.utils.TextUtils.isEmpty(countyName)) {
            return "";
        }

        return String.format("%s %s %s %s", provinceName, cityName, countyName, detail);
    }

    private synchronized void confirmAddress(String receiver, String mobile, String detailAdr) {
        if (isConfirmingAdr) {
            showShortToast("正在确认，莫急");
            return;
        }

        isConfirmingAdr = true;
        final LuckOrder luckOrder = new LuckOrder();
        luckOrder.setObjectId(orderId);
        luckOrder.setReceiver(receiver);
        luckOrder.setMobile(mobile);
        luckOrder.setAddress(detailAdr);
        luckOrder.setState(OrderState.STATE_WAIT_FOR_DELIVER);
        luckOrder.update(me, new UpdateListener() {
            @Override
            public void onSuccess() {
                AppHelper.sendLocalEvent(Event.EVENT_CONFIRM_ADDRESS);
                OrderStatus os = new OrderStatus();
                os.setOrder(luckOrder);
                os.setTitle("已确认收货地址");
                os.setStatus(OrderState.STATE_WAIT_FOR_DELIVER);
                os.save(me, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        isConfirmingAdr = false;
                        showShortToast("确认成功");
                        refresh();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        isConfirmingAdr = false;
                        showShortToast("收货地址确认失败");
                        LogU.w("确认收货地址失败", "code:" + i, "msg:" + s);
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                isConfirmingAdr = false;
                showShortToast("收货地址确认失败");
                LogU.w("确认收货地址失败", "code:" + i, "msg:" + s);
            }
        });
    }

    @Override
    public void onEvent(String action, Bundle data) {
        if (Event.EVENT_ORDER_SHARED.equals(action)) {
            refresh();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Request.REQUEST_CHOOSE_ADDRESS &&
                resultCode == Result.RESULT_SELECTED_ADDRESS) {
            if (data != null) {
                OrderStatusExtra ose = oStatuses.get(oStatuses.size() - 1);
                ose.setAddressId(data.getStringExtra("addressId"));
                ose.setReceiver(data.getStringExtra("receiver"));
                ose.setMobile(data.getStringExtra("mobile"));
                ose.setAddress(data.getStringExtra("address"));

                adapter.notifyDataSetChanged();
            }
        }
    }

    class StatusAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return oStatuses.size();
        }

        @Override
        public Object getItem(int position) {
            return oStatuses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.layout_order_status_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final OrderStatusExtra ose = (OrderStatusExtra) getItem(position);
            OrderStatus os = ose.getOrderStatus();
            LuckOrder order = os.getOrder();

            holder.tvStatus.setText(os.getTitle());
            holder.tvDateTime.setText(os.getCreatedAt());

            int state = os.getStatus() == null ? 0 : os.getStatus();
            switch (state) {
                case OrderState.STATE_GAIN_PRODUCT: //获得商品
                default:
                    holder.llDetail.setVisibility(View.GONE);
                    break;

                case OrderState.STATE_WAIT_FOR_CONFIRM_ADDRESS: //待确认收货地址
                    holder.llDetail.setVisibility(View.VISIBLE);
                    holder.tvDetail.setVisibility(View.GONE);
                    holder.llDetailAddress.setVisibility(View.VISIBLE);
                    holder.llOperaAdr.setVisibility(View.VISIBLE);

                    if (TextUtils.isEmpty(ose.getReceiver()) || TextUtils.isEmpty(ose.getMobile()) ||
                            TextUtils.isEmpty(ose.getAddress())) {
                        holder.tvReceiver.setVisibility(View.GONE);
                        holder.tvMobile.setVisibility(View.GONE);
                        holder.tvAddress.setVisibility(View.GONE);
                        holder.tvConfirmAdr.setVisibility(View.GONE);

                        holder.tvChangeAdr.setText("选择收货地址");

                    } else {
                        holder.tvReceiver.setVisibility(View.VISIBLE);
                        holder.tvMobile.setVisibility(View.VISIBLE);
                        holder.tvAddress.setVisibility(View.VISIBLE);
                        holder.tvConfirmAdr.setVisibility(View.VISIBLE);

                        holder.tvChangeAdr.setText("更换");

                        holder.tvReceiver.setText(ose.getReceiver());
                        holder.tvMobile.setText(ose.getMobile());
                        holder.tvAddress.setText(ose.getAddress());
                    }

                    holder.tvConfirmAdr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SheetUtils.showConfirmSheet(
                                    me,
                                    "是否确认使用该收货地址？",
                                    "确认使用",
                                    "取消",
                                    new IPrompter.OnClickListener() {
                                        @Override
                                        public void onClick(Dialog dialog, int which) {
                                            dialog.dismiss();
                                            switch (which) {
                                                case IPrompter.BUTTON_POSITIVE:
                                                    confirmAddress(
                                                            ose.getReceiver(),
                                                            ose.getMobile(),
                                                            ose.getAddress());
                                                    break;

                                                default:
                                                    break;
                                            }
                                        }
                                    });
                        }
                    });

                    holder.tvChangeAdr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ParamMap<String, Object> params = new ParamMap<String, Object>();
                            params.put("addressId", ose.getAddressId());
                            startActivityForResult(
                                    ChooseAddressActivity.class,
                                    Request.REQUEST_CHOOSE_ADDRESS,
                                    params);
                        }
                    });
                    break;

                case OrderState.STATE_CONFIRM_ADDRESS_LIMIT: //收货地址确认过期
                    holder.llDetail.setVisibility(View.VISIBLE);
                    holder.tvDetail.setVisibility(View.VISIBLE);
                    holder.llDetailAddress.setVisibility(View.GONE);
                    holder.tvDetail.setText("确认地址过期，已视为自动放弃");
                    break;

                case OrderState.STATE_WAIT_FOR_DELIVER: //已确认收货地址，待发货
                    holder.llDetail.setVisibility(View.VISIBLE);
                    holder.tvDetail.setVisibility(View.GONE);
                    holder.llDetailAddress.setVisibility(View.VISIBLE);
                    holder.llOperaAdr.setVisibility(View.GONE);

                    holder.tvReceiver.setText(order.getReceiver());
                    holder.tvMobile.setText(order.getMobile());
                    holder.tvAddress.setText(order.getAddress());
                    break;

                case OrderState.STATE_DELIVERED: //已发货
                    holder.llDetail.setVisibility(View.GONE);
                    break;

                case OrderState.STATE_RECEIVED: //已签收
                    holder.llDetail.setVisibility(View.GONE);
                    break;
            }

            if (getCount() == 1) {
                holder.llTopLine.setVisibility(View.INVISIBLE);
                holder.llBtmLine.setVisibility(View.INVISIBLE);
                holder.llMiddleLine.setVisibility(View.INVISIBLE);

            } else {
                if (position == getCount() - 1) {
                    holder.llTopLine.setVisibility(View.VISIBLE);
                    holder.llMiddleLine.setVisibility(View.INVISIBLE);
                    holder.llBtmLine.setVisibility(View.INVISIBLE);

                } else if (position == 0) {
                    holder.llTopLine.setVisibility(View.INVISIBLE);
                    holder.llMiddleLine.setVisibility(View.VISIBLE);
                    holder.llBtmLine.setVisibility(View.VISIBLE);

                } else {
                    holder.llBtmLine.setVisibility(View.VISIBLE);
                    holder.llMiddleLine.setVisibility(View.VISIBLE);
                    holder.llTopLine.setVisibility(View.VISIBLE);
                }
            }

            if (position == getCount() - 1) {
                holder.tvStatus.setTextColor(getResources().getColor(R.color.blue_027BC5));
                holder.ivCrtNode.setEnabled(true);
            } else {
                holder.tvStatus.setTextColor(getResources().getColor(R.color.black_4F4F4F));
                holder.ivCrtNode.setEnabled(false);
            }

            return convertView;
        }
    }

    class ViewHolder {
        private TextView tvStatus;
        private TextView tvDateTime;
        private LinearLayout llTopLine;
        private LinearLayout llBtmLine;
        private LinearLayout llMiddleLine;
        private ImageView ivCrtNode;
        private TextView tvDetail;
        private LinearLayout llDetail;
        private LinearLayout llDetailAddress;
        private TextView tvReceiver;
        private TextView tvMobile;
        private TextView tvAddress;
        private TextView tvConfirmAdr;
        private TextView tvChangeAdr;
        private LinearLayout llOperaAdr;

        public ViewHolder(View v) {
            tvStatus = ViewUtils.findViewById(v, R.id.tv_status);
            tvDateTime = ViewUtils.findViewById(v, R.id.tv_datetime);
            llTopLine = ViewUtils.findViewById(v, R.id.ll_top_line);
            llBtmLine = ViewUtils.findViewById(v, R.id.ll_bottom_line);
            llMiddleLine = ViewUtils.findViewById(v, R.id.ll_middle_line);
            ivCrtNode = ViewUtils.findViewById(v, R.id.iv_crt_node);
            tvDetail = ViewUtils.findViewById(v, R.id.tv_detail);
            llDetail = ViewUtils.findViewById(v, R.id.ll_detail);
            llDetailAddress = ViewUtils.findViewById(v, R.id.ll_detail_address);
            tvReceiver = ViewUtils.findViewById(v, R.id.tv_receiver);
            tvMobile = ViewUtils.findViewById(v, R.id.tv_mobile);
            tvAddress = ViewUtils.findViewById(v, R.id.tv_address);
            tvConfirmAdr = ViewUtils.findViewById(v, R.id.tv_confirm_adr);
            tvChangeAdr = ViewUtils.findViewById(v, R.id.tv_change_adr);
            llOperaAdr = ViewUtils.findViewById(v, R.id.ll_opera_adr);
        }
    }
}
