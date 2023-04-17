package ru.saraev.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.saraev.entity.AppUser;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {

    AppUser findAppUserByTelegramUserId(Long id);
}
