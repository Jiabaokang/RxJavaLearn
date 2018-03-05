package com.jbk.rxjavalearn.rxjavaNetExample.entity;

/**
 * Created on 2018/3/2 17:18
 *
 * @author baokang.jia
 * @desc
 * @company bqjr
 */

public class TranslationBean {

    private int status;

    private content content;
    private static class content {
        private String from;
        private String to;
        private String vendor;
        private String out;
        private int errNo;

        @Override
        public String toString() {
            return "content{" +
                    "from='" + from + '\'' +
                    ", to='" + to + '\'' +
                    ", vendor='" + vendor + '\'' +
                    ", out='" + out + '\'' +
                    ", errNo=" + errNo +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TranslationBean{" +
                "status=" + status +
                ", content=" + content.toString() +
                '}';
    }

    //定义 输出返回数据 的方法
    public void show() {
        System.out.println(status);

        System.out.println(content.from);
        System.out.println(content.to);
        System.out.println(content.vendor);
        System.out.println(content.out);
        System.out.println(content.errNo);
    }

}
