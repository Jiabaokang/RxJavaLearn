package com.jbk.rxjavalearn.rxjavaNetExample.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.jbk.rxjavalearn.R;
import com.jbk.rxjavalearn.constant.Constant;
import com.jbk.rxjavalearn.rxjavaNetExample.entity.TranslationBean;
import com.jbk.rxjavalearn.rxjavaNetExample.request.GetRequest_Interface;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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

    // 设置变量 = 模拟轮询服务器次数
    private int i = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inteveralQuery();
        conditionsNetworkRequestQuery();
    }

    /**
     * 执行无限次数的网络请求查询(无条件)
     */
    private void inteveralQuery() {
        /*
         * 步骤1：采用interval（）延迟发送
         * 注：此处主要展示无限次轮询，若要实现有限次轮询，仅需将interval（）改成intervalRange（）即可
         **/
        Observable.interval(2, 3, TimeUnit.SECONDS)
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
                                        //Log.i(Constant.TAG,)
                                        //Toast.makeText(RetrofitRxjavaActivity.this, translationBean.toString(), Toast.LENGTH_LONG).show();
                                        translationBean.show();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.i(TAG, "请求失败");
                                    }

                                    @Override
                                    public void onComplete() {
                                        Log.i(Constant.TAG,Constant.COMPLET_EVENT);
                                    }
                                });
                    }
                })
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Long aLong) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "对error时间做出相应");
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "对象complete时间做出相应");
                    }
                });
    }

    /**
     * 执行有条件的网络请求次数查询
     */
    private void conditionsNetworkRequestQuery() {

        //1.创建Retrifit实例
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/")
                .addConverterFactory(GsonConverterFactory.create())//支持json解析
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//支持rxjava
                .build();


        //2.创建网络请求的接口实例
        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);

        //3.采用Observable<T>对网络请求进行封装
        Observable<TranslationBean> observable = request.getCall();

        //4.发送网络请求 & 通过repeatWhen（）进行轮询
        observable.repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {

            @Override
            // 在Function函数中，必须对输入的 Observable<Object>进行处理，此处使用flatMap操作符接收上游的数据
            public ObservableSource<?> apply(@NonNull Observable<Object> objectObservable) throws Exception {
                // 将原始 Observable 停止发送事件的标识（Complete（） /  Error（））转换成1个 Object 类型数据传递给1个新被观察者（Observable）
                // 以此决定是否重新订阅 & 发送原来的 Observable，即轮询
                // 此处有2种情况：
                // 1. 若返回1个Complete（） /  Error（）事件，则不重新订阅 & 发送原来的 Observable，即轮询结束
                // 2. 若返回其余事件，则重新订阅 & 发送原来的 Observable，即继续轮询
                return objectObservable.flatMap(new Function<Object, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(@NonNull Object throwable) throws Exception {

                        // 加入判断条件：当轮询次数 = 5次后，就停止轮询
                        if (i > 2) {
                            // 此处选择发送onError事件以结束轮询，因为可触发下游观察者的onError（）方法回调
                            return Observable.error(new Throwable("轮询结束"));
                        }
                        // 若轮询次数＜4次，则发送1Next事件以继续轮询
                        // 注：此处加入了delay操作符，作用 = 延迟一段时间发送（此处设置 = 2s），以实现轮询间间隔设置
                        return Observable.just(1).delay(2000, TimeUnit.MILLISECONDS);
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TranslationBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TranslationBean translationBean) {
                        //接受服务器返回的数据
                        //Toast.makeText(RetrofitRxjavaActivity.this, translationBean.toString(), Toast.LENGTH_SHORT).show();
                        translationBean.show();
                        i++;
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 获取轮询结束信息
                        Log.d(TAG, e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(Constant.TAG,Constant.COMPLET_EVENT);
                    }
                });

    }


}
