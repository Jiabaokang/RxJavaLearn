package com.jbk.rxjavalearn.rxjavaNetExample.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.jbk.rxjavalearn.R;
import com.jbk.rxjavalearn.rxjavaNetExample.entity.TranslationBean;
import com.jbk.rxjavalearn.rxjavaNetExample.request.GetRequest_Interface;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 2018/3/5 10:05
 *
 * @author baokang.jia
 * @desc
 * @company bqjr
 */

public class RetrofitRxjavaActivity extends AppCompatActivity {

    private static final String TAG = "RxJavafixRxjavaActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inteveralQuery();
    }

    /**
     * 执行无限次数的网络请求查询
     */
    private void inteveralQuery() {
        /*
         * 步骤1：采用interval（）延迟发送
         * 注：此处主要展示无限次轮询，若要实现有限次轮询，仅需将interval（）改成intervalRange（）即可
         **/
        Observable.interval(2, 2, TimeUnit.SECONDS)
        // 参数说明：
        // 参数1 = 第1次延迟时间；
        // 参数2 = 间隔时间数字；
        // 参数3 = 时间单位；
        // 该例子发送的事件特点：延迟2s后发送事件，每隔1秒产生1个数字（从0开始递增1，无限个）

                 /*
                  * 步骤2：每次发送数字前发送1次网络请求（doOnNext（）在执行Next事件前调用）
                  * 即每隔1秒产生1个数字前，就发送1次网络请求，从而实现轮询需求
                  **/
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        //在此方法中执行网络请求
                        /*
                         * 3.通过Rerofit发送网络请求
                         */
                        //a.创建Rerofit对象
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://fy.iciba.com/")//请求的url
                                .addConverterFactory(GsonConverterFactory.create())//使用gson解析
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//支持Rxjava
                                .build();

                        //b.创建网络请求接口的实例
                        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);

                        //c.采用Observable<...>形式对网络请求进行封装
                        Observable<TranslationBean> observable = request.getCall();

                        //d.通过线程切换发送网络请求
                        observable.subscribeOn(Schedulers.io())//切换io线程进行网络请求
                                .observeOn(AndroidSchedulers.mainThread()) //切换到主线程进行结果处理
                                .subscribe(new Observer<TranslationBean>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(TranslationBean translationBean) {
                                        Toast.makeText(RetrofitRxjavaActivity.this,translationBean.toString(),Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.i(TAG,"请求失败");
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                    }
                }).subscribe(new Observer<Long>() {//连接被观察者和观察者
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long aLong) {

            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG,"对error时间做出相应");
            }

            @Override
            public void onComplete() {
                Log.i(TAG,"对象complete时间做出相应");
            }
        });

    }
}
