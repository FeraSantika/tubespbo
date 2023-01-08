package com.example.application.data.service;

import com.example.application.data.entity.UserSadari;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class UserSadariService {

    private final UserSadariRepository repository;

    public UserSadariService(UserSadariRepository repository) {
        this.repository = repository;
    }

    public Optional<UserSadari> get(Long id) {
        return repository.findById(id);
    }

    public UserSadari update(UserSadari entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<UserSadari> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<UserSadari> list(Pageable pageable, Specification<UserSadari> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
