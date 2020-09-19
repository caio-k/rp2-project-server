package com.dev.springbootserver.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Table(name = "exit_log")
public class ExitLog implements Serializable {

    @EmbeddedId
    private ExitLogKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("exitId")
    @JoinColumn(name = "exit_id")
    private Exit exit;

    @NotBlank
    private String message;

    @NotBlank
    private Long timestamp;

    public ExitLog() {
    }

    public ExitLog(User user, Exit exit, String message, Long timestamp) {
        this.id = new ExitLogKey(user.getId(), exit.getId());
        this.user = user;
        this.exit = exit;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ExitLogKey getId() {
        return id;
    }

    public void setId(ExitLogKey id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Exit getExit() {
        return exit;
    }

    public void setExit(Exit exit) {
        this.exit = exit;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
