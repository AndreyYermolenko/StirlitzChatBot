package ru.yermolenko.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yermolenko.model.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long> {
}
