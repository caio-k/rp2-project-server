package com.dev.springbootserver.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ExitLogKey implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "exit_id")
    private Long exitId;

    public ExitLogKey() {
    }

    public ExitLogKey(Long userId, Long exitId) {
        this.userId = userId;
        this.exitId = exitId;
    }

    public Long getExitId() {
        return exitId;
    }

    public void setExitId(Long exitId) {
        this.exitId = exitId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
