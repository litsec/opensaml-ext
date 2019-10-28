![Logo](https://github.com/litsec/opensaml-ext/blob/master/docs/img/litsec-small.png)

------

# opensaml-ext

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.litsec.opensaml/opensaml3-ext/badge.svg)](https://maven-badges.herokuapp.com/maven-central/se.litsec.opensaml/opensaml3-ext) 

<!-- [![Known Vulnerabilities](https://snyk.io/test/github/litsec/opensaml-ext/badge.svg?targetFile=opensaml3%2Fpom.xml)](https://snyk.io/test/github/litsec/opensaml-ext?targetFile=opensaml3%2Fpom.xml) -->

Utility extensions for OpenSAML 3.X.

This open source package is an extension to OpenSAML 3.X that offers utility classes and interfaces such as:

* Easier initialization of the OpenSAML libraries.
* Utility methods for creating OpenSAML objects that does not require using the builder classes directly.
* Utility methods for marshalling and unmarshalling.
* Simplified support for signing XML objects.
* An abstraction for metadata handling making it easier to download and use SAML metadata.
* Spring Framework factory beans for easier integration in a Spring environment.
* A builder pattern for some commonly used objects, such as creating SAML attribute objects, entity descriptors (metadata) or authentication requests.
* A framework for validation of responses and assertions.

Java API documentation of the opensaml-ext library is found at [https://litsec.github.io/opensaml-ext](https://litsec.github.io/opensaml-ext/).

Generated project information is found at [https://litsec.github.io/opensaml-ext/site](https://litsec.github.io/opensaml-ext/site).

*Limited support for OpenSAML 2.X is also part of the project. This library will not be updated and its purpose is to support older libraries built using OpenSAML 2.*

### Maven and opensaml-ext

The opensaml-ext project artifacts are published to Maven central.

Include the following snippet in your Maven POM to add opensaml-ext as a dependency for your project.

```
<dependency>
  <groupId>se.litsec.opensaml</groupId>
  <artifactId>opensaml3-ext</artifactId>
  <version>${opensaml-ext.version}</version>
</dependency>
```

If you are making use of the Spring features in opensaml-ext you need to explicitly add those dependencies. For example:

```
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-beans</artifactId>
  <version>${spring.version}</version>
</dependency>

<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-context</artifactId>
  <version>${spring.version}</version>
</dependency>
```

Currently, opensaml-ext uses version `4.3.19.RELEASE` of Spring (which is the same version as Shibboleth IdP v3.4.6).

Also, we have explicitly excluded some of the dependencies you normally get from OpenSAML since [Snyk](https://snyk.io) has reported vulnerabilities for them. In some cases we have explicitly included the "correct" version, for example for Bounce Castle's `org.bouncycastle:bcprov-jdk15on` where version 1.59 is replaced with 1.60. In other cases you have to include the dependency yourself (velocity) if you need it. Run a `mvn dependency:tree` for your project using the opensaml-ext library and verify that everything looks like you want it to.

If you are using the OpenSAML 2.X version of the library (with limited features) use:

```
<dependency>
  <groupId>se.litsec.opensaml</groupId>
  <artifactId>opensaml2-ext</artifactId>
  <version>${opensaml-ext.version}</version>
</dependency>
```
### Initializing the OpenSAML library

See <https://github.com/swedenconnect/opensaml-security-ext>.

Copyright &copy; 2016-2019, [Litsec AB](http://www.litsec.se). Licensed under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).


