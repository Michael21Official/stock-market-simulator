package com.remitly.stockmarket.repository;

import com.remitly.stockmarket.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {

    @Query("SELECT a FROM AuditLogEntity a ORDER BY a.createdAt ASC")
    List<AuditLogEntity> findAllOrderByCreatedAtAsc();
}