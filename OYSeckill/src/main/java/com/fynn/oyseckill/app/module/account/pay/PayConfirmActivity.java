package com.fynn.oyseckill.app.module.account.pay;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.app.module.account.config.AssistanceDetailActivity;
import com.fynn.oyseckill.app.module.home.detail.SeckillState;
import com.fynn.oyseckill.app.module.home.util.Manager;
import com.fynn.oyseckill.app.module.home.util.SeckillNoUtils;
import com.fynn.oyseckill.app.module.home.util.TextStyleUtils;
import com.fynn.oyseckill.model.entity.Asset;
import com.fynn.oyseckill.model.entity.Expense;
import com.fynn.oyseckill.model.entity.Issue;
import com.fynn.oyseckill.model.entity.OysUser;
import com.fynn.oyseckill.model.entity.Product;
import com.fynn.oyseckill.model.entity.Seckill;
import com.fynn.oyseckill.model.entity.UserPersonTimes;
import com.fynn.oyseckill.util.FileUtils;
import com.fynn.oyseckill.util.ImageUtils;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.pay.Pay;
import com.fynn.oyseckill.util.pay.PayListener;
import com.fynn.oyseckill.util.pay.PayMethod;
import com.tencent.stat.StatService;

import org.appu.common.ParamMap;
import org.appu.common.utils.DateTimeUtils;
import org.appu.common.utils.DeviceUtils;
import org.appu.common.utils.LogU;
import org.appu.common.utils.NetUtils;
import org.appu.common.utils.TextUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import c.b.BP;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.GetServerTimeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Fynn on 2016/8/9.
 */
public class PayConfirmActivity extends BaseActivity {

    private double amount;
    private String picUrl;
    private String productName;
    private String proObjId;

    private double secCoin = 0d;

    private TextView tvProductName;
    private ImageView ivPic;
    private TextView tvAmount;
    private TextView tvPay;
    private RadioButton rbRestCoin;
    private RadioButton rbWechat;
    private TextView tvPayAssistant;

    private String assetObjId;
    private boolean isLoading;
    private Pay.Build payBuild;
    private boolean coinPay = false;
    private double realAmount;
    private Product product;
    private String orderCode;
    private boolean firstUpdateRestCoin = true;

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public int getContentResId() {
        return R.layout.activity_pay_confirm;
    }

    @Override
    public void handleIntent() {
        ParamMap<String, Object> params = getParams();
        if (params != null) {
            Object a = params.get("amount");
            amount = Double.valueOf(String.valueOf(a));

            picUrl = String.valueOf(params.get("picUrl"));
            productName = String.valueOf(params.get("productName"));
            proObjId = String.valueOf(params.get("proObjId"));
        }
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        tvProductName = $(R.id.tv_product_name);
        tvAmount = $(R.id.tv_amount);
        ivPic = $(R.id.iv_pic);
        tvPay = $(R.id.tv_pay);
        rbRestCoin = $(R.id.rb_rest_coin);
        rbWechat = $(R.id.rb_wechat);
        tvPayAssistant = $(R.id.tv_pay_rqa);

        payBuild = new Pay.Build(this);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        tvProductName.setText(productName);

        if (!TextUtils.isEmpty(picUrl)) {
            ImageUtils.getInstance().display(picUrl, ivPic);
        }

        tvAmount.setText(new DecimalFormat("0.0#").format(amount) + "元");

        queryOysCoin();
        setOnClick(tvPay, tvPayAssistant);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pay:
                if (!NetUtils.isNetworkAvailable()) {
                    showShortToast("网络不给力~");
                    return;
                }
                if (isLoading) {
                    showShortToast("正在支付...");
                    return;
                }

                showProgress("请耐心等待");
                isLoading = true;
                if (rbRestCoin.isChecked()) {
                    coinPay = true;
                    payWithRestCoin();
                } else {
                    payWithWechat();
                }
                break;

            case R.id.tv_pay_rqa:
                ParamMap<String, Object> params = new ParamMap<>();
                params.put("typeId", 1); //1表示支付问题
                params.put("typeName", "支付帮助");
                startActivity(AssistanceDetailActivity.class, params);
                break;
        }
    }

    private void goPayResult(boolean success) {
        ParamMap<String, Object> params = new ParamMap<String, Object>();
        params.put("success", success);
        params.put("amount", amount);
        params.put("realAmount", realAmount);
        params.put("payMethod", coinPay ?
                PayMethod.REST_COIN.getValue() : PayMethod.WECHAT.getValue());
        startActivity(PayResultActivity.class, params);
    }

    /**
     * step1，使用秒币支付，首先查询秒币是否充足
     */
    private void payWithRestCoin() {
        BmobQuery<Asset> query = new BmobQuery<Asset>();
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.findObjects(me, new FindListener<Asset>() {
            @Override
            public void onSuccess(List<Asset> list) {
                if (list != null && !list.isEmpty()) {
                    Double coin = list.get(0).getOysCoin();
                    assetObjId = list.get(0).getObjectId();
                    secCoin = coin;
                } else {
                    secCoin = 0d;
                }

                if (secCoin < amount) {
                    showShortToast("秒币不足");
                    updateSecCoin();
                    hideProgress();
                    isLoading = false;

                } else {
                    //秒币充足
                    queryIssue();
                }
            }

            @Override
            public void onError(int i, String s) {
                hideProgress();
                showShortToast("支付失败");
                isLoading = false;
            }
        });
    }

    /**
     * step1，使用微信支付
     */
    private void payWithWechat() {
        double a = amount;
        if (Manager.contains(UserHelper.getObjectId())) {
            a = 0.01d;
        }
        payBuild.setAmount(a)
                .setTitle("1元秒商品参与")
                .setDesc("1元秒商品参与")
                .setPayMethod(PayMethod.WECHAT)
                .pay(new PayListener() {
                    @Override
                    public void onSuccess(String orderId) {
                        orderCode = orderId;
                        queryIssue();
                    }

                    @Override
                    public void onFailure(int code, String msg, String orderId) {
                        orderCode = orderId;
                        isLoading = false;
                        hideProgress();

                        switch (code) {
                            case -3:
                                showShortToast("未安装安全支付插件，请安装后再试");
                                FileUtils.installBmobPayPlugin(me);
                                break;

                            case 7777:
                                showShortToast("未安装微信客户端");
                                break;

                            case 8888:
                                showShortToast("微信版本过低或被加了应用锁");
                                break;

                            case 9010:
                            case 9016:
                                showShortToast("网络异常");
                                break;

                            case 10777:
                                BP.ForceFree();
                                showShortToast("支付失败，请重试");
                                break;

                            case Pay.RESULT_UNKNOWN:
                            case Pay.RESULT_NOT_PAY:
                                String state = code == Pay.RESULT_NOT_PAY ?
                                        Pay.STATE_NOT_PAY : Pay.STATE_UNKNOWN;
                                Issue issue = product == null ? null : product.getCurrentIssue();
                                recordPay(issue, amount, state, orderCode);
                                goPayResult(false);
                                finish();
                                break;

                            default:
                            case 10012: //客户端支付未通过审核
                            case 10003: //微信返回
                                if (TextUtils.isEmpty(orderId)) {
                                    showShortToast("支付失败");
                                    return;
                                }
                                Issue dftIssue = product == null ? null : product.getCurrentIssue();
                                recordPay(dftIssue, amount, Pay.STATE_UNKNOWN, orderCode);
                                goPayResult(false);
                                finish();
                                break;
                        }

                        try {
                            LogU.e("支付结果", "code:" + code, "msg:" + msg, "orderCode:" + orderId);
                            BmobException exception = new BmobException(code, orderCode + ":" + msg);
                            StatService.reportException(me, exception);
                        } catch (Exception e) {
                        }
                    }
                });
    }

    /**
     * step2，查询当前期号剩余人次
     */
    private void queryIssue() {
        BmobQuery<Product> query = new BmobQuery<Product>();
        query.include("currentIssue");
        query.getObject(me, proObjId, new GetListener<Product>() {
            @Override
            public void onSuccess(Product product) {
                PayConfirmActivity.this.product = product;
                if (product != null) {
                    Issue issue = product.getCurrentIssue();
                    double p = product.getPrice();
                    double pt = issue.getPersonTimes();
                    double rest = p - pt;
                    boolean needNewIssue = false;
                    if (rest > 0) {
                        realAmount = 0d;
                        if (amount < rest) {
                            realAmount = amount;
                        } else {
                            realAmount = rest;
                            needNewIssue = true;
                        }
                        if (coinPay) {
                            decreaseAsset(product, realAmount, needNewIssue);
                        } else {
                            incrementIssue(product, realAmount, needNewIssue);
                        }

                    } else {
                        hideProgress();
                        showShortToast("本期已结束");
                        finish();
                    }

                } else {
                    hideProgress();
                    showShortToast("发生错误");
                    finish();
                }
            }

            @Override
            public void onFailure(int i, String s) {
                hideProgress();
                showShortToast("支付失败");
                isLoading = false;
            }
        });
    }

    /**
     * step3，扣除用户秒币
     *
     * @param product
     * @param ptCoin
     * @param needNewIssue
     */
    private void decreaseAsset(
            final Product product, final double ptCoin,
            final boolean needNewIssue) {
        Asset asset = new Asset();
        asset.setObjectId(assetObjId);
        asset.increment("oysCoin", -ptCoin);
        asset.update(me, new UpdateListener() {
            @Override
            public void onSuccess() {
                incrementIssue(product, ptCoin, needNewIssue);
            }

            @Override
            public void onFailure(int i, String s) {
                hideProgress();
                showShortToast("支付失败");
                isLoading = false;
            }
        });
    }

    /**
     * step4，增加当前期号已秒杀人次
     *
     * @param product
     * @param realPt
     * @param needNewIssue
     */
    private void incrementIssue(
            final Product product, final double realPt,
            final boolean needNewIssue) {

        //增加当前期号至用户列表
        addIssueToUser(product.getCurrentIssue());

        //新增消费记录
        recordPay(product.getCurrentIssue(), realPt, Pay.STATE_SUCCESS, orderCode);

        Issue issue = new Issue();
        issue.setObjectId(product.getCurrentIssue().getObjectId());
        if (needNewIssue) {
            issue.setAnnounceState(SeckillState.ANNOUNCING.getValue());
            issue.setTotalPersonTimes(product.getPrice().longValue());
            finishIssue(product.getCurrentIssue().getObjectId());
        }
        issue.increment("personTimes", realPt);

        issue.update(me, new UpdateListener() {
            @Override
            public void onSuccess() {
                //计算号码
                compSeckillNo(product, realPt);

                //新增issue
                boolean isNAIssue = product.isNeedAddIssue() == null ?
                        true : product.isNeedAddIssue();
                if (needNewIssue && isNAIssue) {
                    newIssue(product);

                } else if (!isNAIssue) {
                    Product proUpdate = new Product(product.getObjectId());
                    proUpdate.remove("currentIssue");
                    proUpdate.setCanBuy(false);
                    proUpdate.update(me);
                }
            }

            @Override
            public void onFailure(int i, String s) {
                hideProgress();
                finish();
                showShortToast("出现错误");
                LogU.e("出现错误", "code:" + i, "msg:" + s);
            }
        });
    }

    /**
     * step5，增加当前期号至用户列表
     *
     * @param issue
     */
    private void addIssueToUser(Issue issue) {
        BmobRelation br = new BmobRelation();
        br.add(issue);

        OysUser user = new OysUser();
        user.setObjectId(UserHelper.getObjectId());
        user.setIssues(br);
        user.update(me);
    }

    /**
     * step5，新增消费记录
     *
     * @param issue
     * @param amount
     * @param state
     */
    private void recordPay(Issue issue, double amount, String state, String orderCode) {
        Expense ex = new Expense();
        ex.setUser(UserHelper.getUser());
        ex.setIssue(issue);
        ex.setAmount(amount);
        ex.setOrderCode(orderCode);
        if (coinPay) {
            ex.setMethod(PayMethod.REST_COIN.getValue());
        } else {
            ex.setMethod(PayMethod.WECHAT.getValue());
        }
        ex.setDesc("商品参与");
        ex.setState(state);
        ex.save(me);
    }

    /**
     * step5，记录当前期号结束时间
     *
     * @param objId
     */
    private void finishIssue(final String objId) {
        Bmob.getServerTime(me, new GetServerTimeListener() {
            @Override
            public void onSuccess(long l) {
                Date crtDate = new Date(l * 1000);
                Issue issue = new Issue();
                issue.setObjectId(objId);
                issue.setFinishedAt(new BmobDate(crtDate));
                issue.update(me);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     * step5，新增issue
     *
     * @param pro
     */
    private void newIssue(final Product pro) {
        final Issue issue = new Issue();
        issue.setAnnounceState(SeckillState.SECKILLING.getValue());
        issue.setPersonTimes(0l);
        issue.setProduct(pro);
        issue.save(me, new SaveListener() {
            @Override
            public void onSuccess() {
                Product product = new Product(pro.getObjectId());
                product.setCurrentIssue(issue);
                product.update(me);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     * step6，计算号码
     *
     * @param product
     * @param a
     */
    private void compSeckillNo(final Product product, final double a) {
        BmobQuery<Seckill> query = new BmobQuery<>();
        query.addWhereEqualTo("issue", new BmobPointer(product.getCurrentIssue()));
        query.findObjects(me, new FindListener<Seckill>() {
            @Override
            public void onSuccess(List<Seckill> list) {
                //已存在的号码
                List<Long> nos = new ArrayList<Long>();
                if (list != null) {
                    Iterator<Seckill> i = list.iterator();
                    while (i.hasNext()) {
                        Seckill seckill = i.next();
                        List<Long> itemNos = seckill.getSeckillNo();
                        if (itemNos != null) {
                            nos.addAll(itemNos);
                        }
                    }
                }

                HashSet<Long> rst = SeckillNoUtils.genDiffRandom(
                        nos,
                        product.getPrice().intValue(),
                        (int) a);
                List<Long> lstNos = new ArrayList<Long>();
                lstNos.addAll(rst);
                addSeckill(lstNos, product);
            }

            @Override
            public void onError(int i, String s) {
                hideProgress();
                finish();
                showShortToast("发生错误");
                LogU.e("出现错误", "code:" + i, "msg:" + s);
            }
        });
    }

    /**
     * step7，新增参与记录
     *
     * @param nos
     * @param product
     */
    private void addSeckill(final List<Long> nos, final Product product) {
        Bmob.getServerTime(me, new GetServerTimeListener() {
            @Override
            public void onSuccess(long l) {
                Date d = new Date(l * 1000);
                Date md = DateTimeUtils.addDate(
                        d, Calendar.MILLISECOND, DateTimeUtils.getNowMillis());

                Seckill seckill = new Seckill();
                seckill.setUser(UserHelper.getUser());
                seckill.setIssue(product.getCurrentIssue());
                seckill.setIp(DeviceUtils.getPublicIp());
                seckill.setPersonTimes(nos.size());
                seckill.setSeckillAt(md.getTime());
                seckill.setSeckillNo(nos);
                seckill.save(me, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        increasePersonTimes(product, nos.size());
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        hideProgress();
                        finish();
                        showShortToast("发生错误");
                        LogU.e("出现错误", "code:" + i, "msg:" + s);
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                hideProgress();
                finish();
                showShortToast("发生错误");
                LogU.e("出现错误", "code:" + i, "msg:" + s);
            }
        });
    }

    /**
     * step8，增加用户参与次数
     *
     * @param product
     * @param count
     */
    private void increasePersonTimes(final Product product, final int count) {
        BmobQuery<UserPersonTimes> query = new BmobQuery<>();
        query.addWhereEqualTo("issue", new BmobPointer(product.getCurrentIssue()));
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.findObjects(me, new FindListener<UserPersonTimes>() {
            @Override
            public void onSuccess(List<UserPersonTimes> list) {
                if (list != null && !list.isEmpty()) {
                    UserPersonTimes upt = list.get(0);
                    UserPersonTimes nUpt = new UserPersonTimes();
                    nUpt.setObjectId(upt.getObjectId());
                    nUpt.increment("personTimes", count);
                    nUpt.update(me);
                } else {
                    UserPersonTimes nUpt = new UserPersonTimes();
                    nUpt.setIssue(product.getCurrentIssue());
                    nUpt.setUser(UserHelper.getUser());
                    nUpt.setIssueId(product.getCurrentIssue().getObjectId());
                    nUpt.setPersonTimes((long) count);
                    nUpt.save(me);
                }

                hideProgress();
                isLoading = false;

                goPayResult(true);
                finish();
            }

            @Override
            public void onError(int i, String s) {
                hideProgress();
                showShortToast("发生错误");
                finish();
                LogU.e("出现错误", "code:" + i, "msg:" + s);
            }
        });
    }

    private void updateSecCoin() {
        String coinStr = new DecimalFormat("#.##").format(secCoin);

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append("秒币支付 (剩余：");
        ssb.append(TextStyleUtils.genColorText(coinStr + "秒币", 0xfff85757));
        ssb.append(")");
        rbRestCoin.setText(ssb);

        if (firstUpdateRestCoin) {
            if (secCoin >= amount) {
                rbRestCoin.setChecked(true);
            } else {
                rbWechat.setChecked(true);
            }

            firstUpdateRestCoin = false;
        }
    }

    private void queryOysCoin() {
        BmobQuery<Asset> query = new BmobQuery<Asset>();
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.setMaxCacheAge(TimeUnit.SECONDS.toMillis(15)); //15s
        query.findObjects(me, new FindListener<Asset>() {
            @Override
            public void onSuccess(List<Asset> list) {
                if (list != null && !list.isEmpty()) {
                    Double coin = list.get(0).getOysCoin();
                    secCoin = coin;
                } else {
                    secCoin = 0d;
                }
                updateSecCoin();
            }

            @Override
            public void onError(int i, String s) {
                switch (i) {
                    case 9009:
                        return;
                }
                secCoin = 0d;
                updateSecCoin();
            }
        });
    }
}
