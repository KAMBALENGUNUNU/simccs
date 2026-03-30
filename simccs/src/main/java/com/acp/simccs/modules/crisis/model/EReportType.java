package com.acp.simccs.modules.crisis.model;

/**
 * Classifies the editorial nature of a crisis report.
 */
public enum EReportType {
    /** Time-sensitive breaking news — requires immediate attention */
    BREAKING,
    /** Exclusive story — first-hand account, not published elsewhere */
    EXCLUSIVE,
    /** Official press release submitted for publication */
    PRESS_RELEASE,
    /** In-depth feature article, less time-critical */
    FEATURE
}
