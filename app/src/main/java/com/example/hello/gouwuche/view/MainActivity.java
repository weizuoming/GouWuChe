package com.example.hello.gouwuche.view;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hello.gouwuche.R;
import com.example.hello.gouwuche.adapter.MyExpandAdapter;
import com.example.hello.gouwuche.bean.CartBean;
import com.example.hello.gouwuche.bean.PriceNumber;
import com.example.hello.gouwuche.persenter.MyPersenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainInter {





    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg != null){
                if(msg.what == 0){
                    PriceNumber pn = (PriceNumber) msg.obj;
                    total.setText("总价："+pn.getPrice()+"元");
                    go.setText("去结算（"+pn.getNum()+"）");
                }
            }
        }
    };
    private MyExpandAdapter adapter;
    private ExpandableListView expand;
    private CheckBox all;
    private TextView total;
    private Button go;
    private ProgressBar progressbar;
    private MyPersenter persenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        expand = findViewById(R.id.expand);
        all = findViewById(R.id.all);
        total = findViewById(R.id.total);
        go = findViewById(R.id.go);
        progressbar = findViewById(R.id.progressbar);

        ButterKnife.bind(this);
        persenter = new MyPersenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressbar.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("uid", "71");
        map.put("source", "android");
        persenter.getCartData("https://www.zhaoapi.cn/product/getCarts", map);
    }

    @Override
    public void onCartData(final CartBean cartBean) {
        if (cartBean != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressbar.setVisibility(View.GONE);
                    //先判断所有的子条目是不是全部选中，1.全部选中：组的状态也是选中 2.有一个未选中：组的状态也是未选中
                    for(int i = 0;i<cartBean.getData().size();i++){
                        CartBean.DataBean dataBean = cartBean.getData().get(i);

                        if(isChildChecked(i,cartBean)){
                            dataBean.setChecked(true);
                        }else{
                            dataBean.setChecked(false);
                        }
                    }
                    all.setChecked(isAllGroupChecked(cartBean));//全选按钮的状态
                    expand.setGroupIndicator(null);
                    List<CartBean.DataBean> data = cartBean.getData();
                    adapter = new MyExpandAdapter(data, MainActivity.this, persenter,progressbar,handler,new MainActivity());
                    expand.setAdapter(adapter);
                    //默认打开分组
                    for (int i = 0; i < data.size(); i++) {
                        expand.expandGroup(i);
                    }
                    adapter.sendPriveNum();
                }
            });
        }
    }

    /**
     * 所有的组是否选中
     * @return
     * @param cartBean
     */
    private boolean isAllGroupChecked(CartBean cartBean) {
        for (int i =0;i<cartBean.getData().size();i++){
            if (!cartBean.getData().get(i).isChecked()){//表示有没选中的组
                return false;
            }
        }
        return true;
    }

    /**
     * 判断子条目是否全部选中，返回true或者false
     *
     * @param
     * @return
     */
    private boolean isChildChecked(int i,CartBean cartBean) {
        List<CartBean.DataBean.ListBean> list = cartBean.getData().get(i).getList();
        for(int j = 0;j<list.size();j++){
            if(list.get(j).getSelected() == 0){
                return  false;
            }
        }
        return true;
    }

    @OnClick(R.id.all)
    public void onViewClicked() {
        if(adapter != null){
            //点击全选按钮时进行的操作
            adapter.setAllChecked(all.isChecked());
        }
    }
}
