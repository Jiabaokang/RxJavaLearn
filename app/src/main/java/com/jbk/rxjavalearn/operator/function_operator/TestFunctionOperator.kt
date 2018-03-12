package com.jbk.rxjavalearn.operator.function_operator

import android.util.Log
import com.jbk.rxjavalearn.constant.Constant
import io.reactivex.*
import io.reactivex.disposables.Disposable

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
    fun testSubsribe(){
        //observable.subscribe(observer);
        //被观察者对象
       val observable =  Observable.create(object :ObservableOnSubscribe<Long>{
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
        val observer = object :Observer<Long>{
            override fun onComplete() {
                Log.i(Constant.TAG,Constant.COMPLET_EVENT)
            }

            override fun onSubscribe(d: Disposable) {
                Log.i(Constant.TAG,"开始使用subscribe进行连接")
            }

            override fun onNext(t: Long) {
                Log.d(Constant.TAG, "对Next事件"+ t +"作出响应"  );
            }

            override fun onError(e: Throwable) {
                Log.i(Constant.TAG,Constant.ERROR_EVENT)
            }
        }

        /**
         * 订阅，连接观察者和被观察者
         */
        observable.subscribe(observer)

    }

}