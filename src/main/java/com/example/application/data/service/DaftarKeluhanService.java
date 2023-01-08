package com.example.application.data.service;

import com.example.application.data.entity.DaftarKeluhan;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class DaftarKeluhanService {

    private final DaftarKeluhanRepository repository;

    public DaftarKeluhanService(DaftarKeluhanRepository repository) {
        this.repository = repository;
    }

    public Optional<DaftarKeluhan> get(Long id) {
        return repository.findById(id);
    }

    public DaftarKeluhan update(DaftarKeluhan entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<DaftarKeluhan> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<DaftarKeluhan> list(Pageable pageable, Specification<DaftarKeluhan> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
