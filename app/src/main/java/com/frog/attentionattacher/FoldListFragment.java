package com.frog.attentionattacher;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.frog.attentionattacher.adapter.CollocationListAdapter;
import com.frog.attentionattacher.bean.ToDoListBean;

import java.util.ArrayList;
import java.util.List;

public class FoldListFragment extends Fragment {

    private ExpandableListView elv_collocation1;
    private List<ToDoListBean> collocationList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fold_list_view, container, false);
        elv_collocation1 = (ExpandableListView) view.findViewById(R.id.elv_collocation1);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListViewData();
    }

    private void initListViewData() {
        collocationList = new ArrayList<>();
        ToDoListBean collocation_1 = new ToDoListBean();
        ToDoListBean collocation_2 = new ToDoListBean();

        List<ToDoListBean.ToDoList> goodsList_1 = new ArrayList<>();
        int count = goodsList_1.size();
        if (count == 0) {
            goodsList_1.add(new ToDoListBean.ToDoList("这里空空如也……"));
        }
        collocation_1.setCollocationToDoList(goodsList_1);
        collocation_1.setName("待办 · " + count);
        collocation_1.setIconName(R.drawable.nav_schedule);

        List<ToDoListBean.ToDoList> goodsList_2 = new ArrayList<>();
        goodsList_2.add(new ToDoListBean.ToDoList("宜：重构 洗澡 开会"));
        goodsList_2.add(new ToDoListBean.ToDoList("不宜：金陵小炒"));
        goodsList_2.add(new ToDoListBean.ToDoList("座位朝向：西"));
        collocation_2.setCollocationToDoList(goodsList_2);
        collocation_2.setName("程序员老黄历");
        collocation_2.setIconName(R.drawable.ic_home);

        collocationList.add(collocation_1);
        collocationList.add(collocation_2);
        elv_collocation1.setAdapter(new CollocationListAdapter(getActivity(), elv_collocation1, collocationList));
        elv_collocation1.expandGroup(0);
        elv_collocation1.expandGroup(1);//展开
    }
}
