package ru.yermolenko.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yermolenko.model.Photo;

public interface PhotoDAO extends JpaRepository<Photo, Long> {
}
