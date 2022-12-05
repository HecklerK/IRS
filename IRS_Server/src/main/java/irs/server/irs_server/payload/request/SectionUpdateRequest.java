package irs.server.irs_server.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SectionUpdateRequest {
    @NotBlank
    private Long id;
    @NotBlank
    @Size(min = 3, max = 250)
    private String header;
    @Size(max = 8000)
    private String body;
    private Boolean isVisible;
    @NotBlank
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}
