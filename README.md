# SSLTester
A set of SSL client and server testing tools

## Set up self-signed CA

OpenSSL Self-Signed Certificate

Password: test

Prerequisites

1. Locate and edit openssl.cnf
   - Set default country name
2. Set environment variable OPENSSL_CONF
   - On Windows: set OPENSSL_CONF=C:\GnuWin\share\openssl.cnf
3. Set environment variable HOME

# openssl genrsa -des3 -out server.key 1024
# openssl req -new -key server.key -out server.csr
# cp server.key server.key.org
# openssl rsa -in server.key.org -out server.key
# openssl x509 -req -days 365 -in server.csr -signkey server.key -out server.crt

http://www.akadia.com/services/ssh_test_certificate.html

# openssl pkcs12 -export -in server.crt -inkey server.key -out server.p12 -name server

Import using Portecle

https://blogs.oracle.com/blogbypuneeth/entry/steps_to_create_a_self1
https://blogs.oracle.com/blogbypuneeth/entry/steps_to_create_a_self


SSL Server

# openssl s_server -accept 4443 -www -cert server.crt -key server.key

https://www.openssl.org/docs/manmaster/apps/s_server.html
