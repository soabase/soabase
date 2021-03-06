soabase Launcher
================

This is a simple launcher script that can be registered via init.d or some other *nix startup.

Usage
=====

    <root dir>      
        \___ your-uber-jar-nnnn.jar
        \___ bin
             \_____ service.sh
        \___ config
             \_____ jvm.properties
             \_____ *.json
             \_____ *.yaml


NOTES:

* The uber jar can optionally be in the bin directory
* The config directory is optional as is each file in it
* You can have a config.json or a config.yaml, it can have any name, but only 1 will be used
* If config.json or config.yaml are present, the uber jar is called with the arguments: "server config.nnn"

The service.sh script supports these commands:

* help - displays help text
* start - starts the application in the background and saves its pid
* run - runs the application in the foreground and saves its pid
* stop - stops the application
* restart - restarts the application if it's running
* force-start - stops the application if running (or not) and then restarts it
* status - displays the application's pid if it's running

Installing
==========

Add an init.d script that executes service.sh. NOTE: make sure service.sh has execute permissions.
 