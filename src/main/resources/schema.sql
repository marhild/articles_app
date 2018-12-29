DROP SCHEMA IF EXISTS articles_db;

CREATE SCHEMA articles_db;

CREATE TABLE articles_db.articles (
  article_id    BIGINT PRIMARY KEY,
  created_at		DATETIME NOT NULL,
  updated_at		DATETIME NOT NULL,
  author    		VARCHAR(255) NOT NULL,
  category			VARCHAR(255) NOT NULL,
  description 	TEXT,
  content			  TEXT NOT NULL,
  title   			VARCHAR(255) NOT NULL
  );