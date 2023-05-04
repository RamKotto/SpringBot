package ru.saraev.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.saraev.entity.enums.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
