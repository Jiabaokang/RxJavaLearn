package com.jbk.rxjavalearn

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
class MainActivity : AppCompatActivity() {

    val TAG = "RxJava"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //操作符的使用
        //TestCreateOperator(this).createObservable()
        //TestCreateOperator(this).justObservable()
        //TestCreateOperator(this).fromArrayObservable()
        //TestCreateOperator(this).fromIterable()
        //TestCreateOperator(this).deferOvservable()
        //TestCreateOperator(this).timerObservable()

        //TestCreateOperator(this).intervalObservable()//无线次数轮询
        //TestCreateOperator(this).intervalRanangeObservable() 有限次数轮询
        TestCreateOperator(this).rangeObservable()
    }



}
