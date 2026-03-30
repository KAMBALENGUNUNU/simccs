package com.acp.simccs.modules.crisis.model;

/**
 * Defines the operational dispatch priority of a crisis report.
 * Used to order the editor review queue (URGENT = first).
 */
public enum EPriority {
    URGENT, // Must be reviewed immediately — sort order 1
    HIGH, // Review within the hour — sort order 2
    NORMAL, // Standard review window — sort order 3
    LOW // Can be deferred — sort order 4
}
