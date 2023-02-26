package by.maryana.dao;

import by.maryana.entity.RawData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawDataDAO extends JpaRepository<RawData, Long> {
}
