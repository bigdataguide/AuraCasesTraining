package com.aura.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

public class LogUtil {

    /**
     *
     * @param json
     * @param seconds seconds since 1970-01-01 00:00
     * @return
     */
    public static String resetSecond(String json, long seconds) {
        JSONObject obj = JSON.parseObject(json);
        obj.put("Ts", seconds);
        return obj.toJSONString();
    }

    public static String resetDay(String json, long days) {
        JSONObject obj = JSON.parseObject(json);
        if (obj.containsKey("Ts")) {
            long seconds = days * 86400 + obj.getLong("Ts") % 86400;
            obj.put("Ts", seconds);
        } else {
            obj.put("Ts", days * 86400);
        }
        return obj.toJSONString();
    }

    /**
     *
     * @param input
     * @param output
     * @param date format: 2018-03-12
     */
    public static void updateLogTime(String date, Path input, Path output) {
        long days = LocalDate.parse(date).toEpochDay();
        try (BufferedReader reader = Files.newBufferedReader(input);
             BufferedWriter writer = Files.newBufferedWriter(output)) {
            String json;
            while ((json = reader.readLine()) != null) {
                writer.write(resetDay(json, days));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: LogUtil <day> <input> <output_dir>");
            System.out.println(" e.g LogUtil 2018-03-12 data/logs/aura.log data/logs/aura20180302.log");
            System.exit(-1);
        }
        Path input = Paths.get(args[1]);
        Path output = Paths.get(args[2], args[0] + ".log");
        updateLogTime(args[0], input, output);
    }
}
