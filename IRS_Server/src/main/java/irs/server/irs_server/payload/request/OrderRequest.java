package irs.server.irs_server.payload.request;

public class OrderRequest {
    private Long section_id;
    private Long number;

    public OrderRequest() {

    }
    public OrderRequest(Long section_id, Long number)
    {
        this.section_id = section_id;
        this.number = number;
    }

    public Long getSection_id() {
        return section_id;
    }

    public void setSection_id(Long section_id) {
        this.section_id = section_id;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }
}
