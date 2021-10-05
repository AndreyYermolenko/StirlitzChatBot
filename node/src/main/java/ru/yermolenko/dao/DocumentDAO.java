package ru.yermolenko.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yermolenko.model.Document;

public interface DocumentDAO extends JpaRepository<Document, Long> {
}
