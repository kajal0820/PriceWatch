package com.pricewatch.pricewatch.controller;
import com.pricewatch.pricewatch.model.PriceRecord;
import com.pricewatch.pricewatch.service.PriceService;
import com.pricewatch.pricewatch.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//import com.pricewatch.model.PriceRecord;
//import com.pricewatch.service.PriceService;
//import com.pricewatch.service.ScraperService;


@RestController
@RequestMapping("/api/prices")
public class PriceController {

    @Autowired
    private PriceService priceService;

    @Autowired
    private ScraperService scraperService;

    // GET all records
    @GetMapping("/all")
    public ResponseEntity<List<PriceRecord>> getAllRecords() {
        List<PriceRecord> records = priceService.getAllRecords();
        return ResponseEntity.ok(records);
    }

    // GET price history for a product
    @GetMapping("/history")
    public ResponseEntity<?> getPriceHistory(@RequestParam String url) {
        List<PriceRecord> history = priceService.getPriceHistory(url);
        if (history.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(history);
    }

    // GET latest price for a product
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestPrice(@RequestParam String url) {
        PriceRecord latest = priceService.getLatestPrice(url);
        if (latest == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(latest);
    }

    // GET price drop status
    @GetMapping("/check-drop")
    public ResponseEntity<String> checkPriceDrop(@RequestParam String url) {
        String result = priceService.checkPriceDrop(url);
        return ResponseEntity.ok(result);
    }

    // POST manually trigger a scrape (useful for testing)
    @PostMapping("/scrape")
    public ResponseEntity<?> triggerScrape() {
        PriceRecord record = scraperService.scrapeProduct();
        if (record == null) {
            return ResponseEntity.internalServerError().body("Scraping failed. Check logs.");
        }
        return ResponseEntity.ok(record);
    }
    // POST trigger full pagination scrape
    @PostMapping("/scrape-all")
    public ResponseEntity<String> triggerFullScrape() {
        scraperService.scrapeAllPages();
        return ResponseEntity.ok("Full scrape complete. Check /api/prices/all for results.");
    }
}