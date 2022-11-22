package irs.server.irs_server.models;

import org.aspectj.weaver.ast.Or;
import org.springframework.data.relational.core.sql.In;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.Instant;
import java.time.*;

@Entity
@Table(name = "sections")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 3, max = 50)
    private String header;
    @Size(max = 8000)
    private String body;
    private Boolean isVisible;
    private Instant createdOn;
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;
    private Instant changeOn;
    @ManyToOne
    @JoinColumn(name = "change_by_id")
    private User changeBy;

    public Section(){
    }

    public Section(String header, String body, Boolean isVisible, User user) {
        this.header = header;
        this.body = body;
        this.isVisible = isVisible;
        this.createdBy = user;
        this.createdOn = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Instant createdOn) {
        this.createdOn = createdOn;
    }

    public Instant getChangeOn() {
        return changeOn;
    }

    public void setChangeOn(Instant changeOn) {
        this.changeOn = changeOn;
    }

    public User getChangeBy() {
        return changeBy;
    }

    public void setChangeBy(User changeBy) {
        this.changeBy = changeBy;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}
