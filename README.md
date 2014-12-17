soabase
=======

A suite of [Dropwizard](http://dropwizard.io/) bundles and utilities that aid in building 
Service Oriented Applications. soabase is implementation neutral, however, default production-level implementations
are available.

Features
--------

* Service Discovery
 * Default implementation using [Apache Curator](http://curator.apache.org/curator-x-discovery/index.html)
 * Supports plugging in any desired implementation
* Load Balancing REST Client
 * Default implementations for both Jersey and Apache clients
 * Integrates with Service Discovery for retries, etc.
 * Supports plugging in any desired implementation
* Distribued, scoped attributes
 * Built in support for JDBC datasources via [Mybatis](http://mybatis.github.io/mybatis-3/)
 * Supports plugging in any desired implementation
* Jersey-based Admin APIs
 * Add Jersey resources to the Admin port
 * Built in resources for SOA features

Details
-------

For full details, see the website: [http://randgalt.github.io/soabase]
