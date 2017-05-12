package com.fynn.oyseckill.app.module.home.detail;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseActivity;
import com.fynn.oyseckill.app.core.CommonPagerAdapter;
import com.fynn.oyseckill.app.module.account.lgrg.LoginActivity;
import com.fynn.oyseckill.app.module.account.pay.PayConfirmActivity;
import com.fynn.oyseckill.app.module.account.user.SeckillNoDetailActivity;
import com.fynn.oyseckill.app.module.home.util.ProductUtils;
import com.fynn.oyseckill.app.module.home.util.TextStyleUtils;
import com.fynn.oyseckill.model.entity.Issue;
import com.fynn.oyseckill.model.entity.OysUser;
import com.fynn.oyseckill.model.entity.Product;
import com.fynn.oyseckill.model.entity.Seckill;
import com.fynn.oyseckill.util.ImageUtils;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.widget.CircleImageView;
import com.fynn.oyseckill.widget.CirclePageIndicator;
import com.fynn.oyseckill.widget.Titlebar;
import com.fynn.oyseckill.widget.VerticalViewPager;
import com.fynn.oyseckill.widget.dialog.SeckillCountPicker;

import org.appu.common.ParamMap;
import org.appu.common.utils.DateTimeUtils;
import org.appu.common.utils.LogU;
import org.appu.common.utils.TextUtils;
import org.appu.common.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by Fynn on 2016/7/7.
 */
public class ProductDetailActivity extends BaseActivity {

    private static final int LIMIT = 10;
    private VerticalViewPager vvpProduct;
    private View topLayout;
    private View bottomLayout;
    private DetailBottomActionBarFragment btmAbFragment;
    private ImageView ivGoback;
    private ImageView ivShare;
    /**
     * Top Layout
     */
    private ProductDetailHeaderPager headPicsPager;
    private CirclePageIndicator indicator;
    private TextView tvTitle;
    private TextView tvDesc;
    private TextView tvIssue;
    private ProgressBar pbProgress;
    private TextView tvTotalPerTimes;
    private TextView tvRestPerTimes;
    private TextView tvMyPerTimesEmpty;
    private TextView tvMyPerTimes;
    private TextView tvMySeckillNo;
    private TextView tvRecord;
    private LinearLayout llImageTextDetail;
    private LinearLayout llForwardResult;
    private LinearLayout llShare;
    private LinearLayout llSeckillWorking;
    private RelativeLayout rlSeckillAnnouncing;
    private LinearLayout llSeckillAnnounced;
    private TextView tvAnnouncingIssueNo;
    private TextView tvAnnouncingCompDetail;
    private CircleImageView ivUserProfile;
    private TextView tvUserName;
    private TextView tvUserId;
    private TextView tvUserIssueNo;
    private TextView tvUserPt;
    private TextView tvLuckSecNo;
    private TextView tvAnnouncedCompDetail;
    /**
     * Bottom Layout
     */
    private ListView lvSeckill;
    private LinearLayout llEmpty;
    private long issueNo = -1;
    private Issue issue;
    private List<Seckill> seckills;
    private SeckillRecordAdapter secAdapter;
    private int page = 0;
    private boolean isLoading;
    private boolean isFinish;

    private int pt = 0;

    @Override
    public int getContentResId() {
        return R.layout.activity_product_detail;
    }

    @Override
    public void handleIntent() {
        ParamMap<String, Object> params = getParams();
        if (params != null) {
            Object id = params.get("issueNo");
            try {
                issueNo = Long.valueOf(String.valueOf(id));
            } catch (Exception e) {
                LogU.w(e);
            }
        }
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        vvpProduct = $(R.id.vvp_product);
        topLayout = LayoutInflater.from(this).inflate(R.layout.layout_product_top, null);
        bottomLayout = LayoutInflater.from(this).inflate(R.layout.layout_product_bottom, null);
        btmAbFragment = (DetailBottomActionBarFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_bottom);
        ivGoback = $(R.id.iv_goback);
        ivShare = $(R.id.iv_share);

        //Top Layout
        headPicsPager = ViewUtils.findViewById(topLayout, R.id.head_pager_pics);
        indicator = ViewUtils.findViewById(topLayout, R.id.indicator);
        tvTitle = ViewUtils.findViewById(topLayout, R.id.tv_title);
        tvDesc = ViewUtils.findViewById(topLayout, R.id.tv_desc);
        tvIssue = ViewUtils.findViewById(topLayout, R.id.tv_issue);
        pbProgress = ViewUtils.findViewById(topLayout, R.id.pb_progress);
        tvTotalPerTimes = ViewUtils.findViewById(topLayout, R.id.tv_total);
        tvRestPerTimes = ViewUtils.findViewById(topLayout, R.id.tv_rest);
        tvMyPerTimesEmpty = ViewUtils.findViewById(topLayout, R.id.tv_empty);
        tvMyPerTimes = ViewUtils.findViewById(topLayout, R.id.tv_my_person_times);
        tvMySeckillNo = ViewUtils.findViewById(topLayout, R.id.tv_my_seckill_no);
        llImageTextDetail = ViewUtils.findViewById(topLayout, R.id.ll_image_text_detail);
        llForwardResult = ViewUtils.findViewById(topLayout, R.id.ll_forward_result);
        llShare = ViewUtils.findViewById(topLayout, R.id.ll_share);
        tvRecord = ViewUtils.findViewById(topLayout, R.id.tv_record);

        llSeckillWorking = ViewUtils.findViewById(topLayout, R.id.ll_seckill_working);
        rlSeckillAnnouncing = ViewUtils.findViewById(topLayout, R.id.rl_seckill_announcing);
        llSeckillAnnounced = ViewUtils.findViewById(topLayout, R.id.ll_seckill_announced);
        tvAnnouncingIssueNo = ViewUtils.findViewById(topLayout, R.id.tv_issue_no);
        tvAnnouncingCompDetail = ViewUtils.findViewById(topLayout, R.id.tv_announcing_comp_detail);

        ivUserProfile = ViewUtils.findViewById(topLayout, R.id.iv_user_profile);
        tvUserName = ViewUtils.findViewById(topLayout, R.id.tv_user_name);
        tvUserId = ViewUtils.findViewById(topLayout, R.id.tv_user_id);
        tvUserIssueNo = ViewUtils.findViewById(topLayout, R.id.tv_user_issue_no);
        tvUserPt = ViewUtils.findViewById(topLayout, R.id.tv_user_pt);
        tvLuckSecNo = ViewUtils.findViewById(topLayout, R.id.tv_luck_seckill_no);
        tvAnnouncedCompDetail = ViewUtils.findViewById(topLayout, R.id.tv_announced_comp_detail);

        //Bottom Layout
        lvSeckill = ViewUtils.findViewById(bottomLayout, R.id.lv_seckill);
        llEmpty = ViewUtils.findViewById(bottomLayout, R.id.ll_empty);
        titlebar = ViewUtils.findViewById(bottomLayout, R.id.titlebar);

        seckills = new ArrayList<Seckill>();
        secAdapter = new SeckillRecordAdapter();
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        setOnClick(ivGoback, llImageTextDetail, llForwardResult,
                llShare, tvMySeckillNo, tvRecord, tvMyPerTimesEmpty, ivShare);

        List<View> list = new ArrayList<View>();
        list.add(topLayout);
        list.add(bottomLayout);
        vvpProduct.setAdapter(new CommonPagerAdapter(list));
        indicator.setViewPager(headPicsPager);

        titlebar.setRightImageActionClickListener(
                Titlebar.IMAGE_ACTION_RIGHT_FIRST, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vvpProduct.setCurrentItem(0);
                    }
                });

        titlebar.setGoBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        vvpProduct.setOnPageChangeListener(new VerticalViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        if (positionOffset > 0) {
                            ivGoback.setAlpha(1 - positionOffset);
                            ivShare.setAlpha(1 - positionOffset);
                        }
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        ivGoback.setVisibility(View.VISIBLE);
                        ivGoback.setAlpha(1f);
                        ivShare.setVisibility(View.VISIBLE);
                        ivShare.setAlpha(1f);
                        break;

                    case 1:
                        ivGoback.setAlpha(0f);
                        ivGoback.setVisibility(View.GONE);

                        ivShare.setAlpha(0f);
                        ivShare.setVisibility(View.GONE);

                        if (seckills.size() <= 0 && issue != null) {
                            querySeckillRecord(issue, false);
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case VerticalViewPager.SCROLL_STATE_DRAGGING: //开始滑动
                        ivGoback.setVisibility(View.VISIBLE);
                        ivShare.setVisibility(View.VISIBLE);
                        break;

                    case VerticalViewPager.SCROLL_STATE_IDLE: //滚动动画结束
                        if (vvpProduct.getCurrentItem() == 0) {
                            ivGoback.setVisibility(View.VISIBLE);
                            ivGoback.setAlpha(1f);
                            ivShare.setVisibility(View.VISIBLE);
                            ivShare.setAlpha(1f);
                        } else {
                            ivGoback.setVisibility(View.GONE);
                            ivShare.setVisibility(View.GONE);
                        }
                        break;

                }
            }
        });

        lvSeckill.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        boolean toBottom = view.getLastVisiblePosition() == view.getCount() - 1;
                        if (!isFinish && !isLoading && toBottom) {
                            if (issue != null) {
                                querySeckillRecord(issue);
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        lvSeckill.setEmptyView(llEmpty);
        lvSeckill.setAdapter(secAdapter);

        llSeckillWorking.setVisibility(View.VISIBLE);
        llSeckillAnnounced.setVisibility(View.GONE);
        rlSeckillAnnouncing.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_goback:
                finish();
                break;

            case R.id.ll_image_text_detail:
                if (issue != null) {
                    ParamMap<String, Object> params = new ParamMap<String, Object>();
                    params.put("productId", issue.getProduct().getProductId());
                    startActivity(ProductImageTextDetailActivity.class, params);
                }
                break;

            case R.id.ll_forward_result:
                if (issue != null) {
                    ParamMap<String, Object> params = new ParamMap<String, Object>();
                    params.put("productObjectId", issue.getProduct().getObjectId());
                    startActivity(ForwardAnnActivity.class, params);
                }
                break;

            case R.id.ll_share:
                if (issue != null) {
                    ParamMap<String, Object> params = new ParamMap<>();
                    params.put("showTitlebar", false);
                    params.put("productObjId", issue.getProduct().getObjectId());
                    startActivity(ProductOrderShareActivity.class, params);
                }
                break;

            case R.id.tv_my_seckill_no:
                if (issue != null) {
                    if (!TextUtils.isEmpty(tvMySeckillNo.getText().toString().trim())) {
                        ParamMap<String, Object> params = new ParamMap<String, Object>();
                        params.put("issueObjId", issue.getObjectId());
                        params.put("productName", issue.getProduct().getName());
                        params.put("issueNo", issue.getIssueNumber());
                        params.put("pts", pt);
                        startActivity(SeckillNoDetailActivity.class, params);
                    }
                }
                break;

            case R.id.tv_record:
                vvpProduct.setCurrentItem(1);
                break;

            case R.id.tv_empty:
                if (!UserHelper.isLogin()) {
                    startActivity(LoginActivity.class);
                }
                break;

            case R.id.iv_share:
                showShare();
                break;
        }
    }

    private void showShare() {
        if (issue == null) {
            return;
        }

        OnekeyShare oks = new OnekeyShare();
        String appUrl = "http://oyseckill.bmob.cn";
        String productName = issue.getProduct().getName();
        String appName = getResources().getString(R.string.app_name);
        String shareDesc = "我在" + appName + "发现了好东西！我已经参与了，火速分享给你！";

        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用

        oks.setTitle(shareDesc);

        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl(appUrl);

        // text是分享文本，所有平台都需要这个字段
        oks.setText(productName);

        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片

        String dftUrl = "http://bmob-cdn-2194.b0.upaiyun.com/2016/09/21/e5e94f8e403fb32d804724cfc38d68b4.png";
        String proUrl = issue.getProduct().getImage();
        if (!TextUtils.isEmpty(proUrl)) {
            oks.setImageUrl(proUrl);
        } else {
            oks.setImageUrl(dftUrl);
        }

        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(appUrl);

        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment(shareDesc);

        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(appName);

        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(appUrl);

        //不弹出编辑框
        oks.setSilent(true);

        // 启动分享GUI
        oks.show(this);
    }

    private void fetchSeckillInfo() {
        if (issueNo < 0) {
            return;
        }
        queryIssue();
    }

    private void queryIssue() {
        BmobQuery<Issue> query = new BmobQuery<Issue>();
        query.addWhereEqualTo("issueNumber", issueNo);
        query.include("product,product.currentIssue,succeedSeckill,succeedSeckill.user");
        query.findObjects(me, new FindListener<Issue>() {
            @Override
            public void onSuccess(List<Issue> list) {
                if (list != null && list.size() > 0) {
                    issue = list.get(0);
                    updateIssueView(issue);
                } else {
                    finish();
                    showShortToast("商品不存在");
                }
            }

            @Override
            public void onError(int i, String s) {
                switch (i) {
                    case 9016:
                    case 9010:
                        showShortToast("网络不给力~");
                        finish();
                        break;
                }
                LogU.e("获取商品期号详情失败", "code：" + i, "msg：" + s);
            }
        });

    }

    private SpannableStringBuilder genLogoutDesc() {
        SpannableString ss1 = TextStyleUtils.genAppearanceText("登录", 0xFF4876FF, 13);
        SpannableString ss2 = TextStyleUtils.genAppearanceText("查看参与记录", 0xFFA9A9A9, 11);
        SpannableStringBuilder ssb = new SpannableStringBuilder(ss1);
        ssb.append(ss2);
        return ssb;
    }

    private void querySeckillNo(Issue issue) {
        if (!UserHelper.isLogin()) {
            tvMyPerTimesEmpty.setText(genLogoutDesc());
            tvMyPerTimesEmpty.setVisibility(View.VISIBLE);
            tvMyPerTimes.setVisibility(View.GONE);
            tvMySeckillNo.setVisibility(View.GONE);
            return;
        }
        BmobQuery<Seckill> query = new BmobQuery<Seckill>();
        query.addWhereEqualTo("issue", new BmobPointer(issue));
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.findObjects(me, new FindListener<Seckill>() {
            @Override
            public void onSuccess(List<Seckill> list) {
                if (list != null && !list.isEmpty()) {
                    updateSeckillNoView(list);
                } else {
                    tvMyPerTimesEmpty.setText("您没有参与本次秒杀");
                    tvMyPerTimesEmpty.setVisibility(View.VISIBLE);
                    tvMyPerTimes.setVisibility(View.GONE);
                    tvMySeckillNo.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void querySucceedSeckillPt(Issue issue, OysUser user) {
        BmobQuery<Seckill> query = new BmobQuery<Seckill>();
        query.addWhereEqualTo("issue", new BmobPointer(issue));
        query.addWhereEqualTo("user", user);
        query.findObjects(me, new FindListener<Seckill>() {
            @Override
            public void onSuccess(List<Seckill> list) {
                if (list != null && !list.isEmpty()) {
                    int pt = 0;
                    Iterator<Seckill> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        Seckill s = iterator.next();
                        int p = s.getPersonTimes() == null ? 0 : s.getPersonTimes();
                        pt += p;
                    }
                    tvUserPt.setText("本次参与：" + pt + "人次");
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void querySeckillRecord(Issue issue) {
        querySeckillRecord(issue, true);
    }

    private void querySeckillRecord(Issue issue, boolean showProgress) {
        if (isLoading || isFinish) {
            return;
        }
        isLoading = true;
        if (showProgress) {
            showProgress();
        }

        BmobQuery<Seckill> query = new BmobQuery<Seckill>();
        query.addWhereEqualTo("issue", new BmobPointer(issue));
        query.include("user");
        query.order("-seckillAt");
        query.setLimit(LIMIT);
        query.setSkip(page * LIMIT);
        query.findObjects(me, new FindListener<Seckill>() {
            @Override
            public void onSuccess(List<Seckill> list) {
                isLoading = false;
                hideProgress();

                if (list != null && !list.isEmpty()) {
                    if (page == 0) {
                        seckills.clear();
                    }
                    seckills.addAll(list);
                    secAdapter.notifyDataSetChanged();
                    page++;
                    if (list.size() < LIMIT) {
                        isFinish = true;
                    }

                } else {
                    isFinish = true;
                }
            }

            @Override
            public void onError(int i, String s) {
                isLoading = false;
                hideProgress();
            }
        });
    }

    private void updateIssueView(Issue issue) {
        if (issue == null || issue.getProduct() == null) {
            tvTitle.setText("");
            tvDesc.setText("");
            tvIssue.setText("期号 0");
            tvTotalPerTimes.setText("总需 0");
            tvRestPerTimes.setText("剩余 0");
            pbProgress.setProgress(0);
            tvMyPerTimesEmpty.setVisibility(View.VISIBLE);
            tvMyPerTimesEmpty.setText("您没有参与本次秒杀");
            tvMyPerTimes.setVisibility(View.GONE);
            tvMySeckillNo.setVisibility(View.GONE);

            llSeckillWorking.setVisibility(View.VISIBLE);
            llSeckillAnnounced.setVisibility(View.GONE);
            rlSeckillAnnouncing.setVisibility(View.GONE);

            btmAbFragment.setOnActionClickListener(null);

            seckills.clear();
            secAdapter.notifyDataSetChanged();
            return;
        }

        querySeckillNo(issue);

        final Product product = issue.getProduct();
        final String name = product.getName();
        String desc = product.getDesc();
        long issueNo = issue.getIssueNumber() == null ? 0 : issue.getIssueNumber();
        long price = product.getPrice() == null ? 0 : product.getPrice().longValue();
        long crtPersonTimes = issue.getPersonTimes() == null ? 0 : issue.getPersonTimes();
        final long restPt = price - crtPersonTimes;
        int progressNo = ProductUtils.getProgressInHundred(price, crtPersonTimes);
        Seckill seckill = issue.getSucceedSeckill();
        List<String> bannerImages = product.getBannerImages();

        if (bannerImages != null && !bannerImages.isEmpty()) {
            headPicsPager.update(bannerImages);
        }
        tvTitle.setText(name);
        tvDesc.setText(desc);
        btmAbFragment.getNumberChooser().setMaxNum((int) restPt);

        SeckillState secState = getSeckillState(issue);
        switch (secState) {
            case SECKILLING:
                llSeckillWorking.setVisibility(View.VISIBLE);
                rlSeckillAnnouncing.setVisibility(View.GONE);
                llSeckillAnnounced.setVisibility(View.GONE);

                tvIssue.setText("期号 " + issueNo);
                tvTotalPerTimes.setText("总需 " + price);
                tvRestPerTimes.setText("剩余 " + restPt);
                pbProgress.setProgress(progressNo);
                break;

            case ANNOUNCING:
                rlSeckillAnnouncing.setVisibility(View.VISIBLE);
                llSeckillWorking.setVisibility(View.GONE);
                llSeckillAnnounced.setVisibility(View.GONE);

                tvAnnouncingCompDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoCompNumDetail();
                    }
                });
                tvAnnouncingIssueNo.setText("期号：" + issueNo);
                break;

            case ANNOUNCED:
                llSeckillAnnounced.setVisibility(View.VISIBLE);
                rlSeckillAnnouncing.setVisibility(View.GONE);
                llSeckillWorking.setVisibility(View.GONE);

                OysUser user = seckill.getUser();

                //头像
                if (user.getProfile() == null || TextUtils.isEmpty(user.getProfile().getFileUrl(me))) {
                    ivUserProfile.setImageResource(R.drawable.icon_user_profile_normal);
                } else {
                    ImageUtils.getInstance().display(user.getProfile().getFileUrl(me), ivUserProfile,
                            R.drawable.icon_user_profile_normal);
                }

                //用户名
                SpannableStringBuilder ssBuilder = new SpannableStringBuilder();
                SpannableString ss = TextStyleUtils.genColorText(UserHelper.getNickname(user),
                        Color.parseColor("#4876FF"));
                ssBuilder.append("获得者：").append(ss);
                tvUserName.setText(ssBuilder);

                //用户ID
                tvUserId.setText("用户ID：" + String.valueOf(user.getUserId()) + "(唯一不变标识)");

                //期号
                tvUserIssueNo.setText("期号：" + issueNo);

                //本次参与
                querySucceedSeckillPt(issue, user);

                //幸运号码
                tvLuckSecNo.setText("幸运号码：" + issue.getSucceedSeckillNo());

                //计算详情
                tvAnnouncedCompDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoCompNumDetail();
                    }
                });
                break;
        }

        //Bottom Action Bar
        if (!this.isFinish()) {
            if (!product.getCanBuy() || product.getCurrentIssue() == null || product.getCurrentIssue().getIssueNumber() == null) {
                getSupportFragmentManager().beginTransaction()
                        .hide(btmAbFragment).commitAllowingStateLoss();
                return;
            } else {
                getSupportFragmentManager().beginTransaction()
                        .show(btmAbFragment).commitAllowingStateLoss();
            }
        }

        switch (secState) {
            case SECKILLING:
                btmAbFragment.setMode(Mode.DEFAULT);
                btmAbFragment.setAction("立即秒杀", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoPayConfirm(btmAbFragment.getNumberChooser().getNum(),
                                product.getImage(), name, product.getObjectId());
                    }
                });
                btmAbFragment.getNumberChooser().setMaxNum((int) restPt);

                if (restPt > 10) {
                    btmAbFragment.getNumberChooser().setNum(10);
                } else {
                    btmAbFragment.getNumberChooser().setNum((int) restPt);
                }

                btmAbFragment.getNumberChooser().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SeckillCountPicker.Builder(me)
                                .setMaxValue(btmAbFragment.getNumberChooser().getMaxNum())
                                .setMinValue(btmAbFragment.getNumberChooser().getMinNum())
                                .setValue(btmAbFragment.getNumberChooser().getNum())
                                .setOnSeckillClickListener(new SeckillCountPicker.OnSeckillClickListener() {
                                    @Override
                                    public void onSeckillClick(SeckillCountPicker picker, int value) {
                                        picker.dismiss();
                                        gotoPayConfirm(value, product.getImage(),
                                                name, product.getObjectId());
                                    }
                                })
                                .show();
                    }
                });
                break;

            case ANNOUNCING:
            case ANNOUNCED:
                btmAbFragment.setMode(Mode.HIDE_NUM_PICKER);
                btmAbFragment.setAction("立即前往", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ParamMap<String, Object> params = new ParamMap<String, Object>();
                        params.put("issueNo", product.getCurrentIssue().getIssueNumber());
                        params.put("objectId", product.getObjectId());
                        startActivity(ProductDetailActivity.class, params);
                    }
                });
                break;
        }
    }

    private void gotoPayConfirm(
            double value, String picUrl, String productName, String proObjId) {
        ParamMap<String, Object> params = new ParamMap<String, Object>();
        params.put("amount", value);
        params.put("picUrl", picUrl);
        params.put("productName", productName);
        params.put("proObjId", proObjId);
        startActivityForLogin(PayConfirmActivity.class, params);
    }

    private void gotoCompNumDetail() {
        ParamMap<String, Object> params = new ParamMap<>();
        params.put("issueObjId", issue.getObjectId());
        startActivity(CompNumDetailActivity.class, params);
    }

    private void updateSeckillNoView(List<Seckill> seckills) {
        List<Long> seckillNos = new ArrayList<Long>();
        StringBuilder builder = new StringBuilder();
        pt = 0;
        Iterator<Seckill> iterator = seckills.iterator();
        while (iterator.hasNext()) {
            Seckill s = iterator.next();
            List<Long> nos = s.getSeckillNo();
            int p = s.getPersonTimes() == null ? 0 : s.getPersonTimes();
            pt += p;
            if (nos != null && !nos.isEmpty()) {
                seckillNos.addAll(nos);
            }
        }

        int count = seckillNos.size() > 60 ? 60 : seckillNos.size();
        for (int i = 0; i < count; i++) {
            if (i == count - 1) {
                builder.append(seckillNos.get(i));
            } else {
                builder.append(seckillNos.get(i)).append(" ");
            }
        }

        if (count < seckillNos.size()) {
            builder.append("\n ···");
        }

        tvMyPerTimesEmpty.setVisibility(View.GONE);
        tvMyPerTimes.setVisibility(View.VISIBLE);
        tvMySeckillNo.setVisibility(View.VISIBLE);

        SpannableString sString = new SpannableString(String.valueOf(pt));
        sString.setSpan(
                new ForegroundColorSpan(Color.parseColor("#F85757")),
                0,
                sString.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableStringBuilder ssBuilder = new SpannableStringBuilder();
        ssBuilder.append("您参与了 ").append(sString).append(" 人次，秒杀号码：");

        tvMyPerTimes.setText(ssBuilder);
        tvMySeckillNo.setText(builder);
    }

    private SeckillState getSeckillState(Issue issue) {
        Product product = issue.getProduct();
        Seckill seckill = issue.getSucceedSeckill();
        Long secNo = issue.getSucceedSeckillNo();
        Integer state = issue.getAnnounceState();

        if (state == null) {
            if (issue.getPersonTimes() < product.getPrice()) {
                return SeckillState.SECKILLING;
            } else {
                //尚未揭晓
                if (seckill == null || secNo == null || secNo.intValue() == 0) {
                    return SeckillState.ANNOUNCING;
                }
                //已经揭晓
                else {
                    return SeckillState.ANNOUNCED;
                }
            }

        } else {
            switch (state) {
                case 0:
                default:
                    return SeckillState.SECKILLING;
                case 1:
                    return SeckillState.ANNOUNCING;
                case 2:
                    return SeckillState.ANNOUNCED;
            }
        }
    }

    @Override
    protected void onResume() {
        fetchSeckillInfo();
        super.onResume();
    }

    class SeckillRecordAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return seckills.size();
        }

        @Override
        public Object getItem(int position) {
            return seckills.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null || convertView.getTag() == null) {
                convertView = LayoutInflater.from(me).inflate(R.layout.layout_seckill_record_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Seckill seckill = seckills.get(position);
            OysUser user = seckill.getUser();
            String url = user.getProfile() == null ? "" : user.getProfile().getFileUrl(me);
            long millSec = seckill.getSeckillAt() == null ? 0 : seckill.getSeckillAt();

            holder.tvIP.setText("IP:" + (!TextUtils.isEmpty(seckill.getIp()) ? seckill.getIp() : "未知"));
            holder.tvName.setText(UserHelper.getNickname(user));
            holder.tvTime.setText(DateTimeUtils.formatDate(millSec, "yyyy-MM-dd HH:mm:ss.SSS"));

            SpannableStringBuilder ssBuilder = new SpannableStringBuilder();
            ssBuilder.append("参与了")
                    .append(ProductUtils.getColorfulString(
                            String.valueOf(seckill.getPersonTimes()), Color.parseColor("#f85757")))
                    .append("人次");
            holder.tvPt.setText(ssBuilder);

            if (!TextUtils.isEmpty(url)) {
                ImageUtils.getInstance().display(url, holder.ivUserProfile,
                        R.drawable.icon_user_profile_normal);
            } else {
                holder.ivUserProfile.setImageResource(R.drawable.icon_user_profile_normal);
            }

            return convertView;
        }

        class ViewHolder {
            private ImageView ivUserProfile;
            private TextView tvName;
            private TextView tvIP;
            private TextView tvPt;
            private TextView tvTime;

            public ViewHolder(View convertView) {
                ivUserProfile = ViewUtils.findViewById(convertView, R.id.civ_user_profile);
                tvName = ViewUtils.findViewById(convertView, R.id.tv_user_name);
                tvIP = ViewUtils.findViewById(convertView, R.id.tv_ip);
                tvPt = ViewUtils.findViewById(convertView, R.id.tv_pt);
                tvTime = ViewUtils.findViewById(convertView, R.id.tv_time);
            }
        }
    }
}
