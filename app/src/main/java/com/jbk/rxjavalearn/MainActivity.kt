package com.jbk.rxjavalearn

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.jbk.rxjavalearn.operator.change_operator.ChangeOperatorActivity
import com.jbk.rxjavalearn.operator.create_operator.CreateOperatorActivity
import com.jbk.rxjavalearn.operator.function_operator.FunctionOperatorActivity
import com.jbk.rxjavalearn.operator.group_and_merge_operator.GroupAndMergeActivity
import com.jbk.rxjavalearn.rxjavaNetExample.view.NetworkRequstExampleActivity
import com.jbk.rxjavalearn.rxjavaNetExample.view.RetrofitRxjavaActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = "RxJava"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

    }

    private fun initViews() {
        val tv_create_operator =  findViewById<TextView>(R.id.tv_create_operator)

        tv_create_operator.setOnClickListener{
            getaIntent(CreateOperatorActivity::class.java)
        }

        tv_change_operator.setOnClickListener {
            getaIntent(ChangeOperatorActivity::class.java)
        }

        tv_network_example.setOnClickListener {
            getaIntent(NetworkRequstExampleActivity::class.java)
        }

        tv_group_merge_operator.setOnClickListener{
            getaIntent(GroupAndMergeActivity::class.java)
        }

        tv_function_operator.setOnClickListener{view ->
            if (view is TextView)view.setText("类型转换成功")
            getaIntent(FunctionOperatorActivity::class.java)
        }

    }

    private fun getaIntent(cls:Class<*>?){
        val intent = Intent()
        intent.setClass(this, cls)
        startActivity(intent)
    }


}


