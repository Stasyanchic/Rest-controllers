package ru.kata.spring.boot_security.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.User;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findById(long id);

    User findByName(String name);
}
