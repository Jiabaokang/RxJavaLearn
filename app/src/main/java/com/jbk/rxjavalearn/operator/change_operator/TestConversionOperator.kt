package com.jbk.rxjavalearn.operator.change_operator

import android.util.Log
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function

/**
 * Created on 2018/3/6 10:44
 * @author baokang.jia
 * @desc
 * @company bqjr
 */
class TestConversionOperator {

    val TAG = "Conversion"

    constructor()

    /**
     * map转换符
     *
     * 采用Rxjava基于事件流的调用
     */
    fun mapOperator() {

        // 1. 被观察者发送事件 = 参数为整型 = 1、2、3
        Observable.create(object : ObservableOnSubscribe<Int> {
            override fun subscribe(e: ObservableEmitter<Int>) {
                e.onNext(1)
                e.onNext(2)
                e.onNext(3)
            }

            //2. 使用Map变换操作符中的Function函数对被观察者发送的事件进行统一变换：整型变换成字符串类型
        }).map(object : Function<Int, String> {
            override fun apply(integer: Int): String {
                return "使用-->>Map变换操作符 将事件" + integer + "的参数从 整型" + integer + " 变换成 字符串类型" + integer
            }
            // 3. 观察者接收事件时，是接收到变换后的事件 = 字符串类型
        }).subscribe(object : Consumer<String> {
            override fun accept(t: String) {
                Log.i(TAG, t)
            }

        })
    }


    /**
     * 被观察者生产的事件序列拆分手的新的事件序列是无序的
     *
     * flatMap转换符
     */
    fun flatMapOperator() {
        //被观察者发送事件
        Observable.create(object : ObservableOnSubscribe<Int> {
            override fun subscribe(e: ObservableEmitter<Int>) {
                e.onNext(3)
                e.onNext(2)
                e.onNext(1)
            }
            //采用flatMap变换符
        }).flatMap(object : Function<Int, ObservableSource<String>> {
            override fun apply(integer: Int): ObservableSource<String> {
                val list: ArrayList<String> = arrayListOf()
                for (i in 0..2) {
                    list.add("flatMap-->>我是事件 " + integer + "拆分后的子事件" + i)
                }

                // 通过flatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String事件
                // 最终合并，再发送给被观察者
                val source: ObservableSource<String> = Observable.fromIterable(list)
                return source

            }

        }).subscribe(object : Consumer<String> {
            override fun accept(t: String) {
                Log.i(TAG, t)
            }

        })
    }

    /**
     * ConcatMap转换符
     *
     * 作用：类似FlatMap（）操作符
     *
     * 与FlatMap（）的 区别在于：拆分 & 重新合并生成的事件序列 的顺序 = 被观察者旧序列生产的顺序
     *
     * 作用：有序的将被观察者发送的整个事件序列进行变换
     *
     * 代码可以参考上面 flatMapOperator() 方法
     */

    fun concatMap(){
        Observable.create(object :ObservableOnSubscribe<Int>{
            override fun subscribe(e: ObservableEmitter<Int>) {
                e.onNext(1)
                e.onNext(2)
                e.onNext(3)
            }
        }).concatMap(object :Function<Int,ObservableSource<String>>{
            override fun apply(t: Int): ObservableSource<String> {
                val list:ArrayList<String> = arrayListOf()
                for(i in 0..3){
                    list.add("concatMap-->>我是事件 " + t + "拆分后的子事件" + i)
                }
                val source:ObservableSource<String> = Observable.fromIterable(list)
                return source
            }
        }).subscribe(object :Consumer<String>{
            override fun accept(t: String) {
                Log.i(TAG,t)
            }
        })
    }

    /**
     * buffer转换符
     * 作用
     * 定期从 被观察者（Obervable）需要发送的事件中 获取一定数量的事件 & 放到缓存区中，最终发送
     *
     * 应用场景：缓存被观察者发送的事件
     */
    fun buffer(){
        Observable.just(1,2,3,4,5,6)

            //设置缓存区大小 & 步长
            // 缓存区大小 = 每次从被观察者中获取的事件数量
            // 步长 = 每次获取新事件的数量
                .buffer(3,1)

                .subscribe(object :Observer<List<Int>>{
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(stringList: List<Int>) {
                        val size = stringList.size
                        //
                        Log.d(TAG, " 缓存区里的事件数量 = " +  stringList.size);
                        for (i in 0..size-1) {
                            Log.d(TAG, " 事件 = " + stringList.get(i));
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.i(TAG,"对Error事件作出处理")
                    }

                    override fun onComplete() {
                        Log.i(TAG,"对onComplete事件作出处理")
                    }
                })
    }

}