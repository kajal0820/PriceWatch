package com.pricewatch.pricewatch.service;
import com.pricewatch.pricewatch.model.PriceRecord;
import com.pricewatch.pricewatch.repository.PriceRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;




@Service
public class PriceService {

    @Autowired
    private PriceRecordRepository repository;

    // Get all price records ever saved
    public List<PriceRecord> getAllRecords() {
        return repository.findAll();
    }

    // Get price history for a specific product URL
    public List<PriceRecord> getPriceHistory(String productUrl) {
        return repository.findByProductUrlOrderByScrapedAtDesc(productUrl);
    }

    // Get latest price for a product
    public PriceRecord getLatestPrice(String productUrl) {
        List<PriceRecord> records = repository.findByProductUrlOrderByScrapedAtDesc(productUrl);
        return records.isEmpty() ? null : records.get(0);
    }

    // Detect if price dropped compared to previous record
    public String checkPriceDrop(String productUrl) {
        List<PriceRecord> records = repository.findByProductUrlOrderByScrapedAtDesc(productUrl);

        if (records.size() < 2) {
            return "Not enough data to compare yet.";
        }

        PriceRecord latest = records.get(0);
        PriceRecord previous = records.get(1);

        if (latest.getPrice() < previous.getPrice()) {
            double drop = previous.getPrice() - latest.getPrice();
            return "✅ Price dropped by " + drop + "! Current: " + latest.getPrice() + " | Previous: " + previous.getPrice();
        } else if (latest.getPrice() > previous.getPrice()) {
            return "📈 Price increased. Current: " + latest.getPrice() + " | Previous: " + previous.getPrice();
        } else {
            return "➡️ Price unchanged at " + latest.getPrice();
        }
    }
}