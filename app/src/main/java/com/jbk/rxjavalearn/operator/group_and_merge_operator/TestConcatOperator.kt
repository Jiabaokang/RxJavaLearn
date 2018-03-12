package com.jbk.rxjavalearn.operator.group_and_merge_operator

import android.app.Activity
import android.util.Log
import com.jbk.rxjavalearn.R
import com.jbk.rxjavalearn.constant.Constant
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * Created on 2018/3/8 11:09
 * @author baokang.jia
 * @desc
 * @company bqjr
 */
class TestConcatOperator (activity:Activity){

    val activity:Activity

    init {
        this.activity = activity
    }

    /**
     * 创建观察者
     */
    val observer:Observer<Int> = object :Observer<Int> {

        override fun onComplete() {
            Log.i(Constant.TAG,Constant.COMPLET_EVENT)
        }

        override fun onSubscribe(d: Disposable) {

        }

        override fun onNext(t: Int) {
            Log.d(Constant.TAG, activity.getString(R.string.received_the_event)+t)
        }

        override fun onError(e: Throwable) {
            Log.i(Constant.TAG,Constant.ERROR_EVENT)
        }
    }

    /**
     * concat（）：组合多个被观察者（≤4个）一起发送数据
     * 注：串行执行
     */
     fun concat() {
        // concat（）：组合多个被观察者（≤4个）一起发送数据
        // 注：串行执行
        Observable.concat(
                Observable.just(1, 2, 3),
                Observable.just(4, 5, 6),
                Observable.just(7, 8, 9),
                Observable.just(10, 11, 12))
                .subscribe(observer)
    }

    /**
     * concatArray()
     * 组合多个被观察者一起发送数据（可 > 4个）
     * 注意：穿行执行
     */
    fun concatArray(){
        Observable.concatArray(Observable.just(1, 2, 3),
                Observable.just(4, 5, 6),
                Observable.just(7, 8, 9),
                Observable.just(10, 11, 12),
                Observable.just(13,14,15))
                .subscribe(observer)
    }




}