package com.asuscomm.scg990129.elac.hw.math272;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FormattedLogger {

    protected Logger logger;

    public FormattedLogger(Logger logger) {
        this.logger = logger;
    }

    public void logf(String format, Object... params) {
        this.logger.log(Level.INFO, String.format(format, params));
    }

    public void logf(Level level, String format, Object... params) {
        this.logger.log(level, String.format(format, params));
    }

    public void configf(String format, Object... params) {
        this.logger.config(String.format(format, params));
    }

    public void finef(String format, Object... params) {
        this.logger.fine(String.format(format, params));
    }

    public void warningf(String format, Object... params) {
        this.logger.warning(String.format(format, params));
    }

    public void severef(String format, Object... params) {
        this.logger.severe(String.format(format, params));
    }

    public void infof(String format, Object... params) {
        this.logger.info(String.format(format, params));
    }

    public void debugf(String format, Object... params) {
        this.logger.fine(String.format(format, params));
    }

    public void finestf(String format, Object... params) {
        this.logger.finest(String.format(format, params));
    }

    public void finerf(String format, Object... params) {
        this.logger.finer(String.format(format, params));
    }
}
