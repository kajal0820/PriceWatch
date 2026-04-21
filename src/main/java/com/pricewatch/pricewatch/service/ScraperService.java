package com.pricewatch.pricewatch.service;

import com.pricewatch.pricewatch.model.PriceRecord;
import com.pricewatch.pricewatch.repository.PriceRecordRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class ScraperService {

    @Autowired
    private PriceRecordRepository repository;

    // Target product URL
    private static final String TARGET_URL = "https://books.toscrape.com/catalogue/a-light-in-the-attic_1000/index.html";

    // Runs every 6 hours automatically
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 2000;

    @Scheduled(fixedRate = 21600000)
    public PriceRecord scrapeProduct() {
        int attempt = 0;

        while (attempt < MAX_RETRIES) {
            try {
                attempt++;
                System.out.println("🔄 Scrape attempt " + attempt + " of " + MAX_RETRIES);

                Document doc = Jsoup.connect(TARGET_URL)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .timeout(10000)
                        .get();

                Element nameElement = doc.selectFirst("h1");
                String productName = (nameElement != null) ? nameElement.text() : "Unknown";

                Element priceElement = doc.selectFirst("p.price_color");
                String priceText = (priceElement != null) ? priceElement.text() : "0";

                // Defensive check — if price element missing, site structure may have changed
                if (priceElement == null) {
                    System.err.println("⚠️ Price element not found — site structure may have changed");
                    return null;
                }

                double price = parsePrice(priceText);

                PriceRecord record = new PriceRecord(productName, TARGET_URL, price, LocalDateTime.now());
                repository.save(record);

                System.out.println("✅ Scraped: " + productName + " | Price: " + price + " | At: " + LocalDateTime.now());
                return record;

            } catch (IOException e) {
                System.err.println("❌ Attempt " + attempt + " failed: " + e.getMessage());

                if (attempt < MAX_RETRIES) {
                    try {
                        System.out.println("⏳ Retrying in " + RETRY_DELAY_MS + "ms...");
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        System.err.println("🚨 All " + MAX_RETRIES + " scrape attempts failed.");
        return null;
    }
    private double parsePrice(String priceText) {
        try {
            String cleaned = priceText.replaceAll("[^0-9.]", "");
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Could not parse price: " + priceText);
            return 0.0;
        }
    }
    private static final String BASE_URL = "https://books.toscrape.com/catalogue/";
    private static final String CATALOGUE_URL = "https://books.toscrape.com/catalogue/page-1.html";

    public void scrapeAllPages() {
        String nextPageUrl = CATALOGUE_URL;
        int pageCount = 0;
        int maxPages = 5; // limit to 5 pages for now

        while (nextPageUrl != null && pageCount < maxPages) {
            try {
                pageCount++;
                System.out.println("📄 Scraping page " + pageCount + ": " + nextPageUrl);

                Document doc = Jsoup.connect(nextPageUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .timeout(10000)
                        .get();

                // Each book is inside <article class="product_pod">
                for (Element book : doc.select("article.product_pod")) {

                    // Product name is in the <a> tag's title attribute
                    Element nameEl = book.selectFirst("h3 a");
                    String productName = (nameEl != null) ? nameEl.attr("title") : "Unknown";

                    // Build full product URL
                    String relativeUrl = (nameEl != null) ? nameEl.attr("href") : "";
                    String productUrl = BASE_URL + relativeUrl.replace("../", "");

                    // Price
                    Element priceEl = book.selectFirst("p.price_color");
                    String priceText = (priceEl != null) ? priceEl.text() : "0";
                    double price = parsePrice(priceText);

                    // Save each book
                    PriceRecord record = new PriceRecord(productName, productUrl, price, LocalDateTime.now());
                    repository.save(record);
                    System.out.println("  ✅ Saved: " + productName + " | £" + price);

                    // Polite delay between saves — avoid hammering the server
                    Thread.sleep(300);
                }

                // Find the "next" button and get its URL
                Element nextBtn = doc.selectFirst("li.next a");
                nextPageUrl = (nextBtn != null) ? "https://books.toscrape.com/catalogue/" + nextBtn.attr("href") : null;

            } catch (IOException e) {
                System.err.println("❌ Failed on page " + pageCount + ": " + e.getMessage());
                break;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("🏁 Pagination scrape complete. Pages scraped: " + pageCount);
    }
}