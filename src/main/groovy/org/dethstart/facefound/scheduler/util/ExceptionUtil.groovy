package org.dethstart.facefound.scheduler.util

import org.springframework.stereotype.Component

class ExceptionUtil {

    static String getStackTraceAsString(Throwable exception) {
        StringWriter sw = new StringWriter()
        PrintWriter pw = new PrintWriter(sw)
        exception.printStackTrace(pw)
        String stackTrace = sw.toString()
        return stackTrace
    }
}
