package com.jbk.rxjavalearn.operator.function_operator

import android.util.Log
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.jbk.rxjavalearn.constant.Constant
import com.jbk.rxjavalearn.rxjavaNetExample.entity.TranslationBean
import com.jbk.rxjavalearn.rxjavaNetExample.request.GetRequest_Interface
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created on 2018/3/12 15:14
 * @author baokang.jia
 * @desc
 * @company bqjr
 */
class TestFunctionOperator {


    /**
     * 测试subscribe
     * observable.subscribe(observer);
     * 前者 = 被观察者（observable）；后者 = 观察者（observer 或 subscriber）
     */
    fun testSubsribe() {
        //observable.subscribe(observer);
        //被观察者对象
        val observable = Observable.create(object : ObservableOnSubscribe<Long> {
            override fun subscribe(e: ObservableEmitter<Long>) {
                e.onNext(1)
                e.onNext(2)
                e.onNext(3)
                e.onComplete()
            }

        })

        /**
         * 观察者
         */
        val observer = object : Observer<Long> {
            override fun onComplete() {
                Log.i(Constant.TAG, Constant.COMPLET_EVENT)
            }

            override fun onSubscribe(d: Disposable) {
                Log.i(Constant.TAG, "开始使用subscribe进行连接")
            }

            override fun onNext(t: Long) {
                Log.d(Constant.TAG, "对Next事件" + t + "作出响应");
            }

            override fun onError(e: Throwable) {
                Log.i(Constant.TAG, Constant.ERROR_EVENT)
            }
        }

        /**
         * 订阅，连接观察者和被观察者
         */
        observable.subscribe(observer)
    }

    /**
     * 测试Rxjava中线程调度器
     *
     * 1. RxJava线程控制（调度 / 切换）的作用是什么？
     * 指定 被观察者 （Observable） / 观察者（Observer） 的工作线程类型。
     * 2. 为什么要进行RxJava线程控制（调度 / 切换）？
     * 2.1 背景
     * 在 RxJava模型中，被观察者 （Observable） / 观察者（Observer）的工作线程 = 创建自身的线程
     * 即，若被观察者 （Observable） / 观察者（Observer）在主线程被创建，那么他们的工作（生产事件 / 接收& 响应事件）就会发生在主线程

     * 因为创建被观察者 （Observable） / 观察者（Observer）的线程 = 主线程
     * 所以生产 事件 / 接收& 响应事件都发生在主线程
     *
     * 采用 RxJava内置的线程调度器（ Scheduler ），即通过 功能性操作符subscribeOn（） & observeOn（）实现
     *
     *
     * 在 RxJava中，内置了多种用于调度的线程类型
     * 类型	含义	应用场景
     * Schedulers.immediate()	当前线程 = 不指定线程	默认
     * AndroidSchedulers.mainThread()	Android主线程	操作UI
     * Schedulers.newThread()	常规新线程	耗时等操作
     * chedulers.io()	io操作线程	网络请求、读写文件等io密集型操作
     * Schedulers.computation()	CPU计算操作线程	大量计算操作
     *
     */

    fun testScheduler(){
        //步骤一.创建被观察者对象
        val retrofit = Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        //步骤二.创建观察者对象
        val request = retrofit.create(GetRequest_Interface::class.java)

        //步骤三.订阅，连接Observable和Observer
        val observable:Observable<TranslationBean> = request.call

        //步骤四.发送网络请求
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object :Observer<TranslationBean>{
                    override fun onComplete() {
                        Log.d(Constant.TAG, Constant.COMPLET_EVENT);
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.d(Constant.TAG, "开始采用subscribe连接");
                    }

                    override fun onNext(t: TranslationBean) {
                        Log.d(Constant.TAG, t.toString());
                        t.show()
                    }

                    override fun onError(e: Throwable) {
                        Log.d(Constant.TAG, Constant.ERROR_EVENT);
                    }

                })

    }

}