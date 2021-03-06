package cn.shineiot.navigation.navigationfragment;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.maning.mndialoglibrary.MProgressDialog;

import java.util.List;

import butterknife.BindView;
import cn.shineiot.base.module.BaseMvpFragment;
import cn.shineiot.base.utils.LogUtil;
import cn.shineiot.base.utils.NetworkUtils;
import cn.shineiot.base.utils.ToastUtils;
import cn.shineiot.navigation.R;
import cn.shineiot.navigation.R2;
import cn.shineiot.navigation.bean.Navigation;

/**
 * @author GF63
 */
@Route(path = "/navigation/navigationFragment")
public class NavigationFragment extends BaseMvpFragment<NavigationView, NavigationPresenter> implements NavigationView {
	@BindView(R2.id.recyclerViewTab)
	RecyclerView recyclerViewTab;
	@BindView(R2.id.recyclerViewContent)
	RecyclerView recyclerViewContent;

	private NavigationTabAdapter tabAdapter;
	private NavigationContentAdapter contentAdapter;
	private int currentPosition = 0;
	private LinearLayoutManager linearLayoutManagerTab;
	private LinearLayoutManager linearLayoutManagerContent;

	private boolean isUp = false;//上滑
	private boolean isDown = false;//下滑
	private int firstCompletelyVisibleItemPosition = 0;//第一个完全显示的item
	private int lastCompletelyVisibleItemPosition = 0;//最后一个完全显示的item
	private boolean isConnect =false;


	@Override
	public int getLayoutId() {
		return R.layout.fragment_navigation;
	}

	@Override
	public void initViews(View view) {
		recyclerViewTab.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
		recyclerViewTab.setItemAnimator(new DefaultItemAnimator());
		linearLayoutManagerTab = new LinearLayoutManager(mContext);
		recyclerViewTab.setLayoutManager(linearLayoutManagerTab);
		tabAdapter = new NavigationTabAdapter(R.layout.item_navigation_tab);
		recyclerViewTab.setAdapter(tabAdapter);

		recyclerViewContent.setItemAnimator(new DefaultItemAnimator());
		linearLayoutManagerContent = new LinearLayoutManager(mContext);
		recyclerViewContent.setLayoutManager(linearLayoutManagerContent);
		contentAdapter = new NavigationContentAdapter(R.layout.item_navigation_content);
		recyclerViewContent.setAdapter(contentAdapter);

		addOnItemClickListener();
		presenter.getNavigationData();

		recyclerViewTab.setHasFixedSize(true);
		recyclerViewContent.setHasFixedSize(true);

		isConnect = NetworkUtils.isConnected();
	}

	private void addOnItemClickListener() {
		tabAdapter.setOnItemClickListener((adapter, view, position) -> {
			if (currentPosition != position) {
				tabAdapter.setPosition(position);
				tabAdapter.notifyDataSetChanged();
				currentPosition = position;

				linearLayoutManagerContent.scrollToPositionWithOffset(position, 0);//滚动到指定position并置顶
				linearLayoutManagerContent.setStackFromEnd(false);
			}
		});

		recyclerViewContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if(0 != newState){
					return;
				}

				int firstVisibleItemPosition = linearLayoutManagerContent.findFirstVisibleItemPosition();
				int lastVisibleItemPosition = linearLayoutManagerContent.findLastVisibleItemPosition();


				firstCompletelyVisibleItemPosition = linearLayoutManagerContent.findFirstCompletelyVisibleItemPosition();
				lastCompletelyVisibleItemPosition = linearLayoutManagerContent.findLastCompletelyVisibleItemPosition();

				LogUtil.e("-------first/" + firstCompletelyVisibleItemPosition + "=====//" + firstVisibleItemPosition + "-------" + newState);
				LogUtil.e("-------last/" + lastCompletelyVisibleItemPosition + "=====//" + lastVisibleItemPosition + "-------" + newState);

				if (isUp) {
					int position ;
					 if (currentPosition != firstCompletelyVisibleItemPosition) {
						 if (lastVisibleItemPosition == contentAdapter.getData().size()-1) {
							 position = lastVisibleItemPosition;
						 }else if (firstCompletelyVisibleItemPosition > 0) {
							position = firstCompletelyVisibleItemPosition;
						} else {
							position = firstVisibleItemPosition;
						}
						 tabAdapter.setPosition(position);
						 tabAdapter.notifyDataSetChanged();
						 currentPosition = position;
						 linearLayoutManagerTab.scrollToPositionWithOffset(position,500);//据顶部的距离
						 linearLayoutManagerContent.setStackFromEnd(false);
					}
				} else if (isDown) {
					if (currentPosition != firstCompletelyVisibleItemPosition) {
						int position;
						if (firstCompletelyVisibleItemPosition > 0) {
							position = firstCompletelyVisibleItemPosition;
						} else {
							position = firstVisibleItemPosition;
						}
						tabAdapter.setPosition(position);
						tabAdapter.notifyDataSetChanged();
						currentPosition = position;
						linearLayoutManagerTab.scrollToPositionWithOffset(position,500);
						linearLayoutManagerContent.setStackFromEnd(false);
					}
				}
			}

			@Override
			public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				if (dy > 0) {
					isUp = true;
					isDown = false;
				} else if (dy < 0) {
					isDown = true;
					isUp = false;
				}
			}
		});
	}

	@Override
	public NavigationPresenter initPresenter() {
		return new NavigationPresenter();
	}

	@Override
	public void successData(List<Navigation> list) {
		hideLoading();
		tabAdapter.setNewData(list);
		contentAdapter.setNewData(list);
		tabAdapter.setPosition(0);
		tabAdapter.notifyItemChanged(0);

	}

	@Override
	public void showLoading(String msg) {
		MProgressDialog.showProgress(mContext);
	}

	@Override
	public void hideLoading() {
		recyclerViewTab.postDelayed(MProgressDialog::dismissProgress,1000);
	}

	@Override
	public void showError(String msg) {
		hideLoading();
		ToastUtils.showErrorToast(msg);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden && !isConnect) {
			if(NetworkUtils.isConnected()){
				isConnect = true;
				showLoading("");
				presenter.getNavigationData();
			}else{
				ToastUtils.showErrorToast("请检查网络");
			}
		}
	}
}
