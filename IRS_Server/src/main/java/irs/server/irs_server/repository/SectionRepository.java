package irs.server.irs_server.repository;

import irs.server.irs_server.models.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findAll();

    @Query("SELECT s FROM Order o join o.section s order by o.number")
    List<Section> findAllByOrder();
}
