package irs.server.irs_server.repository;

import irs.server.irs_server.models.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findAllByOrderByOrder();
}
