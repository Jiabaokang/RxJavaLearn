package com.jbk.rxjavalearn;

import android.util.Log;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created on 2018/2/28 10:40
 *
 * @author baokang.jia
 * @desc
 * @company bqjr
 */

public class TestObserver {

    private static final String TAG = "RxJava";

    private Integer i = 10;

    /**
     * defer操作符
     *  1.定时操作：在经过了x秒后，需要自动执行y操作
     *  2.周期性操作：每隔x秒后，需要自动执行y操作
     *  直到有观察者（Observer ）订阅时，才动态创建被观察者对象（Observable） & 发送事件
     */
    public void deferObservable(){
        //第一次对i赋值

        //通过defer定义被观察者对象
        //此时这个观察者对象还没有创建
        Observable<Integer> deferObservable = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> call() throws Exception {
                return Observable.just(i);
            }
        });

        //第二次对i赋值
        i = 15;

        deferObservable.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "开始采用subscribe连接");
            }

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "接收到的整数是"+ value  );
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "对Error事件作出响应");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }
        });
    }


}
