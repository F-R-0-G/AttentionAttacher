package com.frog.attentionattacher.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.frog.attentionattacher.LoginActivity;
import com.frog.attentionattacher.R;
import com.frog.attentionattacher.bean.ToDoListBean;
import com.frog.attentionattacher.utils.ToastUtil;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

public class CollocationListAdapter extends BaseExpandableListAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<ToDoListBean> data;

    private final int SCHEDULE = 0;
    private final int CALENDAR = 1;

    public CollocationListAdapter(Context context, ExpandableListView elv_collocation, List<ToDoListBean> data) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return data.get(groupPosition).getCollocationToDoList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return data.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data.get(groupPosition).getCollocationToDoList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;//如果子条目需要响应click事件,必需返回true
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentViewHolder parentViewHolder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.collocation_list_item_parent, parent, false);
            parentViewHolder = new ParentViewHolder(convertView);
            convertView.setTag(parentViewHolder);
            AutoUtils.auto(convertView);
        } else {
            parentViewHolder = (ParentViewHolder) convertView.getTag();
        }
        ToDoListBean toDoListBean = data.get(groupPosition);
        parentViewHolder.tv_collocation_name.setText(toDoListBean.getName());
        parentViewHolder.parent_icon.setImageResource(toDoListBean.getIconName());
        parentViewHolder.iv_status.setImageResource(isExpanded ? R.mipmap.icon_top : R.mipmap.icon_bottom);
        parentViewHolder.hsv_goods_list.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
        parentViewHolder.hsv_goods_list.setFocusable(false);//设置后解决无法正常展开的bug

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.collocation_list_item_child, parent, false);
            childViewHolder = new ChildViewHolder(convertView);
            convertView.setTag(childViewHolder);
            AutoUtils.auto(convertView);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        final ToDoListBean.ToDoList toDoList = data.get(groupPosition).getCollocationToDoList().get(childPosition);
        childViewHolder.tv_goods_title.setText(toDoList.getContent());
        childViewHolder.ll_root_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupPosition == SCHEDULE) {
                    context.startActivity(new Intent(context, LoginActivity.class));
                    Activity toSchedule = (Activity) context;
                    toSchedule.finish();
                    //待办内点击，跳转到日程管理
                } else if (groupPosition == CALENDAR) {
                    ToastUtil.showToast(context, "点了也不能转运，可理解", Toast.LENGTH_SHORT);
                    //点击老黄历内部
                }
                //修改操作
            }
        });
        if (childPosition == data.get(groupPosition).getCollocationToDoList().size() - 1) {
            childViewHolder.ll_bottom.setVisibility(View.VISIBLE);
            if (groupPosition == SCHEDULE) {
                childViewHolder.tv_add_cart.setText("开始工作");
            } else if (groupPosition == CALENDAR) {
                childViewHolder.tv_add_cart.setText("查看详情");
            }
            childViewHolder.tv_add_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (groupPosition == SCHEDULE) {
                        ToastUtil.showToast(context, "schedule details", Toast.LENGTH_SHORT);
                        //待办详情
                    } else if (groupPosition == CALENDAR) {
                        ToastUtil.showToast(context, "almanac details", Toast.LENGTH_SHORT);
                        //老黄历详情
                    }
                }
            });
        } else {
            childViewHolder.ll_bottom.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ParentViewHolder {
        private ImageView parent_icon;
        private ImageView iv_status;
        private HorizontalScrollView hsv_goods_list;
        private TextView tv_collocation_name;

        private ParentViewHolder(View view) {
            parent_icon = (ImageView) view.findViewById(R.id.parent_icon);
            iv_status = (ImageView) view.findViewById(R.id.iv_status);
            hsv_goods_list = (HorizontalScrollView) view.findViewById(R.id.hsv_goods_list);
            tv_collocation_name = (TextView) view.findViewById(R.id.tv_collocation_name);
        }
    }

    private class ChildViewHolder {
        private LinearLayout ll_bottom, ll_root_view;
        private TextView tv_add_cart, tv_goods_title;

        private ChildViewHolder(View view) {
            ll_bottom = (LinearLayout) view.findViewById(R.id.ll_bottom);
            ll_root_view = (LinearLayout) view.findViewById(R.id.ll_root_view);
            tv_add_cart = (TextView) view.findViewById(R.id.tv_add_cart);
            tv_goods_title = (TextView) view.findViewById(R.id.tv_goods_title);
        }
    }

}
