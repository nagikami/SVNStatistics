package com.nagi.evaluator;

import java.util.Map;

public interface FileEvaluator {
    double evaluateByExtension(Map<String, Double> ExtensionsMap, String filePath);

    double evaluateByDirectory(Map<String, Double> directoriesMap, String filePath);
}
