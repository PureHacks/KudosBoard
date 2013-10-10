DIGITAL KUDO BOARD
========================
Responsive web app that makes the peer recognition experience fun, exciting and seamless.

Digital Kudo Board is part of #PureHacks initiatives
https://github.com/PureHacks


NOTES FOR DEVELOPERS
========================================
The application requires Play 2.1.5 for Scala.

Front end assets are served in /web directory, not /public
You can move them to /public directory if you plan to use Play's default web server to serve up your pages

The current configuration assumes a local instance of MySQL with login root/root and an empty database called kudos.

Fetch the database DDL by running the application and browsing to
http://localhost:9000/ddl
Use this resulting script to initialize the database.

There is a Postman (http://www.getpostman.com/) collection at
https://www.getpostman.com/collections/7f539510e9464008e6e3