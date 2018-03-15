package com.aura.util;

import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;

import java.util.LinkedList;
import java.util.List;

public class JavaStringUtil {

    public static List<String> extractKeywords(String content) {
        List<String> result = new LinkedList<String>();
        KeyWordComputer kwc = new KeyWordComputer(10);
        List<Keyword> words = kwc.computeArticleTfidf(content);
        for (Keyword word: words) {
            result.add(word.getName());
        }
        return result;
    }

}
