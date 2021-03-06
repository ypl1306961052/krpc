package krpc.rpc.bootstrap;

import java.util.ArrayList;
import java.util.List;

public class MonitorConfig {

    boolean accessLog = true;

    int logThreads = 1;
    int logQueueSize = 10000;
    String logFormatter = "simple";
    String maskFields;
    int maxRepeatedSizeToLog = 1;
    int maxFieldSizeToLog = 500;
    boolean printDefault = false;
    String auditFreeServiceIds;

    boolean printOriginalMsgName = true;
    String tags;

    String serverAddr;

    String plugins = ""; // comma seperated MonitorPlugin names
    List<String> pluginParams = new ArrayList<>(); // config MonitorPlugin if needed

    int selfCheckPort = 0; // very important, self check port, process identity
    boolean startSelfCheckPort = true; // start the port or not
    int stdSelfCheckPort = 0; // original port if multiple instances running on a single ip

    public MonitorConfig addPluginParams(String params) {
        pluginParams.add(params);
        return this;
    }

    public int getLogThreads() {
        return logThreads;
    }

    public MonitorConfig setLogThreads(int logThreads) {
        this.logThreads = logThreads;
        return this;
    }

    public int getLogQueueSize() {
        return logQueueSize;
    }

    public MonitorConfig setLogQueueSize(int logQueueSize) {
        this.logQueueSize = logQueueSize;
        return this;
    }

    public String getLogFormatter() {
        return logFormatter;
    }

    public MonitorConfig setLogFormatter(String logFormatter) {
        this.logFormatter = logFormatter;
        return this;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public MonitorConfig setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
        return this;
    }

    public String getMaskFields() {
        return maskFields;
    }

    public MonitorConfig setMaskFields(String maskFields) {
        this.maskFields = maskFields;
        return this;
    }

    public boolean isPrintDefault() {
        return printDefault;
    }

    public MonitorConfig setPrintDefault(boolean printDefault) {
        this.printDefault = printDefault;
        return this;
    }

    public int getMaxRepeatedSizeToLog() {
        return maxRepeatedSizeToLog;
    }

    public MonitorConfig setMaxRepeatedSizeToLog(int maxRepeatedSizeToLog) {
        this.maxRepeatedSizeToLog = maxRepeatedSizeToLog;
        return this;
    }

    public boolean isAccessLog() {
        return accessLog;
    }

    public MonitorConfig setAccessLog(boolean accessLog) {
        this.accessLog = accessLog;
        return this;
    }

    public String getPlugins() {
        return plugins;
    }

    public MonitorConfig setPlugins(String plugins) {
        this.plugins = plugins;
        return this;
    }

    public List<String> getPluginParams() {
        return pluginParams;
    }

    public MonitorConfig setPluginParams(List<String> pluginParams) {
        this.pluginParams = pluginParams;
        return this;
    }

    public boolean isPrintOriginalMsgName() {
        return printOriginalMsgName;
    }

    public MonitorConfig setPrintOriginalMsgName(boolean printOriginalMsgName) {
        this.printOriginalMsgName = printOriginalMsgName;
        return this;
    }

    public int getSelfCheckPort() {
        return selfCheckPort;
    }

    public MonitorConfig setSelfCheckPort(int selfCheckPort) {
        this.selfCheckPort = selfCheckPort;
        return this;
    }

    public String getTags() {
        return tags;
    }

    public MonitorConfig setTags(String tags) {
        this.tags = tags;
        return this;
    }

    public int getMaxFieldSizeToLog() {
        return maxFieldSizeToLog;
    }

    public MonitorConfig setMaxFieldSizeToLog(int maxFieldSizeToLog) {
        this.maxFieldSizeToLog = maxFieldSizeToLog;
        return this;
    }

    public String getAuditFreeServiceIds() {
        return auditFreeServiceIds;
    }

    public MonitorConfig setAuditFreeServiceIds(String auditFreeServiceIds) {
        this.auditFreeServiceIds = auditFreeServiceIds;
        return this;
    }

    public boolean isStartSelfCheckPort() {
        return startSelfCheckPort;
    }

    public MonitorConfig setStartSelfCheckPort(boolean startSelfCheckPort) {
        this.startSelfCheckPort = startSelfCheckPort;
        return this;
    }

    public int getStdSelfCheckPort() {
        return stdSelfCheckPort;
    }

    public MonitorConfig setStdSelfCheckPort(int stdSelfCheckPort) {
        this.stdSelfCheckPort = stdSelfCheckPort;
        return this;
    }
}

