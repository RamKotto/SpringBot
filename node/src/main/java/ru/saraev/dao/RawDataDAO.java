package ru.saraev.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.saraev.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long> {

}
