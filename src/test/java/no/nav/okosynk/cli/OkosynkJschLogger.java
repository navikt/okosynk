package no.nav.okosynk.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OkosynkJschLogger implements com.jcraft.jsch.Logger {

    private final Logger logger = LoggerFactory.getLogger("com.jcraft.jsch");

    @Override
    public boolean isEnabled(final int level) {
        switch (level) {
            case DEBUG:
                return logger.isDebugEnabled();
            case INFO:
                return logger.isInfoEnabled();
            case WARN:
                return logger.isWarnEnabled();
            default:
                return logger.isErrorEnabled();
        }
    }

    @Override
    public void log(final int level, final String msg) {
        switch (level) {
            case DEBUG: {
                logger.debug(msg);
                break;
            }
            case INFO: {
                logger.info(msg);
                break;
            }
            case WARN: {
                logger.warn(msg);
                break;
            }
            default: {
                logger.error(msg);
                break;
            }
        }
    }
}
