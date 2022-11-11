package irs.server.irs_server.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import irs.server.irs_server.models.Section;
import java.util.List;

public class SectionsResponse {
    @JsonProperty(value = "section", required = true)
    List<Section> sectionList;

    public SectionsResponse() {
    }

    public List<Section> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<Section> sectionList) {
        this.sectionList = sectionList;
    }
}
