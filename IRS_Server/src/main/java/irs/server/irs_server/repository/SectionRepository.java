package irs.server.irs_server.repository;

import irs.server.irs_server.models.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findAll();

    @Query(value = "SELECT COUNT(*) FROM orders", nativeQuery = true)
    long countSections();

    @Query("SELECT s FROM Order o join o.section s order by o.number")
    List<Section> findAllByOrder();

    @Query("SELECT s FROM Section s WHERE s.header like %?1% AND s.body like %?1%")
    List<Section> searchAll(String string);

    @Transactional
    @Modifying
    @Query(value = "WITH a AS(\n" +
            "SELECT ROW_NUMBER() OVER(ORDER BY number) as rn, number, section_id\n" +
            "FROM orders\n" +
            ")\n" +
            "UPDATE a SET number = case when rn >= ?2 AND rn != (select COUNT(*) from orders) then rn + 1 end\n" +
            "OPTION (MAXDOP 1)\n" +
            "UPDATE orders set number = ?2\n" +
            "where section_id = ?1", nativeQuery = true)
    void updateOrderSection(Long sectionId, Long number);

    @Query("SELECT o.number FROM Order o WHERE o.section = ?1")
    long getOrderNumber(Long sectionId);

    @Query("SELECT o.section FROM Order o WHERE o.number = ?1")
    long getSectionIdByOrderNamber(Long number);
}
