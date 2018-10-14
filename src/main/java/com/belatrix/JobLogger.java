package com.belatrix;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobLogger {

    private boolean logToFile;
    private boolean logToConsole;
    private boolean logMessage;
    private boolean logWarning;
    private boolean logError;
    private boolean logToDatabase;
    private Map<String, String> dbParams;
    private Logger logger;

    public JobLogger(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam, boolean logMessageParam, boolean logWarningParam, boolean logErrorParam, Map<String, String> dbParamsMap) {
        this.logger = Logger.getLogger("MyLog");
        this.logError = logErrorParam;
        this.logMessage = logMessageParam;
        this.logWarning = logWarningParam;
        this.logToDatabase = logToDatabaseParam;
        this.logToFile = logToFileParam;
        this.logToConsole = logToConsoleParam;
        this.dbParams = dbParamsMap;
    }

    public void test() {
        System.out.println("=================");
        System.out.println(logToFile);
        System.out.println(logToConsole);
        System.out.println(logToDatabase);
        System.out.println("=================");
    }

    public void logMessage(String messageText, boolean message, boolean warning, boolean error) throws RuntimeException, IOException, SQLException {
        if (messageText == null || messageText.trim().length() == 0) {
            throw new RuntimeException("The message text must be specified.");
        }
        if (!this.logToConsole && !this.logToFile && !this.logToDatabase) {
            throw new RuntimeException("Invalid configuration.");
        }
        if ((!this.logError && !this.logMessage && !this.logWarning) || (!message && !warning && !error)) {
            throw new RuntimeException("Error or Warning or Message must be specified.");
        }
        if (this.dbParams == null) {
            throw new RuntimeException("Invalid logger db params.");
        }
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.dbParams.get("userName"));
        connectionProps.put("password", this.dbParams.get("password"));

        File logFile = new File(this.dbParams.get("logFileFolder") + "/logFile.txt");
        if (!logFile.exists()) {
            Files.createFile(Paths.get(logFile.toString()));
        }

        FileHandler fh = new FileHandler(logFile.toString());
        ConsoleHandler ch = new ConsoleHandler();

        Level level = Level.OFF;
        int t = 0;
        String textToLog = "";
        if (error && this.logError) {
            textToLog = textToLog + "error " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText + "\r\n";
            level = Level.SEVERE;
            t = 2;
        }
        if (warning && logWarning) {
            textToLog = textToLog + "warning " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText + "\r\n";
            level = Level.WARNING;
            t = 3;
        }
        if (message && logMessage) {
            textToLog = textToLog + "message " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText + "\r\n";
            level = Level.INFO;
            t = 1;
        }
        if (this.logToFile) {
            this.logger.addHandler(fh);
            this.logger.log(level, textToLog);
        }
        if (this.logToConsole) {
            this.logger.addHandler(ch);
            this.logger.log(level, textToLog);
        }
        if (this.logToDatabase) {
            try (
                Connection connection = DriverManager.getConnection("jdbc:" + this.dbParams.get("dbms") + "://" + this.dbParams.get("serverName") + ":" + this.dbParams.get("portNumber") + "/", connectionProps);
                Statement statement = connection.createStatement()) {
                statement.executeUpdate("insert into Log_Values('" + textToLog + "', " + String.valueOf(t) + ")");
            }
        }
    }
}
