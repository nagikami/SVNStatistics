package com.nagi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.nagi.entity.CommitFile;
import com.nagi.entity.CommitRecord;
import com.nagi.entity.User;
import com.nagi.evaluator.DefaultFileEvaluator;
import com.nagi.evaluator.FileEvaluator;
import com.nagi.filter.DefaultFileFilter;
import com.nagi.filter.FileFilter;
import org.apache.commons.lang3.StringUtils;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Collector {
    private String userName = "test";
    private String password = "test";
    private String host = "svn://192.168.56.101";
    private String project = "/myProject";
    private final String tempDir = "./";
    private DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
    private Random random = new Random();

    private String beginTimeString = "2022-01-01";
    private String endTimeString = "2022-03-12";

    private String directories = "/myProject/test,/myProject/target";

    private String extensions = "xml";

    private String directoriesMap = "demo:3";
    private String extensionsMap = "java:2,py:3";

    private SVNRepository repos;
    private ISVNAuthenticationManager authManager;

    FileFilter fileFilter = new DefaultFileFilter();
    FileEvaluator fileEvaluator = new DefaultFileEvaluator();

    private Map<String, User> users = new HashMap<>();

    public void init() throws SVNException {
        authManager = SVNWCUtil.createDefaultAuthenticationManager(new File(tempDir + "/auth"), userName, password.toCharArray());
        options.setDiffCommand("-x -w");
        repos = SVNRepositoryFactory.create(SVNURL
                .parseURIEncoded(host + project));
        repos.setAuthenticationManager(authManager);
    }

    public void collect() throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date begin = format.parse(beginTimeString);
        Date end = format.parse(endTimeString);
        long startRevision = repos.getDatedRevision(begin);
        long endRevision = repos.getDatedRevision(end);

        Collection logEntries = repos.log(new String[]{""}, null,
                startRevision, endRevision, true, true);

        for (Iterator iterator = logEntries.iterator(); iterator.hasNext();) {
            SVNLogEntry logEntry = (SVNLogEntry)iterator.next();
            User user;
            String author = logEntry.getAuthor();
            if (users.containsKey(author)) {
                user = users.get(author);
            } else {
                user = new User();
                user.setRecordList(new ArrayList<>());
                users.put(author, user);
            }
            user.setUserName(author);
            List<CommitRecord> commitRecords = user.getRecordList();
            CommitRecord commitRecord = new CommitRecord();
            commitRecord.setFilteredFiles(new HashSet<>());
            commitRecords.add(commitRecord);
            List<CommitFile> commitFiles = new ArrayList<>();
            commitRecord.setFiles(commitFiles);
            commitRecord.setRevision(logEntry.getRevision());

            int add = 0;
            int del = 0;
            if (logEntry.getChangedPaths().size() > 0) {
                for (String s : logEntry.getChangedPaths().keySet()) {
                    if ((StringUtils.isNotBlank(directories) && fileFilter.filterByDirectory(directories.split(","), s)) ||
                            (StringUtils.isNotBlank(extensions) && fileFilter.filterByExtension(extensions.split(","), s))) {
                        commitRecord.getFilteredFiles().add(s);
                        continue;
                    }
                    CommitFile commitFile = new CommitFile();
                    commitFile.setFilePath(s);
                    commitFile.setChangeType(logEntry.getChangedPaths().get(s).getType());
                    double factor = 1;
                    if (StringUtils.isNotBlank(directoriesMap)) {
                        factor *= fileEvaluator.evaluateByDirectory(getMap(directoriesMap), s);
                    }
                    if (StringUtils.isNotBlank(extensionsMap)) {
                        factor *= fileEvaluator.evaluateByExtension(getMap(extensionsMap), s);
                    }
                    commitFile.setFactor(factor);
                    int[] result = statisticsCodeAdd(getChangeLog(commitFile, logEntry.getRevision()), commitFile);
                    add += result[0];
                    del += result[1];
                    commitFiles.add(commitFile);
                }
            }
            commitRecord.setDate(logEntry.getDate());
            commitRecord.setSum(new int[] {add, del});
            commitRecord.setMessage(logEntry.getMessage());
        }
    }

    private Map<String,Double> getMap(String mapString) {
        String[] split = mapString.split(",");
        Map<String, Double> map = new HashMap<>();
        for (String s : split) {
            String[] split1 = s.split(":");
            map.put(split1[0], Double.valueOf(split1[1]));
        }
        return map;
    }

    public int[] statisticsCodeAdd(File file, CommitFile commitFile) throws Exception {
        FileReader fileReader = new FileReader(file);
        BufferedReader in = new BufferedReader(fileReader);
        int add = 0;
        int del = 0;
        String line;
        StringBuilder content = new StringBuilder(1024);
        while ((line = in.readLine()) != null) {
            content.append(line).append('\n');
        }
        if (content.length() > 0) {
            int[] result = staticOneFileChange(commitFile, content.toString());
            add = result[0];
            del = result[1];
        }
        in.close();
        fileReader.close();
        file.delete();
        commitFile.setAddCount(add);
        commitFile.setDelCount(del);
        return new int[] {add, del};
    }

    public File getChangeLog(CommitFile commitFile, long version) {
        SVNDiffClient diffClient = new SVNDiffClient(authManager, options);
        diffClient.setGitDiffFormat(true);
        File tempLogFile;
        OutputStream outputStream = null;
        String svnDiffFile;
        do {
            svnDiffFile = tempDir + "/svn_diff_file_" + version + "_" + random.nextInt(10000) + ".txt";
            tempLogFile = new File(svnDiffFile);
        } while (tempLogFile.exists());
        try {
            tempLogFile.createNewFile();
            outputStream = new FileOutputStream(svnDiffFile);
            diffClient.doDiff(SVNURL.parseURIEncoded(host + commitFile.getFilePath()),
                    SVNRevision.create(version - 1),
                    SVNURL.parseURIEncoded(host + commitFile.getFilePath()),
                    SVNRevision.create(version),
                    org.tmatesoft.svn.core.SVNDepth.UNKNOWN, true, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null)
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return tempLogFile;
    }

    public int[] staticOneFileChange(CommitFile commitFile, String fileContent) {
        char changeType = commitFile.getChangeType();
        if (changeType == 'A') {
            return countModifyLine(fileContent);
        } else if (changeType == 'M') {
            return countModifyLine(fileContent);
        }
        return new int[] {0 ,0};
    }

    public int[] countModifyLine(String content) {
        content = content.substring(content.indexOf("@@\n") + 3);
        int add = 0;
        int del = 0;
        if (StringUtils.isNotBlank(content)) {
            content = '\n' + content + '\n';
            char[] chars = content.toCharArray();
            int len = chars.length;
            boolean startPlus = false;
            boolean startDel = false;
            boolean notSpace = false;

            for (int i = 0; i < len; i++) {
                char ch = chars[i];
                if (ch == '\n') {
                    if (startPlus && notSpace) {
                        add++;
                        notSpace = false;
                    }
                    if (startDel && notSpace) {
                        del++;
                        notSpace = false;
                    }
                    if (i < len - 1 && chars[i + 1] == '+') {
                        startPlus = true;
                        i++;
                    } else {
                        startPlus = false;
                    }
                    if (i < len - 1 && chars[i + 1] == '-') {
                        startDel = true;
                        i++;
                    } else {
                        startDel = false;
                    }
                } else if ((startPlus || startDel) && ch > ' ') {
                    notSpace = true;
                }
            }
        }
        return new int[] {add, del};
    }


    public static void main(String[] args) throws Exception {
        Collector collector = new Collector();
        collector.init();
        collector.collect();
        System.out.println(JSON.toJSONString(collector.users.values(), true));
    }
}
