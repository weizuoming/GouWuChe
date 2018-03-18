package com.example.hello.gouwuche.model;

import com.example.hello.gouwuche.bean.CartBean;
import com.example.hello.gouwuche.persenter.inter.PerInter;
import com.example.hello.gouwuche.utils.OkHttp3Util;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by dell on 2018/3/9.
 */

public class MyModel {
    PerInter perInter;

    public MyModel(PerInter perInter) {
        this.perInter = perInter;
    }

    public void getCartData(String url, Map<String,String> map){
        OkHttp3Util.doPost(url, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String json = response.body().string();
                    CartBean cartBean = new Gson().fromJson(json, CartBean.class);
                    perInter.onCartData(cartBean);
                }
            }
        });
    }

}
