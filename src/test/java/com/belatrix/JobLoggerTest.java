package com.belatrix;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class JobLoggerTest {

    private static final String MESSAGE_TEXT = "Mensaje de log.";
    private static final String MESSAGE_TEXT_NULL = null;
    private static final String MESSAGE_TEXT_EMPTY = "";

    private Map<String, String> dbParams;
    private JobLogger logToConsole;
    private JobLogger logToFile;
    private JobLogger logToDataBase;

    @Before
    public void setup() {
        dbParams = new HashMap<>();
        dbParams.put("userName", "userName");
        dbParams.put("password", "password");
        dbParams.put("dbms", "dbms");
        dbParams.put("serverName", "serverName");
        dbParams.put("portNumber", "portNumber");
        dbParams.put("logFileFolder", "/Users/rlizano/IdeaProjects/BelatrixTest");
        logToConsole = new JobLogger(false, true, false, true, true, true, dbParams);
        logToFile = new JobLogger(true, false, false, true, true, true, dbParams);
        logToDataBase = new JobLogger(false, false, true, true, true, true, dbParams);
    }

    @Test
    public void logMessageTest() {
        try {
            logToConsole.logMessage(MESSAGE_TEXT, true, false, false);
            assert (true);
        } catch (IOException | SQLException | RuntimeException e) {
            assert (false);
        }
    }

    @Test
    public void logMessageTestMessageNull() {
        try {
            logToConsole.logMessage(MESSAGE_TEXT_NULL, true, false, false);
            assert (false);
        } catch (IOException | SQLException | RuntimeException e) {
            assert (true);
        }
    }

    @Test
    public void logMessageTestMessageEmpty() {
        try {
            logToConsole.logMessage(MESSAGE_TEXT_EMPTY, true, false, false);
            assert (false);
        } catch (IOException | SQLException | RuntimeException e) {
            assert (true);
        }
    }

    @Test
    public void logMessageTestTypeLogError() {
        try {
            logToConsole.logMessage(MESSAGE_TEXT, false, false, false);
            assert (false);
        } catch (IOException | SQLException | RuntimeException e) {
            assert (true);
        }
    }

    @Test
    public void logMessageTestTypeLoggerError() {
        try {
            logToConsole = new JobLogger(false, false, false, true, true, true, dbParams);
            logToConsole.logMessage(MESSAGE_TEXT, true, false, false);
            assert (false);
        } catch (IOException | SQLException | RuntimeException e) {
            assert (true);
        }
    }

    @Test
    public void logMessageTestTypeLogMessageError() {
        try {
            logToConsole = new JobLogger(false, true, false, false, false, false, dbParams);
            logToConsole.logMessage(MESSAGE_TEXT, true, false, false);
            assert (false);
        } catch (IOException | SQLException | RuntimeException e) {
            assert (true);
        }
    }

    @Test
    public void logMessageTestDbParamsNull() {
        try {
            logToConsole = new JobLogger(false, true, false, true, true, true, null);
            logToConsole.logMessage(MESSAGE_TEXT, true, false, false);
            assert (false);
        } catch (IOException | SQLException | RuntimeException e) {
            assert (true);
        }
    }

}
