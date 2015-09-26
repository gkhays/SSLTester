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

<pre>
   <code>
   # openssl genrsa -des3 -out server.key 1024
   # openssl req -new -key server.key -out server.csr
   # cp server.key server.key.org
   # openssl rsa -in server.key.org -out server.key
   # openssl x509 -req -days 365 -in server.csr -signkey server.key -out server.crt
   </code>
</pre>

http://www.akadia.com/services/ssh_test_certificate.html

<pre>
   # openssl pkcs12 -export -in server.crt -inkey server.key -out server.p12 -name server
</pre>

Import using Portecle [1]

![Portecle](images/Portecle1.7.jpg?raw=true "Portecle 1.7")

In the following article, we are only left with a certificate request. An additional step is required to actually create a certificate. The openssl x509 tool will probably do the job -- I'll have to experiment later.

https://blogs.oracle.com/blogbypuneeth/entry/steps_to_create_a_self1
https://blogs.oracle.com/blogbypuneeth/entry/steps_to_create_a_self

[1] Portecle 1.7 - See http://portecle.sourceforge.net/


SSL Server

<pre>
   # openssl s_server -accept 4443 -www -cert server.crt -key server.key
</pre>

https://www.openssl.org/docs/manmaster/apps/s_server.html

