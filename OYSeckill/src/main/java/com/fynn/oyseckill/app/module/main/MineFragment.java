package com.fynn.oyseckill.app.module.main;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseFragment;
import com.fynn.oyseckill.app.module.account.config.ServiceActivity;
import com.fynn.oyseckill.app.module.account.config.SettingsActivity;
import com.fynn.oyseckill.app.module.account.lgrg.LoginActivity;
import com.fynn.oyseckill.app.module.account.pay.RechargeActivity;
import com.fynn.oyseckill.app.module.account.redpkg.RedEnvelopeActivity;
import com.fynn.oyseckill.app.module.account.user.AccountDetailActivity;
import com.fynn.oyseckill.app.module.account.user.LuckRecordActivity;
import com.fynn.oyseckill.app.module.account.user.MySeckillActivity;
import com.fynn.oyseckill.app.module.account.user.OrderShareActivity;
import com.fynn.oyseckill.app.module.account.user.UserInfoActivity;
import com.fynn.oyseckill.model.entity.Asset;
import com.fynn.oyseckill.model.entity.OysUser;
import com.fynn.oyseckill.util.DisplayImageUtils;
import com.fynn.oyseckill.util.UserHelper;
import com.fynn.oyseckill.util.constants.Event;
import com.fynn.oyseckill.widget.CircleImageView;

import org.appu.common.ParamMap;
import org.appu.common.utils.TextUtils;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by fynn on 16/4/24.
 */
public class MineFragment extends BaseFragment {

    private LinearLayout llUser;
    private LinearLayout llSettings;
    private LinearLayout llRedPackage;
    private LinearLayout llShare;
    private LinearLayout llMySeckill;
    private LinearLayout llLuck;
    private LinearLayout llAccountDetail;
    private LinearLayout llService;

    private TextView tvNick;
    private CircleImageView ivProfile;
    private TextView tvCoin;
    private RelativeLayout rlCharge;
    private TextView tvRecharge;

    @Override
    public int getContentResId() {
        return R.layout.fragment_mine;
    }

    @Override
    public int getTitlebarResId() {
        return R.id.titlebar;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        llUser = $(R.id.ll_user);
        llSettings = $(R.id.ll_settings);
        tvNick = $(R.id.tv_nick);
        llRedPackage = $(R.id.ll_red_package);
        ivProfile = $(R.id.iv_profile);
        tvCoin = $(R.id.tv_coin);
        rlCharge = $(R.id.rl_charge);
        tvRecharge = $(R.id.tv_recharge);
        llShare = $(R.id.ll_share);
        llMySeckill = $(R.id.ll_my_seckill);
        llLuck = $(R.id.ll_luck);
        llAccountDetail = $(R.id.ll_account_detail);
        llService = $(R.id.ll_service);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        setOnClick(llUser, llSettings, llRedPackage, tvRecharge, llShare, llMySeckill, llLuck
                , llAccountDetail, llService);
        register(Event.EVENT_UPDATE_PROFILE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_user:
                if (UserHelper.isLogin()) {
                    startActivity(UserInfoActivity.class);
                } else {
                    startActivity(LoginActivity.class);
                }
                break;

            case R.id.ll_settings:
                startActivityForLogin(SettingsActivity.class);
                break;

            case R.id.ll_red_package:
                startActivityForLogin(RedEnvelopeActivity.class);
                break;

            case R.id.tv_recharge:
                startActivityForLogin(RechargeActivity.class);
                break;

            case R.id.ll_share:
                if (UserHelper.isLogin()) {
                    ParamMap<String, Object> params = new ParamMap<>();
                    params.put("showTitlebar", false);
                    params.put("userObjId", UserHelper.getObjectId());
                    startActivity(OrderShareActivity.class, params);
                } else {
                    startActivity(LoginActivity.class);
                }
                break;

            case R.id.ll_my_seckill:
                startActivityForLogin(MySeckillActivity.class);
                break;

            case R.id.ll_luck:
                startActivityForLogin(LuckRecordActivity.class);
                break;

            case R.id.ll_account_detail:
                startActivityForLogin(AccountDetailActivity.class);
                break;

            case R.id.ll_service:
                startActivity(ServiceActivity.class);
                break;

            default:
                break;
        }
    }

    @Override
    public void onResume() {
        if (isVisible()) {
            update();
        }
        super.onResume();
    }

    @Override
    public void onEvent(String action, Bundle data) {
        if (action.equals(Event.EVENT_UPDATE_PROFILE)) {
            DisplayImageUtils.displayProfile(ivProfile);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!isHidden()) {
            update();
        }
    }

    private void update() {
        if (UserHelper.isLogin()) {
            rlCharge.setVisibility(View.VISIBLE);
            tvCoin.setVisibility(View.VISIBLE);

            OysUser user = BmobUser.getCurrentUser(getContext(), OysUser.class);
            Long userId = user.getUserId();
            String nick = user.getNickname();

            if (!TextUtils.isEmpty(nick)) {
                tvNick.setText(nick);
            } else {
                tvNick.setText("oys" + userId);
            }

            queryOysCoin();

        } else {
            rlCharge.setVisibility(View.GONE);
            tvCoin.setVisibility(View.GONE);
            ivProfile.setImageResource(R.drawable.icon_user_profile_normal);
            tvNick.setText("登录");
            titlebar.setRightAction("", null);
        }

        DisplayImageUtils.displayProfile(ivProfile);
    }

    private void queryOysCoin() {
        BmobQuery<Asset> query = new BmobQuery<Asset>();
        query.addWhereEqualTo("user", new BmobPointer(UserHelper.getUser()));
        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.setMaxCacheAge(TimeUnit.SECONDS.toMillis(15)); //15s
        query.findObjects(getActivity(), new FindListener<Asset>() {
            @Override
            public void onSuccess(List<Asset> list) {
                if (!isVisible()) {
                    return;
                }
                if (list != null && !list.isEmpty()) {
                    Double coin = list.get(0).getOysCoin();
                    String coinStr = new DecimalFormat("#.##").format(coin);
                    tvCoin.setText(String.format("%s 秒币", coinStr));
                } else {
                    tvCoin.setText("0 秒币");
                }
            }

            @Override
            public void onError(int i, String s) {
                switch (i) {
                    case 9009:
                        return;
                }
                tvCoin.setText("0 秒币");
            }
        });
    }
}
