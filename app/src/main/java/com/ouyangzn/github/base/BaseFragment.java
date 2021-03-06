/*
 * Copyright (c) 2016.  ouyangzn   <email : ouyangzn@163.com>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ouyangzn.github.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.ouyangzn.github.R;
import com.ouyangzn.github.utils.Log;

public abstract class BaseFragment<V, T extends BasePresenter<V>> extends Fragment {

  protected String TAG = "BaseFragment";
  protected T mPresenter;
  protected LayoutInflater mInflater;
  protected View mContentView;
  protected View mLoadingView;
  protected View mErrorView;
  private ViewGroup mRootView;
  private com.ouyangzn.github.utils.Toast mToast;
  private Status mStatus;
  private boolean isVisible;
  private Unbinder mUnbinder;

  @Override public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    isVisible = isVisibleToUser;
    Log.d(TAG, "-----------isVisibleToUser = " + isVisible + " ,class = " + getArguments().get(
        "language"));
  }

  @Override public final void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    TAG = this.getClass().getSimpleName();
    mToast = com.ouyangzn.github.utils.Toast.getInstance(getContext());
    mPresenter = initPresenter();
    initData(savedInstanceState);
  }

  @Nullable @Override
  public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mInflater = inflater;
    mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_base_content, container, false);
    mLoadingView = mRootView.findViewById(R.id.stub_loading);
    mErrorView = mRootView.findViewById(R.id.stub_error);
    mContentView = inflater.inflate(getContentView(), container, false);
    if (mContentView == null) throw new UnsupportedOperationException("contentView == null");
    mRootView.addView(mContentView);
    if (mStatus == null) {
      switchStatus(getCurrentStatus());
    } else {
      switchStatus(mStatus);
    }
    mUnbinder = ButterKnife.bind(this, mContentView);
    return mRootView;
  }

  @Override public final void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mPresenter.onAttach((V) this);
    initView(mContentView);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    if (mPresenter != null) mPresenter.onDetach();
    mUnbinder.unbind();
  }

  protected void setLoadingView(View loadingView) {
    loadingView.setVisibility(mLoadingView.getVisibility());
    mRootView.removeView(mLoadingView);
    mRootView.addView(loadingView);
    mLoadingView = loadingView;
  }

  protected void setErrorView(View errorView) {
    errorView.setVisibility(mErrorView.getVisibility());
    mRootView.removeView(mErrorView);
    mRootView.addView(errorView);
    mErrorView = errorView;
  }

  /**
   * 切换当前fragment状态
   *
   * @param status 当前状态
   * @see Status
   */
  protected void switchStatus(Status status) {
    mStatus = status;
    switch (status) {
      case STATUS_NORMAL:
        hideAllView();
        mContentView.setVisibility(View.VISIBLE);
        break;
      case STATUS_LOADING:
        hideAllView();
        mLoadingView.setVisibility(View.VISIBLE);
        break;
      case STATUS_ERROR:
        hideAllView();
        mErrorView.setVisibility(View.VISIBLE);
        break;
    }
  }

  protected void toast(String content) {
    mToast.show(content, Toast.LENGTH_SHORT);
  }

  protected void toast(int resId) {
    mToast.show(resId, Toast.LENGTH_SHORT);
  }

  /**
   * 当前状态是加载中还是其他的···,会根据状态显示对应的view
   *
   * @return 状态, 如果直接显示content，状态为{@link Status#STATUS_NORMAL}
   * @see Status
   */
  protected abstract Status getCurrentStatus();

  protected abstract int getContentView();

  /**
   * 初始化一些数据,此时view还未创建完，
   * 如果是拿到数据马上显示的操作，应放到{@link #initView(View)}中
   *
   * @param savedInstanceState
   */
  protected abstract void initData(Bundle savedInstanceState);

  protected abstract void initView(View parent);

  public abstract T initPresenter();

  private void hideAllView() {
    int childCount = mRootView.getChildCount();
    for (int i = 0; i < childCount; i++) {
      mRootView.getChildAt(i).setVisibility(View.GONE);
    }
  }

  /**
   * fragment的数据等加载状态
   */
  public enum Status {
    STATUS_NORMAL, STATUS_LOADING, STATUS_ERROR
  }
}
