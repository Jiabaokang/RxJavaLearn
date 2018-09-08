package com.jbk.rxjavalearn.rxjavaNetExample.entity

/**
 * Created on 2018/3/13 15:22
 * @author baokang.jia
 * @desc
 * @company bqjr
 */
data class TranslationBean2(
        var status: Int,
        var content:Content) {

    data class Content(
            var from: String,
            var to: String,
            var vendor: String,
            var out: String,
            var errNo: Int
    )
}