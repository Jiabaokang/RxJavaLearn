package com.jbk.rxjavalearn.rxjavaNetExample.request

import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.jbk.rxjavalearn.constant.Constant
import com.jbk.rxjavalearn.constant.Constant.TAG
import com.jbk.rxjavalearn.rxjavaNetExample.entity.TranslationBean
import com.jbk.rxjavalearn.rxjavaNetExample.entity.TranslationBean2
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created on 2018/3/13 11:28
 * @author baokang.jia
 *
 * @desc
 * 1、网络循环无线次数(无提交)。
 * 2、执行有条件的网络请求次数查询。
 * 3、网路请求嵌套(例如注册成功后直接登陆的场景)
 *
 * @company bqjr
 */
class NetworkRequestType {

    /**
     * 执行次数
     */
    var mCount = 0

    /**
     * 创建Retrofit对象
     */
    val mRetrofit = Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())//支持json界面
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//支持Rxjava2
            .build()
    /**
     * 此对象用来结束事件流
     */
    lateinit var mDisposable: Disposable

    /**
     * 请求网络的接口//采用Observable<...>形式 对 2个网络请求 进行封装
     */
    lateinit var observable1: Observable<TranslationBean>//请求1
    lateinit var observable2: Observable<TranslationBean2>//请求2

    /**
     * 无条件循环网络请求
     * 步骤1：采用interval（）延迟发送
     * 注：此处主要展示无限次轮询，若要实现有限次轮询，仅需将interval（）改成intervalRange（）即可
     */
    fun notConditionLoopNetworkRequest() {
        // 参数说明：
        // 参数1 = 第1次延迟时间；
        // 参数2 = 间隔时间数字；
        // 参数3 = 时间单位；
        // 该例子发送的事件特点：延迟2s后发送事件，每隔1秒产生1个数字（从0开始递增1，无限个）
        Observable.interval(2, 2, TimeUnit.SECONDS)
                /*
                 * 步骤2：每次发送数字前发送1次网络请求（doOnNext（）在执行Next事件前调用）
                 * 即每隔1秒产生1个数字前，就发送1次网络请求，从而实现轮询需求
                 **/
                .doOnNext(object : Consumer<Long> {
                    override fun accept(t: Long) {
                        //a.创建Rerofit对象

                        //b.创建网络请求接口的实例
                        val request: GetRequest_Interface = mRetrofit.create(GetRequest_Interface::class.java)

                        //封装网络请求
                        val observable: Observable<TranslationBean> = request.call

                        //d.通过线程切换发送网络请求
                        observable.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : Observer<TranslationBean> {
                                    override fun onComplete() {
                                        Log.i(Constant.TAG, Constant.COMPLET_EVENT)
                                    }

                                    override fun onSubscribe(d: Disposable) {
                                        Log.i(Constant.TAG, "使用subscribe进行连接")
                                    }

                                    override fun onNext(t: TranslationBean) {
                                        t.show()
                                    }

                                    override fun onError(e: Throwable) {
                                        Log.i(Constant.TAG, Constant.ERROR_EVENT)
                                    }
                                })
                    }
                }).subscribe(object : Observer<Long> {
                    override fun onComplete() {
                        Log.i(Constant.TAG, Constant.COMPLET_EVENT)
                    }

                    override fun onSubscribe(d: Disposable) {
                        mDisposable = d
                        Log.i(Constant.TAG, "使用subscribe进行连接")
                    }

                    override fun onNext(t: Long) {
                        Log.i(Constant.TAG, "接受到事件" + t)
                    }

                    override fun onError(e: Throwable) {
                        Log.i(Constant.TAG, Constant.ERROR_EVENT)
                    }
                })
    }

    fun quitNetworkLoop() {
        mDisposable.dispose()
    }

    /**
     * 有条件网络请，例如达到某种提交后就不在执行请求
     */
    fun havConditionNetworkRequest() {

        //创建网络请求的接口实例
        val request: GetRequest_Interface = mRetrofit.create(GetRequest_Interface::class.java)

        //采用Observable<T>对网络请求进行封装
        val observer = request.call

        //发送网络请求 & 通过repeatWhen（）进行轮询
        observer.repeatWhen(object : Function<Observable<Any>, ObservableSource<*>> {

            // 将原始 Observable 停止发送事件的标识（Complete（） /  Error（））转换成1个 Object 类型数据传递给1个新被观察者（Observable）
            // 以此决定是否重新订阅 & 发送原来的 Observable，即轮询
            // 此处有2种情况：
            // 1. 若返回1个Complete（） /  Error（）事件，则不重新订阅 & 发送原来的 Observable，即轮询结束
            // 2. 若返回其余事件，则重新订阅 & 发送原来的 Observable，即继续轮询
            override fun apply(observable: Observable<Any>): ObservableSource<*> {
                return observable.flatMap(object : Function<Any, ObservableSource<*>> {
                    override fun apply(t: Any): ObservableSource<*> {
                        // 加入判断条件：当轮询次数 = 5次后，就停止轮询
                        if (mCount > 3) {
                            // 此处选择发送onError事件以结束轮询，因为可触发下游观察者的onError（）方法回调
                            return Observable.error<Any>(Throwable("轮询结束"))
                        }
                        // 若轮询次数＜4次，则发送1Next事件以继续轮询
                        // 注：此处加入了delay操作符，作用 = 延迟一段时间发送（此处设置 = 2s），以实现轮询间间隔设置
                        return Observable.just(1).delay(2000, TimeUnit.MILLISECONDS);
                    }
                })
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<TranslationBean> {

                    override fun onComplete() {
                        Log.i(Constant.TAG, Constant.COMPLET_EVENT)
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.i(Constant.TAG, "使用subscribe进行连接")
                    }

                    override fun onNext(t: TranslationBean) {
                        //接受服务器返回的数据
                        mCount++
                        t.show()
                    }

                    override fun onError(e: Throwable) {
                        // 获取轮询结束信息
                        Log.i(Constant.TAG, Constant.ERROR_EVENT)
                    }
                })
    }


    /**
     * 网路请求嵌套
     * 例如 注册成功后根据返回结果直接登陆
     *
     * 下面模拟 翻译-->>根据金山词霸的api
     */
    fun nestNetworkRequest(view: TextView) {
        //初始化网络请求实例
        val request = mRetrofit.create(GetRequest_Interface::class.java)

        //封装网络请求1
        observable1 = request.call
        //封装网络请求2
        observable2 = request.call_2

        observable1.subscribeOn(Schedulers.io())     //初始化被观察者,切换到IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())  //新观察者，切换到主线程，进行网络请求结果的处理
                .doOnNext(object : Consumer<TranslationBean> {
                    override fun accept(t: TranslationBean) {
                        Toast.makeText(view.context, "第一次网络请求成功", Toast.LENGTH_SHORT).show()
                        Log.e(Constant.TAG, "第一次网络请求成功")
                        //对第一次网络请求结果进行处理
                        t.show()
                    }
                })
                .observeOn(Schedulers.io())// （新被观察者，同时也是新观察者）切换到IO线程去发起登录请求

                // 特别注意：因为flatMap是对初始被观察者作变换，所以对于旧被观察者，它是新观察者，所以通过observeOn切换线程
                // 但对于初始观察者，它则是新的被观察者
                .flatMap(object : Function<TranslationBean, ObservableSource<TranslationBean2>> {
                    override fun apply(t: TranslationBean): ObservableSource<TranslationBean2> {
                        // 将网络请求1转换成网络请求2，即发送网络请求2
                        return observable2
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Consumer<TranslationBean2> {
                    override fun accept(t: TranslationBean2) {
                        Toast.makeText(view.context, "第二次网络请求成功", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "第2次网络请求成功");
                        // 对第2次网络请求返回的结果进行操作 = 显示翻译结果
                        // t.content
                        Log.e(Constant.TAG, "第二次翻译的结果" + t.toString())
                    }
                }, object : Consumer<Throwable> {
                    override fun accept(t: Throwable) {
                        System.out.println("登录失败")
                    }
                })
    }


}