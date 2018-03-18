package com.example.hello.gouwuche.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hello.gouwuche.R;
import com.example.hello.gouwuche.bean.CartBean;
import com.example.hello.gouwuche.bean.PriceNumber;
import com.example.hello.gouwuche.persenter.MyPersenter;
import com.example.hello.gouwuche.utils.OkHttp3Util;
import com.example.hello.gouwuche.view.MainActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by dell on 2018/3/9.
 */

public class MyExpandAdapter extends BaseExpandableListAdapter {
    MainActivity mainActivity;
    Handler handler;
    MyPersenter persenter;
    List<CartBean.DataBean> data;
    Context context;
    ProgressBar progressbar;
    private int allIndex;
    private int childIndex;

    public MyExpandAdapter(List<CartBean.DataBean> data, Context context, MyPersenter persenter, ProgressBar progressbar, Handler handler, MainActivity mainActivity) {
        this.data = data;
        this.context = context;
        this.persenter = persenter;
        this.progressbar = progressbar;
        this.handler = handler;
        this.mainActivity = mainActivity;
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return data.get(i).getList().size();
    }

    @Override
    public Object getGroup(int i) {
        return data.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return data.get(i).getList().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        final CartBean.DataBean dataBean = data.get(i);
        final GroupViewHolder vh;
        if (view == null) {
            vh = new GroupViewHolder();
            view = View.inflate(context, R.layout.expand_group, null);
            vh.checkBox = view.findViewById(R.id.group_ck);
            ;
            vh.textView = view.findViewById(R.id.group_title);
            ;
            view.setTag(vh);
        } else {
            vh = (GroupViewHolder) view.getTag();
        }
        vh.checkBox.setChecked(dataBean.isChecked());
        vh.textView.setText(dataBean.getSellerName());
        vh.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressbar.setVisibility(View.VISIBLE);
                //https://www.zhaoapi.cn/product/updateCarts?uid=71&sellerid=1&pid=1&selected=0&num=10
                childIndex = 0;
                updateChildChecked(vh.checkBox.isChecked(), dataBean);
            }
        });
        return view;
    }

    private void updateChildChecked(final boolean ck, final CartBean.DataBean dataBean) {
        CartBean.DataBean.ListBean listBean = dataBean.getList().get(childIndex);
        Map<String, String> map = new HashMap<>();
        map.put("uid", "71");
        map.put("sellerid", String.valueOf(listBean.getSellerid()));
        map.put("pid", String.valueOf(listBean.getPid()));
        map.put("selected", String.valueOf(ck ? 1 : 0));
        map.put("num", String.valueOf(listBean.getNum()));
        OkHttp3Util.doPost("https://www.zhaoapi.cn/product/updateCarts", map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    childIndex++;
                    if (childIndex < dataBean.getList().size()) {
                        updateChildChecked(ck, dataBean);
                    } else {
                        Map<String, String> map1 = new HashMap<String, String>();
                        map1.put("uid", "71");
                        map1.put("source", "android");
                        persenter.getCartData("https://www.zhaoapi.cn/product/getCarts", map1);
                    }
                }
            }
        });
    }


    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final CartBean.DataBean.ListBean listBean = data.get(i).getList().get(i1);
        final ChildViewHolder vh;
        if (view == null) {
            vh = new ChildViewHolder();
            view = View.inflate(context, R.layout.expand_child, null);
            vh.checkBox = view.findViewById(R.id.child_ck);
            vh.simpleDraweeView = view.findViewById(R.id.child_sdv);
            vh.description = view.findViewById(R.id.child_description);
            ;
            vh.del = view.findViewById(R.id.child_delete);
            ;
            vh.price = view.findViewById(R.id.child_price);
            ;
            vh.jian = view.findViewById(R.id.jian);
            ;
            vh.num = view.findViewById(R.id.num);
            ;
            vh.jia = view.findViewById(R.id.jia);
            ;
            view.setTag(vh);
        } else {
            vh = (ChildViewHolder) view.getTag();
        }
        vh.checkBox.setChecked(listBean.getSelected() == 1 ? true : false);
        Uri uri = Uri.parse(listBean.getImages().split("\\|")[0]);
        vh.simpleDraweeView.setImageURI(uri);
        vh.description.setText(listBean.getTitle());
        vh.price.setText(listBean.getBargainPrice() + "");
        vh.num.setText(listBean.getNum() + "");
        vh.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressbar.setVisibility(View.VISIBLE);
                Map<String, String> map = new HashMap<>();
                map.put("uid", "71");
                map.put("sellerid", String.valueOf(listBean.getSellerid()));
                map.put("pid", String.valueOf(listBean.getPid()));
                map.put("selected", String.valueOf(listBean.getSelected() == 1 ? 0 : 1));
                map.put("num", String.valueOf(listBean.getNum()));
                OkHttp3Util.doPost("https://www.zhaoapi.cn/product/updateCarts", map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Map<String, String> map1 = new HashMap<String, String>();
                            map1.put("uid", "71");
                            map1.put("source", "android");
                            persenter.getCartData("https://www.zhaoapi.cn/product/getCarts", map1);
                        }
                    }
                });
            }
        });
        vh.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressbar.setVisibility(View.VISIBLE);
                //https://www.zhaoapi.cn/product/deleteCart?uid=72&pid=1
                Map<String, String> map = new HashMap<>();
                map.put("uid","71");
                map.put("pid", String.valueOf(listBean.getPid()));
                OkHttp3Util.doPost("https://www.zhaoapi.cn/product/deleteCart", map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()){
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context,"删除成功",Toast.LENGTH_SHORT).show();
                                }
                            });
                            Map<String, String> map1 = new HashMap<>();
                            map1.put("uid", "71");
                            map1.put("source", "android");
                            persenter.getCartData("https://www.zhaoapi.cn/product/getCarts", map1);
                        }
                    }
                });
            }
        });

        vh.jian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = listBean.getNum();
                if(num == 1){
                    return;
                }
                progressbar.setVisibility(View.VISIBLE);
                Map<String, String> map = new HashMap<>();
                map.put("uid", "71");
                map.put("sellerid", String.valueOf(listBean.getSellerid()));
                map.put("pid", String.valueOf(listBean.getPid()));
                map.put("selected", String.valueOf(listBean.getSelected()));
                map.put("num", String.valueOf(listBean.getNum()-1));
                OkHttp3Util.doPost("https://www.zhaoapi.cn/product/updateCarts", map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()){
                            Map<String, String> map1 = new HashMap<>();
                            map1.put("uid", "71");
                            map1.put("source", "android");
                            persenter.getCartData("https://www.zhaoapi.cn/product/getCarts", map1);
                        }
                    }
                });
            }
        });
        vh.jia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressbar.setVisibility(View.VISIBLE);
                Map<String, String> map = new HashMap<>();
                map.put("uid", "71");
                map.put("sellerid", String.valueOf(listBean.getSellerid()));
                map.put("pid", String.valueOf(listBean.getPid()));
                map.put("selected", String.valueOf(listBean.getSelected()));
                map.put("num", String.valueOf(listBean.getNum()+1));
                OkHttp3Util.doPost("https://www.zhaoapi.cn/product/updateCarts", map, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()){
                            Map<String, String> map1 = new HashMap<>();
                            map1.put("uid", "71");
                            map1.put("source", "android");
                            persenter.getCartData("https://www.zhaoapi.cn/product/getCarts", map1);
                        }
                    }
                });
            }
        });
        return view;
    }


    public void sendPriveNum(){
        int p = 0;
        int n = 0;
        for(int i = 0;i<data.size();i++){
            List<CartBean.DataBean.ListBean> list = data.get(i).getList();
            for(int l = 0;l<list.size();l++){
                CartBean.DataBean.ListBean listBean = list.get(l);
                if(listBean.getSelected() == 1){
                    p += listBean.getNum()*listBean.getBargainPrice();
                    n += listBean.getNum();
                }
            }
        }
        PriceNumber pn = new PriceNumber(p,n);
        Message message = Message.obtain();
        message.what = 0;
        message.obj = pn;
        handler.sendMessage(message);
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    public void setAllChecked(boolean allChecked) {
        progressbar.setVisibility(View.VISIBLE);
        List<CartBean.DataBean.ListBean> allList = new ArrayList<>();
        for (int a = 0; a < data.size(); a++) {
            List<CartBean.DataBean.ListBean> list = data.get(a).getList();
            for (int i = 0; i < list.size(); i++) {
                allList.add(list.get(i));
            }
        }
        allIndex = 0;
        upDateGroupChecked(allChecked, allList);

    }

    private void upDateGroupChecked(final boolean ck, final List<CartBean.DataBean.ListBean> allList) {
        CartBean.DataBean.ListBean listBean = allList.get(allIndex);
        Map<String, String> map = new HashMap<>();
        map.put("uid", "71");
        map.put("sellerid", String.valueOf(listBean.getSellerid()));
        map.put("pid", String.valueOf(listBean.getPid()));
        map.put("selected", String.valueOf(ck ? 1 : 0));
        map.put("num", String.valueOf(listBean.getNum()));
        OkHttp3Util.doPost("https://www.zhaoapi.cn/product/updateCarts", map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    allIndex++;

                    if (allIndex < allList.size()) {
                        upDateGroupChecked(ck, allList);
                    } else {
                        Map<String, String> map1 = new HashMap<String, String>();
                        map1.put("uid", "71");
                        map1.put("source", "android");
                        persenter.getCartData("https://www.zhaoapi.cn/product/getCarts", map1);
                    }
                }
            }
        });


    }

    class GroupViewHolder {
        CheckBox checkBox;
        TextView textView;

    }

    class ChildViewHolder {
        CheckBox checkBox;
        SimpleDraweeView simpleDraweeView;
        TextView description;
        Button del;
        TextView price;
        Button jian;
        TextView num;
        Button jia;
    }
}
