[![Build Status](https://travis-ci.org/soabase/soabase.svg?branch=master)](https://travis-ci.org/soabase/soabase)
[![Maven Central](https://img.shields.io/maven-central/v/io.soabase/soabase.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.soabase%22%20AND%20a%3A%22soabase%22)

soabase
=======

A suite of [Dropwizard](http://dropwizard.io/) bundles and utilities that aid in building 
Service Oriented Applications. Soabase is implementation neutral. However, default production-level implementations
are provided.

Why Soabase?
------------

Dropwizard was created to create a turn key solution for Java server applications. It standardizes the things that
every Java server app needs and relieves the tedium of re-inventing the wheel every time you write a new Java server app.

Soabase extends this to Service Oriented Architecture applications. Writing clustered Java server applications
requires writing the same things over and over. Every app needs a discovery service integrated with the REST client.
They also need global configuration/attributes and global administration.

Features
--------

* Service Discovery
 * Default implementation using [Apache Curator](http://curator.apache.org/curator-x-discovery/index.html)
 * Supports deployment groups for red/black style deploys
 * Supports plugging in any desired implementation
* Load Balancing REST Client
 * Default implementations for both Jersey and Apache clients
 * Integrates with Service Discovery for retries, etc.
 * Supports plugging in any desired implementation
* Distributed, scoped attributes
 * Built in support for JDBC datasources via [Mybatis](http://mybatis.github.io/mybatis-3/) or JDBI
 * Supports plugging in any desired implementation
* Jersey-based Admin APIs
 * Add Jersey resources to the Admin port
 * Built in resources for SOA features
* Administration Console
 * Customizable/extensible
 * Monitor all instances in the cluster
 * Watch any Dropwizard metric
 * Supports LDAP or custom authentication
* Configuration Utilities
 * A flexible Dropwizard ConfigurationSourceProvider. Allows configuration to be either a string, an external file or a resource in the classpath. Special tokens are used to determine how to treat the argument.
 * A simple way of allowing shared bundles, etc. to access their custom configuration objects
* Guice Integration
 * Bundle for adding Guice support to Jersey 2.0 Resources which supports most features of Guice's ServletModule

Release Notes
-------------
https://github.com/soabase/soabase/blob/master/CHANGELOG.md

Details
-------

For full details, see the website: http://soabase.io
