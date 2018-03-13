package com.jbk.rxjavalearn.rxjavaNetExample.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.jbk.rxjavalearn.R
import com.jbk.rxjavalearn.rxjavaNetExample.request.NetworkRequestType
import kotlinx.android.synthetic.main.activity_newwork_example.*

/**
 * Created on 2018/3/5 10:05
 * @author baokang.jia
 * @desc
 * @company bqjr
 */
class NetworkRequstExampleActivity : AppCompatActivity() {

    /**
     * 测试Rxjava三种网络请求
     */
    val mRequestType = NetworkRequestType()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newwork_example)

        initViews()
    }

    private fun initViews() {

        tv_rxjava_not_condition_loop.setOnClickListener { view ->
            mRequestType.notConditionLoopNetworkRequest()
        }

        tv_rxjava_has_condition_loop.setOnClickListener {
            mRequestType.havConditionNetworkRequest()
        }

        tv_rxjava_network_requst_nest.setOnClickListener { view ->
            if (view is TextView) mRequestType.nestNetworkRequest(view)
        }

        tv_rxjava_not_condition_loop.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(p0: View?): Boolean {
                mRequestType.quitNetworkLoop()
                return false
            }
        })

    }


}