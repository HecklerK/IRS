package irs.server.irs_server.repository;

import irs.server.irs_server.models.Order;
import irs.server.irs_server.models.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findBySection(Section section);
}
