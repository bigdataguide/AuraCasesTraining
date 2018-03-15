package com.aura.spark.core;

import com.aura.model.Log;
import org.apache.spark.api.java.JavaRDD;

public class JavaSearchAnalysis extends LogAnalysis {

    @Override
    protected void process(JavaRDD<Log> logs) {
        // TODO add your code here
    }

    public static void main(String[] args) {
        JavaSearchAnalysis search = new JavaSearchAnalysis();
        search.runAnalysis(args[0]);
    }
}
