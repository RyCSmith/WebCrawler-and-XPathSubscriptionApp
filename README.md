#Web Crawler and X-Path Subscription

Overview: Web Crawler and Web App to search and store XML/HTML documents pertaining to XPath subscriptions.

Use:

This repository contains both the Web Crawler that populates a database and a basic web app that accesses a database. Although they work in unison, their function is independent of each other.

CRAWLER

The Web Crawler is started from the command line using the XPathCrawler file. It takes three arguments [seed url from which to start crawling, local location of database, max size limit (in MB) if any]. 

It maintains a crawl queue (queue of queues) with a queue for each domain that contains all necessary info regarding the domain. The crawler parses and respects robots.txt.

The crawler will parse all XML and HTML documents, ignoring others. It then tokenizes the XPath expressions from existing accounts and attempts to match the documents to existing subscriptions. 

The crawler terminates crawling when there are not any documents left in the queue.

DATABASE

This program uses a local instance of Berkeley DB.

WEB APP

The web app uses Java Servlets and can be packaged as a .war file and deployed in a servlet container such as Jetty. From the basic interface, users can create accounts and add XPath expressions depicting subscription patterns they would like to receive. When documents are matched to the patterns they appear as text in the user's account under subscription's name link. 