/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cn.rainss.smartNote.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import cn.rainss.smartNote.R;
import cn.rainss.smartNote.adapter.entity.NewInfo;
import cn.rainss.smartNote.utils.XToastUtils;

import com.xuexiang.xui.adapter.recyclerview.BaseRecyclerAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.adapter.recyclerview.XRecyclerAdapter;
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem;
import com.xuexiang.xui.widget.banner.widget.banner.SimpleImageBanner;
import com.xuexiang.xui.widget.banner.widget.banner.base.BaseBanner;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页动态新闻【只是用于演示效果】
 */
public class NewsCardViewListAdapter extends BaseRecyclerAdapter<NewInfo> {

    private static final int TYPE_BANNER_HEAD = 0;
    private static final int TYPE_COMMON = 0;

    private List<BannerItem> mData;

    /**
     * 适配的布局
     *
     * @param viewType
     * @return
     */
    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.adapter_news_card_view_list_item;
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_COMMON;
    }

    public XRecyclerAdapter refresh(List<NewInfo> data) {
        List<NewInfo> list = new ArrayList<>(data);
        return super.refresh(list);
    }

    @Override
    public void bindData(@NonNull RecyclerViewHolder holder, int position, NewInfo model) {
        if (model == null) {
            return;
        }
        holder.text(R.id.tv_user_name, model.getUserName());
        holder.text(R.id.tv_tag, model.getTag());
        holder.text(R.id.tv_title, model.getTitle());
        holder.text(R.id.tv_summary, model.getSummary());
        holder.text(R.id.tv_praise, model.getPraise() == 0 ? "点赞" : String.valueOf(model.getPraise()));
        holder.text(R.id.tv_comment, model.getComment() == 0 ? "评论" : String.valueOf(model.getComment()));
        holder.text(R.id.tv_read, "阅读量 " + model.getRead());
        holder.image(R.id.iv_image, model.getImageUrl());
    }

}
