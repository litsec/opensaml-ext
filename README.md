![Logo](https://github.com/litsec/opensaml-ext/blob/master/docs/img/litsec-small.png)

------

# opensaml-ext

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.litsec.opensaml/opensaml3-ext/badge.svg)](https://maven-badges.herokuapp.com/maven-central/se.litsec.opensaml/opensaml3-ext)

Utility extensions for OpenSAML 3.X.

This open source package is an extension to OpenSAML 3.X that offers utility classes and interfaces such as:

* Easier initialization of the OpenSAML libraries.
* Utility methods for creating OpenSAML objects that does not require using the builder classes directly.
* Utility methods for marshalling and unmarshalling.
* Simplified support for signing XML objects.
* An abstraction for metadata handling making it easier to download and use SAML metadata.
* Spring Framework factory beans for easier integration in a Spring environment.
* A builder pattern for some commonly used objects, such as creating SAML attribute objects, entity descriptors (metadata) or authentication requests.

Java API documentation of the opensaml-ext library is found at [https://github.com/litsec/opensaml-ext/tree/master/docs/javadoc/opensaml3/0.9.3/](https://github.com/litsec/opensaml-ext/tree/master/docs/javadoc/opensaml3/0.9.3/).

*Limited support for OpenSAML 2.X is also part of the project. This library will not be updated and its purpose is to support older libraries built using OpenSAML 2.*

> Note: Some features of this library is still experimental.

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

Currently, opensaml-ext uses version `4.3.7.RELEASE` of Spring.

If you are using the OpenSAML 2.X version of the library (with limited features) use:

```
<dependency>
  <groupId>se.litsec.opensaml</groupId>
  <artifactId>opensaml2-ext</artifactId>
  <version>${opensaml-ext.version}</version>
</dependency>
```
### Initializing the OpenSAML library

The OpenSAML library needs to be initialized before it can be used. The opensaml-ext library offers a simple default way of doing this.

In order to initialize the OpenSAML library, include the following code somewhere in your application. It must be exectuted before any other code that is dependent on OpenSAML runs.

```
OpenSAMLInitializer.getInstance().initialize();
```

The OpenSAMLInitializer may also be supplied a customized parser pool. If none is assigned, a default parser pool is used.

If you are using opensaml-ext in a Spring environment you may use the `OpenSAMLInitializerBean` to initialize OpenSAML. Include the following bean declaration in your Spring XML context:

```
<bean id="openSamlInitializer" 
      class="se.litsec.opensaml.config.spring.OpenSAMLInitializerBean" 
      scope="singleton" 
      lazy-init="false" />
```


Copyright &copy; 2016-2018, [Litsec AB](http://www.litsec.se). Licensed under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).


