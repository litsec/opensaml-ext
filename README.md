![Logo](https://github.com/litsec/opensaml-ext/blob/master/docs/img/litsec-small.png)

------

# opensaml-ext

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.litsec.opensaml/opensaml3-ext/badge.svg)](https://maven-badges.herokuapp.com/maven-central/se.litsec.opensaml/opensaml4-ext) 

Utility extensions for OpenSAML.

**Note**: Support for OpenSAML 2.X and 3.X has been discontinued. The last release of eidas-opensaml supporting OpenSAML 3.X is 1.4.4.

This open source package is an extension to OpenSAML that offers utility classes and interfaces such as:

* Utility methods for creating OpenSAML objects that does not require using the builder classes directly.
* Utility methods for marshalling and unmarshalling.
* Simplified support for signing XML objects.
* An abstraction for metadata handling making it easier to download and use SAML metadata.
* Spring Framework factory beans for easier integration in a Spring environment.
* A builder pattern for some commonly used objects, such as creating SAML attribute objects, entity descriptors (metadata) or authentication requests.
* A framework for validation of responses and assertions.

Java API documentation of the opensaml-ext library is found at [https://litsec.github.io/opensaml-ext](https://litsec.github.io/opensaml-ext/).

Generated project information is found at [https://litsec.github.io/opensaml-ext/site](https://litsec.github.io/opensaml-ext/site).

### Maven and opensaml-ext

The opensaml-ext project artifacts are published to Maven central.

Include the following snippet in your Maven POM to add opensaml-ext as a dependency for your project.

```
<dependency>
  <groupId>se.litsec.opensaml</groupId>
  <artifactId>opensaml4-ext</artifactId>
  <version>${opensaml-ext.version}</version>
</dependency>
```
### Initializing the OpenSAML library

See <https://github.com/swedenconnect/opensaml-security-ext>.

Copyright &copy; 2016-2021, [Litsec AB](http://www.litsec.se). Licensed under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).


