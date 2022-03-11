package com.nagi.filter;

public interface FileFilter {
    boolean filterByExtension(String[] extensions, String filePath);

    boolean filterByDirectory(String[] directories, String filePath);
}
