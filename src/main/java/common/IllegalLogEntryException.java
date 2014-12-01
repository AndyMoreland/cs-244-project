package common;

/**
 * Thrown when a log entry cannot be added to the tentative set for some reason.
 * Likely the result of a primary issuing a pre-prepare message for the same sequence number.
 */
public class IllegalLogEntryException extends Exception {
}
