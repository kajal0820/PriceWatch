# PriceWatch — Java Web Scraper & Price Tracker

A production-style backend system that scrapes product prices from e-commerce 
websites, stores historical price data, detects price drops, and exposes a 
REST API for querying results.

## Tech Stack
- Java 17, Spring Boot 3.2
- JSoup (HTML scraping)
- Spring Data JPA + MySQL
- Spring Scheduler (automated scraping)
- REST API (Spring Web)

## Features
- Scrapes product name and price from target URLs using JSoup
- Handles pagination — crawls multiple pages with polite request delays
- Retry logic (3 attempts) for failed scrape requests
- Detects and reports price drops between scrape cycles
- Global exception handler returns clean JSON errors
- Scheduled auto-scrape every 6 hours
- Full REST API for querying price history and latest prices

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/prices/scrape | Manually trigger single scrape |
| POST | /api/prices/scrape-all | Scrape all pages (pagination) |
| GET | /api/prices/all | Get all saved price records |
| GET | /api/prices/latest?url= | Get latest price for a product |
| GET | /api/prices/history?url= | Get full price history |
| GET | /api/prices/check-drop?url= | Check if price dropped |

## How to Run

1. Clone the repo
2. Create a MySQL database: `CREATE DATABASE pricewatch;`
3. Set your DB password as environment variable: `DB_PASSWORD=yourpassword`
4. Run `PricewatchApplication.java`
5. Hit `POST /api/prices/scrape-all` to start scraping

## Engineering Decisions
- **Retry logic** — scraper retries 3 times with 2s delay before failing, 
  handles intermittent network issues
- **Polite crawl delay** — 300ms sleep between requests to avoid 
  server hammering and IP blocks
- **Null-safe element parsing** — detects missing HTML elements caused 
  by site structure changes instead of silently saving bad data
- **Layered architecture** — Controller → Service → Repository separation 
  for maintainability and testability
