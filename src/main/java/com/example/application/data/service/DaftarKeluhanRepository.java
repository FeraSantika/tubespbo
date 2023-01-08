package com.example.application.data.service;

import com.example.application.data.entity.DaftarKeluhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DaftarKeluhanRepository
        extends
        JpaRepository<DaftarKeluhan, Long>,
        JpaSpecificationExecutor<DaftarKeluhan> {

}
