package com.fynn.oyseckill.app.module.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fynn.oyseckill.R;
import com.fynn.oyseckill.app.core.BaseFragment;
import com.fynn.oyseckill.app.module.home.card.HomeProductView;
import com.fynn.oyseckill.app.module.home.card.PagerCard;
import com.fynn.oyseckill.app.module.home.util.ComparatorType;
import com.fynn.oyseckill.app.module.home.util.ProductComparator;
import com.fynn.oyseckill.model.entity.Banner;
import com.fynn.oyseckill.model.entity.Product;
import com.fynn.oyseckill.widget.StateScrollView;
import com.fynn.oyseckill.widget.Switcher;
import com.fynn.oyseckill.widget.Titlebar;

import org.appu.common.utils.DensityUtils;
import org.appu.common.utils.LogU;

import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by fynn on 16/4/24.
 */
public class HomeFragment extends BaseFragment {

    private static final int LIMIT = 10;
    private Titlebar titlebar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TabLayout tlTab;
    private Switcher switcher;
    private StateScrollView scrollView;
    private HomeProductView hpvProduct;
    private PagerCard pagerCard;
    private TextView tvMoreDesc;
    private int skip = 0;
    private ComparatorType orderType = ComparatorType.POPULARITY;

    private boolean isLoading;
    private boolean isFinish;

    @Override
    public int getContentResId() {
        return R.layout.fragment_home;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        titlebar = $(R.id.titlebar);
        tlTab = $(R.id.tl_tab);
        swipeRefreshLayout = $(R.id.swipe_refresh);
        scrollView = $(R.id.scroll_view);
        switcher = (Switcher) tlTab.getTabAt(3).getCustomView().findViewById(R.id.switcher);
        hpvProduct = $(R.id.hpv_product);
        pagerCard = $(R.id.pager_card);
        tvMoreDesc = $(R.id.tv_more);
    }

    @Override
    public void initActions(Bundle savedInstanceState) {
        switcher.setEnabled(false);

        pagerCard.getLayoutParams().height = (int) (DensityUtils.getScreenWidth() * 0.4) + 1;

        tlTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        orderType = ComparatorType.POPULARITY;
                        break;

                    case 1:
                        orderType = ComparatorType.PROGRESS;
                        break;

                    case 2:
                        orderType = ComparatorType.RECENT;
                        break;

                    case 3:
                        switcher.setEnabled(true);
                        switcher.setChecked(true);
                        orderType = ComparatorType.PERSON_TIMES;
                        break;

                    default:
                        orderType = ComparatorType.POPULARITY;
                        break;
                }

                refresh(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 3) {
                    switcher.setEnabled(false);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 3) {
                    switcher.toggle();
                    orderType = switcher.isChecked() ?
                            ComparatorType.PERSON_TIMES : ComparatorType.PERSON_TIMES_SEQUENCE;
                }
                order();
            }
        });

        scrollView.setOnScrollListener(new StateScrollView.OnScrollListener() {
            @Override
            public void onScrollChanged(ScrollView scrollView, int x, int y, int oldX, int oldY) {
                boolean gotoBottom = scrollView.getChildAt(0).getMeasuredHeight() <=
                        scrollView.getScrollY() + scrollView.getHeight();

                if (gotoBottom && !isLoading && !isFinish) {
                    fetchData();
                }
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isLoading) {
                    return;
                }
                refresh();
            }
        });

        hpvProduct.getRefreshButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh(true);
            }
        });

        refresh(true);
    }

    @Override
    public void onResume() {
        if (isVisible()) {
//            refresh();
            pagerCard.getRollPagerView().resume();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (!isVisible()) {
            pagerCard.getRollPagerView().pause();
        }
        super.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (isVisible()) {
//            refresh();
            pagerCard.getRollPagerView().resume();
        } else {
            pagerCard.getRollPagerView().pause();
        }
    }

    private void refresh() {
        refresh(false);
    }

    private void refresh(boolean showLoading) {
        skip = 0;
        isFinish = false;
        fetchData(showLoading);
        fetchBanner();
    }

    private void fetchBanner() {
        BmobQuery<Banner> query = new BmobQuery<>();
        query.order("-updatedAt");
        query.include("product,product.currentIssue");
        query.findObjects(activity, new FindListener<Banner>() {
            @Override
            public void onSuccess(List<Banner> list) {
                if (isVisible()) {
                    pagerCard.setVisibility(View.VISIBLE);
                    pagerCard.setPagerData(list);
                }

                if (list == null || list.isEmpty()) {
                    pagerCard.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onError(int i, String s) {
                LogU.e("fetchBanner", "code:" + 1, "msg:" + s);
            }
        });
    }

    private void order() {
        Collections.sort(
                hpvProduct.getProducts(),
                ProductComparator.getComparator(orderType));
        hpvProduct.notifyDataSetChanged();
    }

    private void fetchData() {
        fetchData(false);
    }

    private void fetchData(boolean showLoading) {
        if (isLoading) {
            return;
        }
        if (showLoading) {
            showProgress();
        }
        isLoading = true;

        BmobQuery<Product> query = new BmobQuery<>();
        query.setLimit(LIMIT);
        query.setSkip(skip * LIMIT);
        query.addWhereEqualTo("canBuy", true);
        query.include("currentIssue");
        query.order("-price");
        query.findObjects(getContext(), new FindListener<Product>() {
            @Override
            public void onSuccess(List<Product> list) {
                hideProgress();
                isLoading = false;
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                if (skip == 0) {
                    hpvProduct.getProducts().clear();
                }

                if (list != null && !list.isEmpty()) {
                    if (!isVisible()) {
                        return;
                    }

                    hpvProduct.getProducts().addAll(list);
                    if (skip == 0) {
                        Collections.sort(
                                hpvProduct.getProducts(),
                                ProductComparator.getComparator(orderType));
                    }
                    skip++;
                    if (list.size() < LIMIT) {
                        isFinish = true;
                    }

                } else {
                    isFinish = true;
                }

                if (hpvProduct.getProducts().size() > 0) {
                    showMoreDesc(true);
                } else {
                    showMoreDesc(false);
                    hpvProduct.setEmpty();
                }

                hpvProduct.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                isLoading = false;
                hideProgress();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

                switch (i) {
                    case 9010:
                    case 9016:
                        showShortToast("网络不给力");
                        hpvProduct.setEmpty();
                        break;

                    default:
                        break;
                }

                LogU.e("商品数据获取失败", "code:" + i, "msg:" + s);
            }
        });
    }

    private void showMoreDesc(boolean isShow) {
        if (isShow) {
            tvMoreDesc.setVisibility(View.VISIBLE);
        } else {
            tvMoreDesc.setVisibility(View.GONE);
        }

        if (isFinish) {
            tvMoreDesc.setText("无更多商品");
        } else {
            tvMoreDesc.setText("上拉加载更多");
        }
    }
}
