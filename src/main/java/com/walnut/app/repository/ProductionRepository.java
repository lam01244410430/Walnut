package com.walnut.app.repository;

import com.walnut.app.model.ProductionData;
import org.springframework.data.jpa.repository.JpaRepository;

// ID của ProductionData cũng là Long
public interface ProductionRepository extends JpaRepository<ProductionData, Long> {
}