package com.aura.spark.core;

import com.aura.model.Log;
import org.apache.spark.api.java.JavaRDD;

public class JavaProvinceAnalysis extends LogAnalysis {

    @Override
    protected void process(JavaRDD<Log> logs) {
        // TODO add your code here
    }

    public static void main(String[] args) {
        JavaProvinceAnalysis province = new JavaProvinceAnalysis();
        province.runAnalysis(args[0]);
    }
}
