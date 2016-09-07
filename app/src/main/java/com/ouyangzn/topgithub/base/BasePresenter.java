/*
 * Copyright (c) 2016.  ouyangzn   <ouyangzn@163.com>
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

package com.ouyangzn.topgithub.base;

import java.util.ArrayList;
import java.util.List;
import rx.Subscription;

/**
 * Created by ouyangzn on 2016/7/1.<br/>
 * Description：
 */
public abstract class BasePresenter<T> {

  protected T mView;
  private List<Subscription> mSubscriptionList = new ArrayList<>(2);

  public void onAttach(T view) {
    this.mView = view;
  }

  public void onDetach() {
    this.mView = null;
    int size = mSubscriptionList.size();
    if (size > 0) {
      Subscription subscription;
      for (int i = 0; i < size; i++) {
        subscription = mSubscriptionList.get(i);
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
      }
    }
    this.mSubscriptionList.clear();
    this.mSubscriptionList = null;
    this.onDestroy();
  }

  /**
   * 对应的view销毁时调用的清理资源方法
   */
  protected abstract void onDestroy();

  protected void addSubscription(Subscription subscription) {
    this.mSubscriptionList.add(subscription);
  }
}