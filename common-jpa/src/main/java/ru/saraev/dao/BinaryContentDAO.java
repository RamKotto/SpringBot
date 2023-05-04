package ru.saraev.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.saraev.entity.enums.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
