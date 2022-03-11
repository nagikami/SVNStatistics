package com.nagi.evaluator;

import java.util.Map;

public class DefaultFileEvaluator implements FileEvaluator {
    @Override
    public double evaluateByExtension(Map<String, Double> extensionsMap, String filePath) {
        for (String s : extensionsMap.keySet()) {
            if (filePath.endsWith("." + s)) return extensionsMap.get(s);
        }
        return 1;
    }

    @Override
    public double evaluateByDirectory(Map<String, Double> directoriesMap, String filePath) {
        for (String s : directoriesMap.keySet()) {
            if (filePath.startsWith(s)) return directoriesMap.get(s);
        }
        return 1;
    }
}
