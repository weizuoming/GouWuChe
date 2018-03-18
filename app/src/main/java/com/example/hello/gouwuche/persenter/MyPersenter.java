package com.example.hello.gouwuche.persenter;


import com.example.hello.gouwuche.bean.CartBean;
import com.example.hello.gouwuche.model.MyModel;
import com.example.hello.gouwuche.persenter.inter.PerInter;
import com.example.hello.gouwuche.view.MainInter;

import java.util.Map;

/**
 * Created by dell on 2018/3/9.
 */

public class MyPersenter implements PerInter {
    MainInter mainInter;
    private final MyModel model;

    public MyPersenter(MainInter mainInter) {
        model = new MyModel(this);
        this.mainInter = mainInter;
    }
    public void getCartData(String url, Map<String,String> map){
        model.getCartData(url,map);
    }

    @Override
    public void onCartData(CartBean cartBean) {
        mainInter.onCartData(cartBean);
    }
}
