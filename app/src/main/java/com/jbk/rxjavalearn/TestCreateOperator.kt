package com.jbk.rxjavalearn

import android.app.Activity
import android.util.Log
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Created on 2018/2/28 16:53
 * @author baokang.jia
 * @desc    Rxjava 创建操作符
 * @company bqjr
 */
class TestCreateOperator constructor(activity: MainActivity){

    val TAG = "RxJava"
    var money = 1000;
    val mContext:Activity

    init {
        mContext = activity
        Log.i(activity.TAG,"初始化构造函数"+money)
    }

    /**
     * interval操作符
     *
     *  快速创建1个被观察者对象（Observable）
     *  发送事件的特点：每隔指定时间 就发送 事件
     *  发送的事件序列 = 从0开始、无限递增1的的整数序列
     */
      fun intervalObservable() {
        // 参数说明：
        // 参数1 = 第1次延迟时间；
        // 参数2 = 间隔时间数字；
        // 参数3 = 时间单位；
        Observable.interval(3, 2, TimeUnit.SECONDS)
                // 该例子发送的事件序列特点：延迟3s后发送事件，每隔1秒产生1个数字（从0开始递增1，无限个）
                .subscribe(object : Observer<Long> {
                    lateinit var dis: Disposable

                    override fun onSubscribe(d: Disposable) {
                        dis = d
                        Log.i(TAG, "interval  开始使用subscribe进行连接")
                    }

                    override fun onNext(t: Long) {
                        Log.i(TAG, "接受到了事件-->>>" + t)
                        if (t == 5L) {
                            mContext.runOnUiThread{
                                Toast.makeText(mContext, "事件结束了", Toast.LENGTH_SHORT).show()
                            }
                            dis.dispose()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.i(TAG, "对Error事件作出响应")
                    }

                    override fun onComplete() {
                        Log.i(TAG, "对Complete事件作出响应")
                    }

                })

    }

    /**
     *  timer操作符
     *
     *  快速创建1个被观察者对象（Observable）
     *  发送事件的特点：延迟指定时间后，发送1个数值0（Long类型）
     *  本质 = 延迟指定时间后，调用一次 onNext(0)
     *
     *  应用场景
     *  延迟指定事件，发送一个0，一般用于检测
     */
      fun timerObservable() {
        // 该例子 = 延迟5s后，发送一个long类型数值
        // 注：timer操作符默认运行在一个新线程上
        // 也可自定义线程调度器（第3个参数）：timer(long,TimeUnit,Scheduler)
        val time = 5L
        Observable.timer(time, TimeUnit.SECONDS)
                .subscribe(object : Observer<Long> {

                    lateinit var dis: Disposable

                    override fun onSubscribe(d: Disposable) {
                        dis = d
                        Log.i(TAG, "timer操作符  开始使用subscribe进行连接")
                    }

                    override fun onNext(t: Long) {
                        Log.i(TAG, "接受到了事件-->>>" + t)
                        if (t == 5L) {
                            Toast.makeText(mContext, "事件结束了", Toast.LENGTH_SHORT).show()
                            dis.dispose()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.i(TAG, "对Error事件作出响应")
                    }

                    override fun onComplete() {
                        Log.i(TAG, "对Complete事件作出响应")
                    }

                })
    }

    /**
     * 调用java方法验证deferOvservable
     */
      fun deferOvservable() {
        //调用Java代码
        TestObserver().deferObservable()
    }


    /**
     *  fromIterable操作符
     *  快速创建1个被观察者对象（Observable）
     *  发送事件的特点：直接发送 传入的集合List数据
     *  会将数组中的数据转换为Observable对象
     */
      fun fromIterable() {
        val item: ArrayList<String> = arrayListOf()
        item.add("A")
        item.add("B")
        item.add("C")
        for (i in 0..10) {
            item.add("添加数据" + i)
        }
        Observable.fromIterable(item).subscribe(object : Observer<String> {

            override fun onSubscribe(d: Disposable) {
                Log.i(TAG, "开始使用subscribe进行连接--fromIterable")
            }

            override fun onNext(t: String) {
                Log.i(TAG, "集合List数据" + t)
            }

            override fun onError(e: Throwable) {
                Log.i(TAG, "对Error事件作出响应")
            }

            override fun onComplete() {
                Log.i(TAG, "循环结束")
            }
        })
    }


    /**
     * fromArray操作符
     * 快速创建 被观察者对象（Observable） & 发送10个以上事件（数组形式）
     * 数组元素遍历
     */
      fun fromArrayObservable() {
        //基本类型
        val itemInt: IntArray = intArrayOf(1, 2, 3)
        //字符串类型比较特殊，也是可以用获取字符串类型的方式定义基本类型
        val items: Array<String> = arrayOf("01", "02", "03", "04")

        Observable.fromArray(items)
                .subscribe(object : Observer<Array<String>> {

                    override fun onSubscribe(d: Disposable) {
                        Log.i(TAG, "开始使用subscribe进行连接")
                    }

                    override fun onNext(t: Array<String>) {
                        for (i in 0 until t.size) {
                            Toast.makeText(mContext, t[i], Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.i(TAG, "对Error事件作出响应")
                    }

                    override fun onComplete() {
                        Log.i(TAG, "对Complete事件作出响应")
                    }
                })

        Observable.fromArray(itemInt)
                .subscribe(object : Observer<IntArray> {

                    override fun onSubscribe(d: Disposable) {
                        Log.i(TAG, "循环开始")
                    }

                    override fun onNext(t: IntArray) {
                        for (i in 0 until t.size) {
                            Log.i(TAG, "数组值" + t[i])
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.i(TAG, "对Error事件作出响应")
                    }

                    override fun onComplete() {
                        Log.i(TAG, "循环结束")
                    }

                })
    }


    /**
     * just操作符
     * 最大只能发送10个事件
     */
      fun justObservable() {
        Observable.just(1, 2, 3).subscribe(object : Observer<Int> {

            override fun onSubscribe(d: Disposable) {
                Log.i(TAG, "开始使用subscribe进行连接")
            }

            override fun onNext(integer: Int) {
                if (integer == 2) {

                        Toast.makeText(mContext, "这是Kotlin的Toast", Toast.LENGTH_LONG).show()

                    Log.i(TAG, "对Next事件" + integer + "做出相应")
                }
            }

            override fun onError(e: Throwable) {
                Log.i(TAG, "对Error事件作出响应")
            }

            override fun onComplete() {
                Log.i(TAG, "对Complete事件作出响应")
            }
        })

    }

    /**
     * create操作符
     * 使用create操作符，创建Observable和Observer对象
     */
      fun createObservable() {
        //1.创建被观察这对象
        val observable = Observable.create(ObservableOnSubscribe<Int> { emitter ->
            //在subscribe方法中定义需要发送的事件
            emitter.onNext(1)
            emitter.onNext(2)
            emitter.onNext(3)
            emitter.onComplete()
        })

        //2.创建观察者对象
        val observer = object : Observer<Int> {
            var mDisposable: Disposable? = null

            override fun onSubscribe(d: Disposable) {
                Log.i(TAG, "开始使用subscribe进行连接")
                mDisposable = d
            }

            override fun onNext(integer: Int) {
                Log.i(TAG, "对Next事件" + integer + "做出相应")
                if (integer == 2) {
                    mDisposable!!.dispose()
                }
            }

            override fun onError(e: Throwable) {
                Log.i(TAG, "对Error事件作出响应")
            }

            override fun onComplete() {
                Log.i(TAG, "对Complete事件作出响应")
            }
        }
        //3.通过订阅联系其观察者和被观察者
        observable.subscribe(observer)
    }

}