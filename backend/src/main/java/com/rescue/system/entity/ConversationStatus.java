package com.rescue.system.entity;

/**
 * Enum representing the status of a conversation
 */
public enum ConversationStatus {
    ACTIVE,          // Conversation is ongoing
    ENDED,           // Conversation ended normally
    COST_AGREED,     // Cost has been agreed upon
    COST_DISAGREED,  // Parties could not agree on cost
    CANCELLED        // Conversation was cancelled
}
