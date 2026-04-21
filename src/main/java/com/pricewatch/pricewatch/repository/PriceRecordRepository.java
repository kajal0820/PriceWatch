package com.pricewatch.pricewatch.repository;

import com.pricewatch.pricewatch.model.PriceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceRecordRepository extends JpaRepository<PriceRecord, Long> {

    List<PriceRecord> findByProductUrlOrderByScrapedAtDesc(String productUrl);
}
