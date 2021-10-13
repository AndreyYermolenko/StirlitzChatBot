package ru.yermolenko.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yermolenko.model.ERole;
import ru.yermolenko.model.Role;

import java.util.Optional;

@Repository
public interface RoleDAO extends JpaRepository<Role, Long> {
  Optional<Role> findByName(ERole name);
}
