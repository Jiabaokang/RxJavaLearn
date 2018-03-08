package com.jbk.rxjavalearn.operator.change_operator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jbk.rxjavalearn.R

class ChangeOperatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_operator)

        TestConversionOperator().mapOperator()
        TestConversionOperator().flatMapOperator()
        TestConversionOperator().concatMap()
        TestConversionOperator().buffer()
    }
}
