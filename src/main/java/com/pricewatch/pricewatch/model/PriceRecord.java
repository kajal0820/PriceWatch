package com.pricewatch.pricewatch.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "price_records")
public class PriceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private String productUrl;
    private Double price;
    private LocalDateTime scrapedAt;

    // Constructors
    public PriceRecord() {}

    public PriceRecord(String productName, String productUrl, Double price, LocalDateTime scrapedAt) {
        this.productName = productName;
        this.productUrl = productUrl;
        this.price = price;
        this.scrapedAt = scrapedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductUrl() { return productUrl; }
    public void setProductUrl(String productUrl) { this.productUrl = productUrl; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public LocalDateTime getScrapedAt() { return scrapedAt; }
    public void setScrapedAt(LocalDateTime scrapedAt) { this.scrapedAt = scrapedAt; }
}