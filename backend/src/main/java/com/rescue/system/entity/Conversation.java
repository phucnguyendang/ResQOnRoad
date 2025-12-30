package com.rescue.system.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a conversation (chat session) between users
 * Based on UC101 - Messaging feature
 * A conversation is linked to a rescue request
 */
@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "rescue_request_id", nullable = false)
    private RescueRequest rescueRequest;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Account user;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Account company;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt = Instant.now();

    @Column(name = "ended_at")
    private Instant endedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ConversationStatus status = ConversationStatus.ACTIVE;

    @Column(name = "agreed_cost")
    private Double agreedCost;

    @Column(name = "cost_notes", columnDefinition = "TEXT")
    private String costNotes;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    // Constructors
    public Conversation() {
    }

    public Conversation(RescueRequest rescueRequest, Account user, Account company) {
        this.rescueRequest = rescueRequest;
        this.user = user;
        this.company = company;
        this.startedAt = Instant.now();
        this.status = ConversationStatus.ACTIVE;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RescueRequest getRescueRequest() {
        return rescueRequest;
    }

    public void setRescueRequest(RescueRequest rescueRequest) {
        this.rescueRequest = rescueRequest;
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }

    public Account getCompany() {
        return company;
    }

    public void setCompany(Account company) {
        this.company = company;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }

    public ConversationStatus getStatus() {
        return status;
    }

    public void setStatus(ConversationStatus status) {
        this.status = status;
    }

    public Double getAgreedCost() {
        return agreedCost;
    }

    public void setAgreedCost(Double agreedCost) {
        this.agreedCost = agreedCost;
    }

    public String getCostNotes() {
        return costNotes;
    }

    public void setCostNotes(String costNotes) {
        this.costNotes = costNotes;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    // Helper methods
    public void addMessage(Message message) {
        messages.add(message);
        message.setConversation(this);
    }

    public void endConversation() {
        this.endedAt = Instant.now();
        this.status = ConversationStatus.ENDED;
    }
}
