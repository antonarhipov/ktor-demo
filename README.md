# ktor-demo

ktor-demo is your new project powered by [Ktor](http://ktor.io) framework.

<img src="https://repository-images.githubusercontent.com/40136600/f3f5fd00-c59e-11e9-8284-cb297d193133" alt="Ktor" width="100" style="max-width:20%;">

Company website: example.com Ktor Version: 1.5.2 Kotlin Version: 1.4.10
BuildSystem: [Gradle with Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html)

# Ktor Documentation

Ktor is a framework for quickly creating web applications in Kotlin with minimal effort.

* Ktor project's [Github](https://github.com/ktorio/ktor/blob/master/README.md)
* Getting started with [Gradle](http://ktor.io/quickstart/gradle.html)
* Getting started with [Maven](http://ktor.io/quickstart/maven.html)
* Getting started with [IDEA](http://ktor.io/quickstart/intellij-idea.html)

Selected Features:

* [Routing](#routing-documentation-jetbrainshttpswwwjetbrainscom)
* [Authentication](#authentication-documentation-jetbrainshttpswwwjetbrainscom)
* [Authentication Basic](#authentication-basic-documentation-jetbrainshttpswwwjetbrainscom)
* [Authentication Digest](#authentication-digest-documentation-jetbrainshttpswwwjetbrainscom)
* [Authentication JWT](#authentication-jwt-documentation-jetbrainshttpswwwjetbrainscom)
* [AutoHeadResponse](#autoheadresponse-documentation-jetbrainshttpswwwjetbrainscom)
* [CachingHeaders](#cachingheaders-documentation-jetbrainshttpswwwjetbrainscom)
* [CallLogging](#calllogging-documentation-jetbrainshttpswwwjetbrainscom)
* [Compression](#compression-documentation-jetbrainshttpswwwjetbrainscom)
* [ConditionalHeaders](#conditionalheaders-documentation-jetbrainshttpswwwjetbrainscom)
* [CORS](#cors-documentation-jetbrainshttpswwwjetbrainscom)
* [DataConversion](#dataconversion-documentation-jetbrainshttpswwwjetbrainscom)
* [DefaultHeaders](#defaultheaders-documentation-jetbrainshttpswwwjetbrainscom)
* [DoubleReceive](#doublereceive-documentation-jetbrainshttpswwwjetbrainscom)
* [ForwardedHeaderSupport](#forwardedheadersupport-documentation-jetbrainshttpswwwjetbrainscom)
* [Locations](#locations-documentation-jetbrainshttpswwwjetbrainscom)
* [Raw Sockets](#raw-sockets-documentation-jetbrainshttpswwwjetbrainscom)
* [Raw Secure SSL/TLS Sockets](#raw-secure-ssl/tls-sockets-documentation-jetbrainshttpswwwjetbrainscom)
* [WebSockets](#websockets-documentation-jetbrainshttpswwwjetbrainscom)
* [PartialContent](#partialcontent-documentation-jetbrainshttpswwwjetbrainscom)
* [Status Pages](#status-pages-documentation-jetbrainshttpswwwjetbrainscom)
* [Webjars](#webjars-documentation-jetbrainshttpswwwjetbrainscom)
* [ContentNegotiation](#contentnegotiation-documentation-jetbrainshttpswwwjetbrainscom)
* [kotlinx.serialization](#kotlinx.serialization-documentation-jetbrainshttpswwwjetbrainscom)
* [GSON](#gson-documentation-jetbrainshttpswwwjetbrainscom)
* [Jackson](#jackson-documentation-jetbrainshttpswwwjetbrainscom)
* [HTML DSL](#html-dsl-documentation-jetbrainshttpswwwjetbrainscom)
* [CSS DSL](#css-dsl-documentation-jetbrainshttpswwwjetbrainscom)
* [Freemarker](#freemarker-documentation-jetbrainshttpswwwjetbrainscom)
* [Micrometer Metrics](#micrometer-metrics-documentation-jetbrainshttpswwwjetbrainscom)

## Routing Documentation ([JetBrains](https://www.jetbrains.com))

Allows to define structured routes and associated handlers.

### Description

Routing is a feature that is installed into an Application to simplify and structure page request handling. This page
explains the routing feature. Extracting information about a request, and generating valid responses inside a route, is
described on the requests and responses pages.

```application.install(Routing) {
    get("/") {
        call.respondText("Hello, World!")
    }
    get("/bye") {
        call.respondText("Good bye, World!")
    }

```

`get`, `post`, `put`, `delete`, `head` and `options` functions are convenience shortcuts to a flexible and powerful
routing system. In particular, get is an alias to `route(HttpMethod.Get, path) { handle(body) }`, where body is a lambda
passed to the get function.

### Usage

## Routing Tree

Routing is organized in a tree with a recursive matching system that is capable of handling quite complex rules for
request processing. The Tree is built with nodes and selectors. The Node contains handlers and interceptors, and the
selector is attached to an arc which connects another node. If selector matches current routing evaluation context, the
algorithm goes down to the node associated with that selector.

Routing is built using a DSL in a nested manner:

```kotlin
route("a") { // matches first segment with the value "a"
  route("b") { // matches second segment with the value "b"
     get {…} // matches GET verb, and installs a handler
     post {…} // matches POST verb, and installs a handler
  }
}
```

```kotlin
method(HttpMethod.Get) { // matches GET verb
   route("a") { // matches first segment with the value "a"
      route("b") { // matches second segment with the value "b"
         handle { … } // installs handler
      }
   }
}
```kotlin
route resolution algorithms go through nodes recursively discarding subtrees where selector didn't match.

Builder functions:
* `route(path)` – adds path segments matcher(s), see below about paths
* `method(verb)` – adds HTTP method matcher.
* `param(name, value)` – adds matcher for a specific value of the query parameter
* `param(name)` – adds matcher that checks for the existence of a query parameter and captures its value
* `optionalParam(name)` – adds matcher that captures the value of a query parameter if it exists
* `header(name, value)` – adds matcher that for a specific value of HTTP header, see below about quality

## Path
Building routing tree by hand would be very inconvenient. Thus there is `route` function that covers most of the use cases in a simple way, using path.

`route` function (and respective HTTP verb aliases) receives a `path` as a parameter which is processed to build routing tree. First, it is split into path segments by the `/` delimiter. Each segment generates a nested routing node.

These two variants are equivalent:

```kotlin
route("/foo/bar") { … } // (1)

route("/foo") {
   route("bar") { … } // (2)
}
```

### Parameters

Path can also contain parameters that match specific path segment and capture its value into `parameters` properties of
an application call:

```kotlin
get("/user/{login}") {
   val login = call.parameters["login"]
}
```

When user agent requests `/user/john` using `GET` method, this route is matched and `parameters` property will
have `"login"` key with value `"john"`.

### Optional, Wildcard, Tailcard

Parameters and path segments can be optional or capture entire remainder of URI.

* `{param?}` –- optional path segment, if it exists it's captured in the parameter
* `*` –- wildcard, any segment will match, but shouldn't be missing
* `{...}` –- tailcard, matches all the rest of the URI, should be last. Can be empty.
* `{param...}` –- captured tailcard, matches all the rest of the URI and puts multiple values for each path segment
  into `parameters` using `param` as key. Use `call.parameters.getAll("param")` to get all values.

Examples:

```kotlin
get("/user/{login}/{fullname?}") { … }
get("/resources/{path...}") { … }
```

## Quality

It is not unlikely that several routes can match to the same HTTP request.

One example is matching on the `Accept` HTTP header which can have multiple values with specified priority (quality).

```kotlin
accept(ContentType.Text.Plain) { … }
accept(ContentType.Text.Html) { … }
```

The routing matching algorithm not only checks if a particular HTTP request matches a specific path in a routing tree,
but it also calculates the quality of the match and selects the routing node with the best quality. Given the routes
above, which match on the Accept header, and given the request header `Accept: text/plain; q=0.5, text/html` will
match `text/html` because the quality factor in the HTTP header indicates a lower quality fortext/plain (default is 1.0)
.

The Header `Accept: text/plain, text/*` will match `text/plain`. Wildcard matches are considered less specific than
direct matches. Therefore the routing matching algorithm will consider them to have a lower quality.

Another example is making short URLs to named entities, e.g. users, and still being able to prefer specific pages
like `"settings"`. An example would be

* `https://twitter.com/kotlin` -– displays user `"kotlin"`
* `https://twitter.com/settings` -- displays settings page

This can be implemented like this:

```kotlin
get("/{user}") { … }
get("/settings") { … }
```

The parameter is considered to have a lower quality than a constant string, so that even if `/settings` matches both,
the second route will be selected.

### Options

No options()

## Authentication Documentation ([JetBrains](https://www.jetbrains.com))

Handle Basic and Digest HTTP Auth, Form authentication and OAuth 1a and 2

### Description

Ktor supports authentication out of the box as a standard pluggable feature. It supports mechanisms to read credentials,
and to authenticate principals. It can be used in some cases along with the sessions feature to keep the login
information between requests.

### Usage

## Basic usage

Ktor defines two concepts: credentials and principals. A principal is something that can be authenticated: a user, a
computer, a group, etc. A credential is an object that represents a set of properties for the server to authenticate a
principal: a `user/password`, an API key or an authenticated payload signature, etc. To install it, you have to call
to `application.install(Authentication)`. You have to install this feature directly to the application and it won't work
in another `ApplicationCallPipeline` like `Route`. You might still be able to call the install code inside a Route if
you have the `Application` injected in a nested DSL, but it will be applied to the application itself. Using its DSL, it
allows you to configure the authentication providers available:

```kotlin
install(Authentication) {
    basic(name = "myauth1") {
        realm = "Ktor Server"
        validate { credentials ->
            if (credentials.name == credentials.password) {
                UserIdPrincipal(credentials.name)
            } else {
                null
            }
        }
    }
}

```

After defining one or more authentication providers (named or unnamed), with the routing feature you can create a route
group, that will apply that authentication to all the routes defined in that group:

```kotlin
routing {
    authenticate("myauth1") {
        get("/authenticated/route1") {
            // ...
        }
        get("/other/route2") {
            // ...
        }
    }
    get("/") {
        // ...
    }
}

```

You can specify several names to apply several authentication providers, or none or null to use the unnamed one. You can
get the generated Principal instance inside your handler with:

```kotlin
val principal: UserIdPrincipal? = call.authentication.principal<UserIdPrincipal>()

```

In the generic, you have to put a specific type that must match the generated Principal. It will return null in the case
you provide another type. The handler won't be executed if the configured authentication fails (when returning null in
the authentication mechanism)

## Naming the AuthenticationProvider

It is possible to give arbitrary names to the authentication providers you specify, or to not provide a name at all (
unnamed provider) by not setting the name argument or passing a null. You cannot repeat authentication provider names,
and you can define just one provider without a name. In the case you repeat a name for the provider or try to define two
unnamed providers, an exception will be thrown:

```
java.lang.IllegalArgumentException: Provider with the name `authName` is already registered
```

Summarizing:

```kotlin
install(Authentication) {
    basic { // Unamed `basic` provider
        // ...
    }
    form { // Unamed `form` provider (exception, already defined a provider with name = null)
        // ...
    }
    basic("name1") { // "name1" provider
        // ...
    }
    basic("name1") { // "name1" provider (exception, already defined a provider with name = "name1")
        // ...
    }
}

```

## Skipping/Omitting Authentication providers

You can also skip an authentication based on a criteria.

```kotlin
/**
 * Authentication filters specifying if authentication is required for particular [ApplicationCall]
 * If there is no filters, authentication is required. If any filter returns true, authentication is not required.
 */
fun AuthenticationProvider.skipWhen(predicate: (ApplicationCall) -> Boolean)

```

For example, to skip a basic authentication if there is already a session, you could write:

```kotlin
authentication {
    basic {
        skipWhen { call -> call.sessions.get<UserSession>() != null }
    }
}

```

### Options

No options()

## Authentication Basic Documentation ([JetBrains](https://www.jetbrains.com))

Handle Basic authentication

### Description

Ktor supports two methods of authentication with the user and raw password as credentials: `basic` and `form`.

### Usage

```kotlin
install(Authentication) {
    basic(name = "myauth1") {
        realm = "Ktor Server"
        validate { credentials -> /*...*/ }
    }

    form(name = "myauth2") {
        userParamName = "user"
        passwordParamName = "password"
        challenge = FormAuthChallenge.Unauthorized
        validate { credentials -> /*...*/ }
    }
}

```

Both authentication providers have a method `validate` to provide a callback that must generate a Principal from given
a `UserPasswordCredential` or null for invalid credentials. That callback is marked as suspending, so that you can
validate credentials in an asynchronous fashion.

### Options

* basic
* form()

## Authentication Digest Documentation ([JetBrains](https://www.jetbrains.com))

Handle Digest authentication

### Description

Ktor supports HTTP digest authentication. It works differently than the basic/form auths.

### Usage

```kotlin
authentication {
    digest {
        val password = "Circle Of Life"
        digester = MessageDigest.getInstance("MD5")
        realm = "testrealm@host.com"
        userNameRealmPasswordDigestProvider = { userName, realm ->
            when (userName) {
                "missing" -> null
                else -> {
                    digester.reset()
                    digester.update("$userName:$realm:$password".toByteArray())
                    digester.digest()
                }
            }
        }
    }
}

```

Instead of providing a verifier, you have to provide a `userNameRealmPasswordDigestProvider` that is in charge of
returning the `HA1` part of the digest. In the case of `MD5`: `MD5("$username:$realm:$password")`. The idea is that you
can store passwords already hashed. And only return the expected hash for a specific user, or null if the user does not
exist. The callback is suspendable, so you can retrieve or compute the expected hash asynchronously, for example from
disk or a database.

```kotlin
authentication {
    val myRealm = "MyRealm"
    val usersInMyRealmToHA1: Map<String, ByteArray> = mapOf(
        // pass="test", HA1=MD5("test:MyRealm:pass")="fb12475e62dedc5c2744d98eb73b8877"
        "test" to hex("fb12475e62dedc5c2744d98eb73b8877")
    )

    digest("auth") {
        userNameRealmPasswordDigestProvider = { userName, realm ->
            usersInMyRealmToHA1[userName]
        }
    }
}

```

`HA1 (H(A1))` comes from RFC 2069 (An Extension to HTTP: Digest Access Authentication)

```
HA1=MD5(username:realm:password) <-- You usually store this.
HA2=MD5(method:digestURI)
response=MD5(HA1:nonce:HA2) <-- The client and the server sends and checks this.

```

### Options

No options()

## Authentication JWT Documentation ([JetBrains](https://www.jetbrains.com))

Handle JWT authentication

### Description

Ktor supports `JWT` (JSON Web Tokens), which is a mechanism for authenticating JSON-encoded payloads. It is useful to
create stateless authenticated APIs in the standard way, since there are client libraries for it in a myriad of
languages.

This feature will handle Authorization: `Bearer <JWT-TOKEN>`.

Ktor has a couple of classes to use the JWT Payload as `Credential` or as `Principal`.

```kotlin
class JWTCredential(val payload: Payload) : Credential
class JWTPrincipal(val payload: Payload) : Principal

```

### Usage

## Configuring server/routes:

`JWT` and `JWK` each have their own method with slightly different parameters. Both require the realm parameter, which
is used in the `WWW-Authenticate` response header.

## Using a verifier and a validator:

The verifier will use the secret to verify the signature to trust the source. You can also check the payload within
validate callback to ensure everything is right and to produce a Principal.

### application.conf:

```kotlin
jwt {
    domain = "https://jwt-provider-domain/"
    audience = "jwt-audience"
    realm = "ktor sample app"
}

```

### JWT auth:

```kotlin
val jwtIssuer = environment.config.property("jwt.domain").getString()
val jwtAudience = environment.config.property("jwt.audience").getString()
val jwtRealm = environment.config.property("jwt.realm").getString()

install(Authentication) {
    jwt {
        realm = jwtRealm
        verifier(makeJwtVerifier(jwtIssuer, jwtAudience))
        validate { credential ->
            if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
        }
    }
}

private val algorithm = Algorithm.HMAC256("secret")
private fun makeJwtVerifier(issuer: String, audience: String): JWTVerifier = JWT
        .require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

```

## Using a JWK provider:

```kotlin
fun AuthenticationPipeline.jwtAuthentication(jwkProvider: JwkProvider, issuer: String, realm: String, validate: (JWTCredential) -> Principal?)

```

```kotlin
val jwkIssuer = "https://jwt-provider-domain/"
val jwkRealm = "ktor jwt auth test"
val jwkProvider = JwkProviderBuilder(jwkIssuer)
            .cached(10, 24, TimeUnit.HOURS)
            .rateLimited(10, 1, TimeUnit.MINUTES)
            .build()
install(Authentication) {
    jwt {
        verifier(jwkProvider, jwkIssuer)
        realm = jwkRealm
        validate { credentials ->
            if (credentials.payload.audience.contains(audience)) JWTPrincipal(credentials.payload) else null
        }
    }
}

```

### Options

No options()

## AutoHeadResponse Documentation ([JetBrains](https://www.jetbrains.com))

Provide responses to HEAD requests for existing routes that have the GET verb defined

### Description

Ktor can automatically provide responses to `HEAD` requests for existing routes that have the `GET` verb defined.

## Under the covers

This feature automatically responds to `HEAD` requests by routing as if it were `GET` response and discarding the body.
Since any `FinalContent` produced by the system has lazy content semantics, it does not incur in any performance costs
for processing a `GET` request with a body.

### Usage

To enable automatic `HEAD` responses, install the `AutoHeadResponse` feature

```kotlin
fun Application.main() {
  // ...
  install(AutoHeadResponse)
  // ...
}

```

### Options

No options()

## CachingHeaders Documentation ([JetBrains](https://www.jetbrains.com))

Send the headers Cache-Control and Expires used by clients and proxies to cache requests

### Description

The `CachingOptions` feature adds the ability to send the headers `Cache-Control` and `Expires` used by clients and
proxies to cache requests in an easy way.

### Usage

The basic feature is installed just like many others, but for it to do something, you have to define options blocks
transforming `outputContent` to `CachingOptions` using for example:

```kotlin
install(CachingHeaders) {
    options { outgoingContent ->
        when (outgoingContent.contentType?.withoutParameters()) {
            ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
            else -> null
        }
    }
}

```

The options configuration method, allows you to define code to optionally select a `CachingOptions` from a
provided `outgoingContent: OutgoingContent`. You can, for example, use the `Content-Type` of the outgoing message to
determine which `Cache-Control` to use.

## CachingOptions and CacheControl

The options high order function requires you to return a `CachingOption` that describes a `CacheControl` plus an
optional expiring time:

```kotlin
data class CachingOptions(val cacheControl: CacheControl? = null, val expires: ZonedDateTime? = null)

sealed class CacheControl(val visibility: Visibility?) {
    enum class Visibility { Public, Private }

    class NoCache(visibility: Visibility?) : CacheControl(visibility)
    class NoStore(visibility: Visibility?) : CacheControl(visibility)
    class MaxAge(val maxAgeSeconds: Int, val proxyMaxAgeSeconds: Int? = null, val mustRevalidate: Boolean = false, val proxyRevalidate: Boolean = false, visibility: Visibility? = null) : CacheControl(visibility)
}

```

If you have several options, that would append several `Cache-Control` headers per each matching option.

### Options

No options()

## CallLogging Documentation ([JetBrains](https://www.jetbrains.com))

Logs client requests

### Description

You might want to log client requests: and the Call Logging feature does just that. It uses
the `ApplicationEnvironment.log(LoggerFactory.getLogger("Application"))` that uses `slf4j` so you can easily configure
the output. For more information on logging in Ktor, please check the logging in the ktor page.

### Usage

## Basic usage

The basic unconfigured feature logs every request using the level `TRACE`:

```kotlin
install(CallLogging)

```

## Configuring

This feature allows you to configure the log level and filtering the requests that are being logged:

```kotlin
install(CallLogging) {
    level = Level.INFO
    filter { call -> call.request.path().startsWith("/section1") }
    filter { call -> call.request.path().startsWith("/section2") }
    // ...
}

```

The filter method keeps an allow list of filters. If no filters are defined, everything is logged. And if there are
filters, if any of them returns true, the call will be logged.

In the example, it will log both: `/section1/*` and `/section2/*` requests.

## MDC

The `CallLogging` feature supports `MDC` (Mapped Diagnostic Context) from `slf4j` to associate information as part of
the request.

When installing the `CallLogging`, you can configure a parameter to associate to the request with the mdc method. This
method requires a key name, and a function provider. The context would be associated (and the providers will be called)
as part of the `Monitoring` pipeline phase.

```kotlin
install(CallLogging) {
    mdc(name) { // call: ApplicationCall ->
        "value"
    }
    // ...
}

```

### Options

No options()

## Compression Documentation ([JetBrains](https://www.jetbrains.com))

Compress outgoing content using gzip, deflate or custom encoder and thus reduce the size of the response

### Description

`Compression` feature adds the ability to compress outgoing content using `gzip`, `deflate` or ``custom` encoder and
thus reduce the size of the response.

### Usage

## Installation

```kotlin
install(Compression)

```

## Configuration

When the configuration block is omitted, the default configuration is used. It includes the following encoders:

* gzip

* deflate

* identity

If you want to select specific encoders you need to provide a configuration block:

```kotlin
install(Compression) {
    gzip()
}

```

Each encoder can be configured with a priority and some conditions:

```kotlin
install(Compression) {
    gzip {
        priority = 1.0
    }
    deflate {
        priority = 10.0
        minimumSize(1024) // condition
    }
}

```

Encoders are sorted by specified quality in an `Accept-Encoding` header in the HTTP request, and then by specified
priority. First encoder that satisfies all conditions wins.

In the example above when `Accept-Encoding` doesn't specify quality, `gzip` will be selected for all contents less
than `1K` in size, and all the rest will be encoded with `deflate` encoder.

Some typical conditions are readily available:

* `minimumSize` – minimum size of the response to compress

* `matchContentType` – one or more content types that should be compressed

* `excludeContentType` – do not compress these content types

You can also use a custom condition by providing a predicate:

```kotlin
gzip {
    condition {
        parameters["e"] == "1"
    }
}

```

## Security with HTTPS

HTTPS with any kind of compression is vulnerable to the BREACH attack. This kind of attack allows a malicious attacker
to guess a secret (like a session, an auth token, a password, or a credit card) from an encrypted `HTTPS` page in less
than a minute.

You can mitigate this attack by:

* Completely turn off HTTP compression (which might affect performance).
* Not placing user input (`GET`, `POST` or `Header/Cookies` parameters) as part of the response (either `Headers`
  or `Bodies`) mixed with secrets (including a `Set-Cookie` with a `session_id`).
* Add a random amount of bytes to the output for example in an html page, you can just
  add `<!-- 100~500 random_bytes !-->` making it much harder to guess the secret for an attacker in a reasonable time.
* Ensure that your website is completely `HTTPS` and has `HSTS` enabled, and adding a conditional header checking the
  Referrer page. (If you have a single page without `HTTPS`, the malicious attacker can use that page to inject code
  using the same domain as Referrer).
* Adding `CSRF` protection to your pages.

```kotlin
application.install(Compression) {
    gzip {
        condition {
            // @TODO: Check: this is only effective if your website is completely HTTPS and has HSTS enabled.
            request.headers[HttpHeaders.Referrer]?.startsWith("https://my.domain/") == true
        }
    }
}

```

## Extensibility

You can provide your own encoder by implementing the `CompressionEncoder` interface and providing a configuration
function. Since content can be provided as a `ReadChannel` or `WriteChannel`, it should be able to compress in both
ways. See `GzipEncoder` as an example of an encoder.

### Options

* gzip
* deflate
* custom
  ()

## ConditionalHeaders Documentation ([JetBrains](https://www.jetbrains.com))

Avoids sending content if the client already has the same content using ETag or LastModified

### Description

ConditionalHeaders feature adds the ability to avoid sending content if the client already has the same content. It does
so by checking the `ETag` or `LastModified` properties of the `Resource` or `FinalContent` that are sent and comparing
these properties to what client indicates it is having. If the conditions allow it, the entire content is not sent and
a `"304 Not Modified"` response is sent instead.

### Usage

## Configuration

You can install and use `ConditionalHeaders` without additional configuration:

```kotlin
install(ConditionalHeaders)

```

It also allows to configure a lambda to fetch a version list from the generated `OutgoingContent` passed as parameter of
the lambda:

```kotlin
install(ConditionalHeaders) {
    version { content -> listOf(EntityTagVersion("tag1")) }
}

```

## Extensibility

`Version` interface implementations are attached to the `Resource` instances, and you can return custom implementations
with your own logic. Please note that `FinalContent` is only checked for ETag and LastModified headers.

### Options

* version()

## CORS Documentation ([JetBrains](https://www.jetbrains.com))

Enable Cross-Origin Resource Sharing (CORS)

### Description

Ktor by default provides an interceptor for implementing proper support for Cross-Origin Resource Sharing (`CORS`).

### Usage

## Basic usage:

First of all, install the `CORS` feature into your application.

```kotlin
fun Application.main() {
  ...
  install(CORS)
  ...
}
```

The default configuration to the CORS feature handles only `GET`, `POST` and `HEAD` HTTP methods and the following
headers:

```
HttpHeaders.Accept
HttpHeaders.AcceptLanguages
HttpHeaders.ContentLanguage
HttpHeaders.ContentType
```

## Advanced usage:

Here is an advanced example that demonstrates most of CORS-related API functions

```kotlin
fun Application.main() {
  ...
  install(CORS)
  {
    method(HttpMethod.Options)
    header(HttpHeaders.XForwardedProto)
    anyHost()
    host("my-host")
    // host("my-host:80")
    // host("my-host", subDomains = listOf("www"))
    // host("my-host", schemes = listOf("http", "https"))
    allowCredentials = true
    allowNonSimpleContentTypes = true
    maxAge = Duration.ofDays(1)
  }
  ...
}
```

### Options

* `method("HTTP_METHOD")` : Includes this method to the white list of Http methods to use CORS.
* `header("header-name")` : Includes this header to the white list of headers to use CORS.
* `exposeHeader("header-name")` : Exposes this header in the response.
* `exposeXHttpMethodOverride()` : Exposes `X-Http-Method-Override` header in the response
* `anyHost()` : Allows any host to access the resources
* `host("hostname")` : Allows only the specified host to use `CORS`, it can have the port number, a list of subDomains
  or the supported schemes.
* `allowCredentials` : Includes `Access-Control-Allow-Credentials` header in the response
* `allowNonSimpleContentTypes`: Inclues `Content-Type` request header to the white list for values other than simple
  content types.
* `maxAge`: Includes `Access-Control-Max-Age` header in the response with the given max age()

## DataConversion Documentation ([JetBrains](https://www.jetbrains.com))

Allows to serialize and deserialize a list of values (used by the Locations feature)

### Description

`DataConversion` is a feature that allows to serialize and deserialize a list of values.

By default, it handles primitive types and enums, but it can also be configured to handle additional types.

If you are using the `Locations` feature and want to support custom types as part of its parameters, you can add new
custom converters with this service.

### Usage

## Basic Installation

Installing the DataConversion is pretty easy, and it should be cover primitive types:

```kotlin
install(DataConversion)
```

## Adding Converters

The `DataConversion` configuration, provide a `convert<T>` method to define type conversions. Inside, you have to
provide a decoder and an encoder with the `decode` and `encode` methods accepting callbacks.

* decode callback: `converter: (values: List<String>, type: Type) -> Any? Accepts values, a list of strings)`
  representing repeated values in the URL, for example, `a=1&a=2`, and accepts the type to convert to. It should return
  the decoded value.
* encode callback: `converter: (value: Any?) -> List<String>` Accepts an arbitrary value, and should return a list of
  strings representing the value. When returning a list of a single element, it will be serialized as `key=item1`. For
  multiple values, it will be serialized in the query string as: `samekey=item1&samekey=item2`.

For example:

```kotlin
install(DataConversion) {
    convert<Date> { // this: DelegatingConversionService
        val format = SimpleDateFormat.getInstance()

        decode { values, _ -> // converter: (values: List<String>, type: Type) -> Any?
            values.singleOrNull()?.let { format.parse(it) }
        }

        encode { value -> // converter: (value: Any?) -> List<String>
            when (value) {
                null -> listOf()
                is Date -> listOf(SimpleDateFormat.getInstance().format(value))
                else -> throw DataConversionException("Cannot convert $value as Date")
            }
        }
    }
}
```

Another potential use is to customize how a specific enum is serialized. By default enums are serialized and
de-serialized using its `.name` in a case-sensitive fashion. But you can for example serialize them as lower case and
deserialize them in a case-insensitive fashion:

```
enum class LocationEnum {
    A, B, C
}

@Location("/") class LocationWithEnum(val e: LocationEnum)

@Test fun `location class with custom enum value`() = withLocationsApplication {
    application.install(DataConversion) {
        convert(LocationEnum::class) {
            encode { if (it == null) emptyList() else listOf((it as LocationEnum).name.toLowerCase()) }
            decode { values, type -> LocationEnum.values().first { it.name.toLowerCase() in values } }
        }
    }
    application.routing {
        get<LocationWithEnum> {
            call.respondText(call.locations.resolve<LocationWithEnum>(LocationWithEnum::class, call).e.name)
        }
    }

    urlShouldBeHandled("/?e=a", "A")
    urlShouldBeHandled("/?e=b", "B")
}
```

## Accessing the Service

You can easily access the `DataConversion` service, from any call with:

```kotlin
val dataConversion = call.conversionService
```

## The ConversionService Interface

```kotlin
interface ConversionService {
    fun fromValues(values: List<String>, type: Type): Any?
    fun toValues(value: Any?): List<String>
}
```

```kotlin
class DelegatingConversionService(private val klass: KClass<*>) : ConversionService {
    fun decode(converter: (values: List<String>, type: Type) -> Any?)
    fun encode(converter: (value: Any?) -> List<String>)
}
```

### Options

No options()

## DefaultHeaders Documentation ([JetBrains](https://www.jetbrains.com))

This feature adds a default set of headers to HTTP responses

### Description

The `DefaultHeaders` feature adds the standard `Server` and `Date` headers into each response. Moreover, you can provide
additional default headers and override the `Server` header.

### Usage

## Installation

To install the `DefaultHeaders` feature, pass it to the `install` function in the application initialization code. This
can be the `main` function ...

```kotlin
import io.ktor.features.*
// ...
fun Application.main() {
  install(DefaultHeaders)
  // ...
}
```

... or a specified `module`:

```kotlin
import io.ktor.features.*
// ...
fun Application.module() {
    install(DefaultHeaders)
    // ...
}
```

The `DefaultHeaders` feature adds the `Server` and `Date` headers into each response. If necessary, you can override
the `Server`, as described in `Override Headers` section.

## Add Additional Headers

To customize a list of default headers, pass a desired header to `install` by using the `header(name, value)` function.
The name parameter accepts an `HttpHeaders` value, for example:

```kotlin
install(DefaultHeaders) {
    header(HttpHeaders.ETag, "7c876b7e")
}
```

To add a custom header, pass its name as a string value:

```kotlin
install(DefaultHeaders) {
    header("Custom-Header", "Some value")
}
```

## Override Headers

To override the `Server` header, use a corresponding `HttpHeaders` value:

```kotlin
install(DefaultHeaders) {
    header(HttpHeaders.Server, "Custom")
}
```

Note that the `Date` header is cached due to performance reasons and cannot be overridden by using `DefaultHeaders`. If
you need to override it, do not install the `DefaultHeaders` feature and use route interception instead.

## Customize Headers for Specific Routes

If you need to add headers for a specific route only, you can append desired headers into a response. The code snippet
below shows how to do this for the `/order` request:

```kotlin
get("/order") {
    call.response.headers.append(HttpHeaders.ETag, "7c876b7e")
}
```

You can learn more about routing in Ktor from [Routing in Ktor](https://ktor.io/docs/routing-in-ktor.html).

### Options

* `header` -- specify default value for the given header()

## DoubleReceive Documentation ([JetBrains](https://www.jetbrains.com))

Allows ApplicationCall.receive several times

### Description

DoubleReceive feature provides the ability to invoke ApplicationCall.receive several times with no
RequestAlreadyConsumedException exception. This usually makes sense when a feature is consuming a request body so a
handler is unable to receive it again.

### Usage

Install DoubleReceive feature into the ApplicationCall

```kotlin
install(DoubleReceive)

```

After that you can receive from a call multiple times and every invocation may return the same instance.

```kotlin
val first = call.receiveText()
val theSame = call.receiveText()

```

Types that could be always received twice with this feature are: ByteArray, String and Parameters and all types provided
by ContentNegotiation feature (for example, objects deserialized from JSON payloads).

Receiving different types from the same call is not guaranteed to work without receiveEntireContent but may work in some
specific cases. For example, receiving a text after receiving a byte array always works.

When receiveEntireContent is enabled, then receiving different types should always work. Also double receive of a
channel or stream works as well. However, receive executes the whole receive pipeline from the beginning so all content
transformations and converters are executed every time that may be slower than with the option disabled.##Custom types
If a custom content transformation is installed (for example, by intercepting receive pipeline), then a transformed
value couldn't be re-received without receiveEntireContent option by default. However it is possible to mark a
transformed value object as reusable by specifying reusableValue option:

```kotlin
val converted = .... // convert somehow from a request payload
proceedWith(ApplicationReceiveRequest(receive.typeInfo, converted, reusableValue = true))

```

### Options

*receiveEntireContent : When enabled, for every request the whole content will be received and stored as a byte array.
This is useful when completely different types need to be received. You also can receive streams and channels. Note that
enabling this causes the whole receive pipeline to be executed for every further receive pipeline.()

## ForwardedHeaderSupport Documentation ([JetBrains](https://www.jetbrains.com))

This feature allows you to handle reverse proxy headers to get information about the original request when it’s behind a
proxy.

### Description

This feature allows you to handle reverse proxy headers to get information about the original request when it's behind a
proxy.

* ForwardedHeaderSupport handles the standard `Forwarded` header (`RFC 7239`)
* `XForwardedHeaderSupport` handles the non-standard (but standard de-facto) `X-Forwarded-Host/X-Forwarded-Server`
  , `X-Forwarded-For`, `X-Forwarded-By`, `X-Forwarded-Proto/X-Forwarded-Protocol` and `X-Forwarded-SSL/Front-End-Https`

### Usage

## Basic usage

These features don't require any special configuration. You can install any of the two depending on your reverse proxy,
but since the standard is the `Forwarded` header, you should favor it whenever possible.

```kotlin
install(ForwardedHeaderSupport)
```

or

```kotlin
install(XForwardedHeaderSupport)
```

## Request information

### The proxy request information

You can read the raw or local request information, read from the received normal headers and socket properties, that
correspond to the proxy request using the `request.local` property:

```kotlin
val scheme = request.local.scheme
val version = request.local.version
val port = request.local.port
val host = request.local.host
val uri = request.local.uri
val method = request.local.method
val remoteHost = request.local.remoteHost
```

### The original request information

You can read the original request information, read from the `Forwarded` or `X-Forwarded-*` headers with fallback to the
raw headers, that corresponds to original client request using the `request.origin` property:

```kotlin
val scheme = request.origin.scheme // Determined from X-Forwarded-Proto / X-Forwarded-Protocol / X-Forwarded-SSL
val version = request.origin.version
val port = request.origin.port // Determined from X-Forwarded-Host / X-Forwarded-Server
val host = request.origin.host // Determined from X-Forwarded-Host / X-Forwarded-Server
val uri = request.origin.uri
val method = request.origin.method
val remoteHost = request.origin.remoteHost // Determined from X-Forwarded-For
```

In the cases where you need the `X-Forwarded-By` (the interface used for the socket), you can access the
raw `X-Forwarded` properties with:

```kotlin
val forwardedValues: List<ForwardedHeaderSupport.ForwardedHeaderValue> = call.attributes[ForwardedHeaderSupport.ForwardedParsedKey]
```

```kotlin
data class ForwardedHeaderValue(val host: String?, val by: String?, val forParam: String?, val proto: String?, val others: Map<String, String>)
```

## Header description

The standard `Forwarded` header looks like this:

`Forwarded: by=<identifier>; for=<identifier>; host=<host>; proto=<http|https>`

* `by` - The interface where the request came in to the proxy server.
* `for` - The client that initiated the request and subsequent proxies in a chain of proxies.
* `host` - The Host request header field as received by the proxy.
* `proto` - Indicates which protocol was used to make the request (typically `"http"` or `"https"`).

### Options

No options()

## Locations Documentation ([JetBrains](https://www.jetbrains.com))

Allows to define route locations in a typed way

### Description

Ktor provides a mechanism to create routes in a typed way, for both: constructing URLs and reading the parameters.

### Usage

## Installation

The Locations feature doesn't require any special configuration:

```kotlin
install(Locations)
```

## Defining route classes

For each typed route you want to handle, you need to create a class (usually a data class) containing the parameters
that you want to handle.

The parameters must be of any type supported by the `Data Conversion` feature. By default, you can use `Int`, `Long`
, `Float`, `Double`, `Boolean`, `String`, enums and `Iterable` as parameters.

### URL parameters

That class must be annotated with `@Location` specifying a path to match with placeholders between curly brackets `{`
and `}`. For example: `{propertyName}`. The names between the curly braces must match the properties of the class.

```kotlin
@Location("/list/{name}/page/{page}")
data class Listing(val name: String, val page: Int)
```

* Will match: `/list/movies/page/10`
* Will construct: `Listing(name = "movies", page = 10)`

### GET parameters

If you provide additional class properties that are not part of the path of the `@Location`, those parameters will be
obtained from the `GET`'s query string or `POST` parameters:

```kotlin
@Location("/list/{name}")
data class Listing(val name: String, val page: Int, val count: Int)
```

* Will match: `/list/movies?page=10&count=20`
* Will construct: `Listing(name = "movies", page = 10, count = 20)`

## Defining route handlers

Once you have defined the classes annotated with `@Location`, this feature artifact exposes new typed methods for
defining route handlers: `get`, `options`, `header`, `post`, `put`, `delete` and `patch`.

```kotlin
routing {
    get<Listing> { listing ->
        call.respondText("Listing ${listing.name}, page ${listing.page}")
    }
}
```

## Building URLs

You can construct URLs to your routes by calling `application.locations.href` with an instance of a class annotated
with `@Location`:

```kotlin
val path = application.locations.href(Listing(name = "movies", page = 10, count = 20))
```

So for this class, `path` would be `"/list/movies?page=10&count=20"`.

```kotlin
@Location("/list/{name}") data class Listing(val name: String, val page: Int, val count: Int)
```

If you construct the URLs like this, and you decide to change the format of the URL, you will just have to update
the `@Location` path, which is really convenient.

## Subroutes with parameters

You have to create classes referencing to another class annotated with `@Location` like this, and register them
normally:

```kotlin
routing {
    get<Type.Edit> { typeEdit -> // /type/{name}/edit
        // ...
    }
    get<Type.List> { typeList -> // /type/{name}/list/{page}
        // ...
    }
}
```

To obtain parameters defined in the superior locations, you just have to include those property names in your classes
for the internal routes. For example:

```kotlin
@Location("/type/{name}") data class Type(val name: String) {
	// In these classes we have to include the `name` property matching the parent.
	@Location("/edit") data class Edit(val parent: Type)
	@Location("/list/{page}") data class List(val parent: Type, val page: Int)
}
```

### Options

No options()

## Raw Sockets Documentation ([JetBrains](https://www.jetbrains.com))

Adds Raw Socket support for listening and connecting to tcp and udp sockets

### Description

In addition to HTTP handling for the server and the client, Ktor supports client and server, TCP and UDP raw sockets. It
exposes a suspending API that uses NIO under the hoods.

### Usage

In order to create either server or client sockets, you have to use the `aSocket` builder, with a
mandatory `ActorSelectorManager`: `aSocket(selector)`. For example: `aSocket(ActorSelectorManager(Dispatchers.IO))`.

Then use:

* `val socketBuilder = aSocket(selector).tcp()` for a builder using TCP sockets
* `val socketBuilder = aSocket(selector).udp()` for a builder using UDP sockets

This returns a `SocketBuilder` that can be used to:

* `val serverSocket = aSocket(selector).tcp().bind(address)` to listen to an address (for servers)
* `val clientSocket = aSocket(selector).tcp().connect(address)` to connect to an address (for clients)

If you need to control the dispatcher used by the sockets, you can instantiate a selector, that uses, for example, a
cached thread pool:

```kotlin
val exec = Executors.newCachedThreadPool()
val selector = ActorSelectorManager(exec.asCoroutineDispatcher())
val tcpSocketBuilder = aSocket(selector).tcp()
```

Once you have a `socket` open by either binding or connecting the builder, you can read from or write to the socket, by
opening read/write channels:

```kotlin
val input : ByteReadChannel  = socket.openReadChannel()
val output: ByteWriteChannel = socket.openWriteChannel(autoFlush = true)
```

When creating a server socket, you have to bind to a specific `SocketAddress` to get a `ServerSocket`:

```kotlin
val server = aSocket(selector).tcp().bind(InetSocketAddress("127.0.0.1", 2323))
```

The server socket has an `accept` method that returns, one at a time, a connected socket for each incoming connection
pending in the backlog:

```kotlin
val socket = server.accept()
```

When creating a socket client, you have to connect to a specific SocketAddress to get a Socket:

```kotlin
val socket = aSocket(selector).tcp().connect(InetSocketAddress("127.0.0.1", 2323))
```

### Options

No options()

## Raw Secure SSL/TLS Sockets Documentation ([JetBrains](https://www.jetbrains.com))

Adds Raw Socket support for listening and connecting to tcp and udp sockets with secure sockets

### Description

Ktor supports secure sockets. To enable them you will need to include the `io.ktor:ktor-network-tls:$ktor_version`
artifact, and call the `.tls()` to a connected socket.

### Usage

Connect to a secure socket:

```kotlin
runBlocking {
    val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("google.com", 443)).tls()
    val w = socket.openWriteChannel(autoFlush = false)
    w.write("GET / HTTP/1.1\r\n")
    w.write("Host: google.com\r\n")
    w.write("\r\n")
    w.flush()
    val r = socket.openReadChannel()
    println(r.readUTF8Line())
}
```

You can adjust a few optional parameters for the TLS connection:

```kotlin
suspend fun Socket.tls(
        trustManager: X509TrustManager? = null,
        randomAlgorithm: String = "NativePRNGNonBlocking",
        serverName: String? = null,
        coroutineContext: CoroutineContext = Dispatchers.IO
): Socket
```

### Options

No options()

## WebSockets Documentation ([JetBrains](https://www.jetbrains.com))

Adds WebSockets support for bidirectional communication with the client

### Description

This feature adds WebSockets support to Ktor. WebSockets are a mechanism to keep a bi-directional real-time ordered
connection between the server and the client. Each message from this channel is called Frame: a frame can be a text or
binary message, or a close or ping/pong message. Frames can be marked as incomplete or final.

### Usage

## Installation

In order to use the `WebSockets` functionality you first have to install it:

```kotlin
install(WebSockets)
```

You can adjust a few parameters when installing if required:

```kotlin
install(WebSockets) {
    pingPeriod = Duration.ofSeconds(60) // Disabled (null) by default
    timeout = Duration.ofSeconds(15)
    maxFrameSize = Long.MAX_VALUE // Disabled (max value). The connection will be closed if surpassed this length.
    masking = false
}
```

## Basic usage

Once installed, you can define the `webSocket` routes for the `routing` feature:

Instead of the short-lived normal route handlers, webSocket handlers are meant to be long-lived. And all the relevant
WebSocket methods are suspended so that the function will be suspended in a non-blocking way while receiving or sending
messages.

`webSocket` methods receive a callback with a `WebSocketSession` instance as the receiver. That interface defines
an `incoming` (`ReceiveChannel`) property and an `outgoing` (`SendChannel`) property, as well as a close method.

### Usage as an suspend actor

```kotlin
routing {
    webSocket("/") { // websocketSession
        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> {
                    val text = frame.readText()
                    outgoing.send(Frame.Text("YOU SAID: $text"))
                    if (text.equals("bye", ignoreCase = true)) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    }
                }
            }
        }
    }
}
```

### Usage as a Channel

Since the `incoming` property is a `ReceiveChannel`, you can use it with its stream-like interface:

```kotlin
routing {
    webSocket("/") { // websocketSession
        for (frame in incoming.mapNotNull { it as? Frame.Text }) {
            val text = frame.readText()
            outgoing.send(Frame.Text("YOU SAID $text"))
            if (text.equals("bye", ignoreCase = true)) {
                close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
            }
        }
    }
}
```

### Options

* `pingPeriod` -- duration between pings or null to disable pings.
* `timeout` -- write/ping timeout after that a connection will be closed
* `maxFrameSize` -- maximum frame that could be received or sent
* `masking` -- whether masking need to be enabled (useful for security)()

## PartialContent Documentation ([JetBrains](https://www.jetbrains.com))

Handles requests with the Range header. Generating Accept-Ranges and the Content-Range headers and slicing the served
content when required.

### Description

This feature adds support for handling Partial Content requests: requests with the `Range` header. It intercepts the
generated response adding the `Accept-Ranges` and the `Content-Range` header and slicing the served content when
required.

Partial Content is well-suited for streaming content or resume partial downloads with download managers, or in
unreliable networks.

It is especially useful for the `Static Content` Feature.

This feature only works with `HEAD` and `GET` requests. And it will return a `405 Method Not Allowed` if the client
tries to use the `Range` header with other methods.

It disables compression when serving ranges.

It is only enabled for responses that define the `Content-Length`. And it:

Removes the `Content-Length` header

Adds the `Accept-Ranges` header

Adds the Content-Range header with the requested Ranges

Serves only the requested slice of the content

### Usage

To install the `PartialContent` feature with the default configuration:

```kotlin
import io.ktor.features.*

fun Application.main() {
    // ...
    install(PartialContent)
    // ...
}
```

### Options

* `maxRangeCount` -- Maximum number of ranges that will be accepted from a HTTP request. If the HTTP request specifies
  more ranges, they will all be merged into a single range.()

## Status Pages Documentation ([JetBrains](https://www.jetbrains.com))

Allow to respond to thrown exceptions.

### Description

The `StatusPages` feature allows Ktor applications to respond appropriately to any failure state.

### Usage

## Installation

This feature is installed using the standard application configuration:

```kotlin
fun Application.main() {
    install(StatusPages)
}
```

## Exceptions

The exception configuration can provide simple interception patterns for calls that result in a thrown exception. In the
most basic case, a `500` HTTP status code can be configured for any exceptions.

```kotlin
install(StatusPages) {
    exception<Throwable> { cause ->
        call.respond(HttpStatusCode.InternalServerError)
    }
}
```

More specific responses can allow for more complex user interactions.

```kotlin
install(StatusPages) {
    exception<AuthenticationException> { cause ->
        call.respond(HttpStatusCode.Unauthorized)
    }
    exception<AuthorizationException> { cause ->
        call.respond(HttpStatusCode.Forbidden)
    }
}
```

These customizations can work well when paired with custom status code responses, e.g. providing a login page when a
user has not authenticated.

Each call is only caught by a single exception handler, the closest exception on the object graph from the thrown
exception. When multiple exceptions within the same object hierarchy are handled, only a single one will be executed.

```kotlin
install(StatusPages) {
    exception<IllegalStateException> { cause ->
        fail("will not reach here")
    }
    exception<ClosedFileSystemException> {
        throw IllegalStateException()
    }
}
intercept(ApplicationCallPipeline.Fallback) {
    throw ClosedFileSystemException()
}
```

Single handling also implies that recursive call stacks are avoided. For example, this configuration would result in the
created IllegalStateException propagating to the client.

```kotlin
install(StatusPages) {
    exception<IllegalStateException> { cause ->
        throw IllegalStateException("")
    }
}
```

## Logging Exceptions

It is important to note that adding the handlers above will "swallow" the exceptions generated by your routes. In order
to log the actual errors generated, you can either log the `cause` manually, or simply re-throw it as shown below:

```kotlin
install(StatusPages) {
    exception<Throwable> { cause ->
        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error")
        throw cause
    }
}
```

## Status

The `status` configuration provides a custom actions for status responses from within the application. Below is a basic
configuration that provides information about the HTTP status code within the response text.

```kotlin
install(StatusPages) {
    status(HttpStatusCode.NotFound) {
        call.respond(TextContent("${it.value} ${it.description}", ContentType.Text.Plain.withCharset(Charsets.UTF_8), it))
    }
}
```

## StatusFile

While the `status` configuration provides customizable actions on the response object, the more common solution is to
provide an error HTML page that visitors will see on an error or authorization failure. The `statusFile` configuration
provides that type of functionality.

```kotlin
install(StatusPages) {
    statusFile(HttpStatusCode.NotFound, HttpStatusCode.Unauthorized, filePattern = "error#.html")
}
```

This will resolve two resources from the classpath.

* On a `404`, it will return `error404.html`.
* On a `401`, it will return `error401.html`.

The `statusFile` configuration replaces any `#` character with the value of the status code within the list of
configured statuses.

## Redirections using StatusPages

When doing redirections by executing `call.respondRedirect("/moved/here", permanent = true)`, the rest of the callee
function is executed. So when doing redirections inside guard clauses, you have to return the function.

```kotlin
routing {
    get("/") {
        if (condition) {
            return@get call.respondRedirect("/invalid", permanent = false)
        }
        call.respondText("Normal response")
    }
}
```

Other frameworks, use exceptions on redirect, so the normal flow is broken and you can execute redirections in guard
clauses or subfunctions without having to worry about returning in all the subfunction chain. You can use the
StatusPages feature to simulate this:

```kotlin
fun Application.module() {
    install(StatusPages) {
        exception<HttpRedirectException> { e ->
            call.respondRedirect(e.location, permanent = e.permanent)
        }
    }
    routing {
        get("/") {
            if (condition) {
                redirect("/invalid", permanent = false)
            }
            call.respondText("Normal response")
        }
    }
}

class HttpRedirectException(val location: String, val permanent: Boolean = false) : RuntimeException()
fun redirect(location: String, permanent: Boolean = false): Nothing = throw HttpRedirectException(location, permanent)
```

### Options

* `exceptions` - Configures response based on mapped exception classes
* `status` - Configures response to status code value
* `statusFile` - Configures standard file response from classpath()

## Webjars Documentation ([JetBrains](https://www.jetbrains.com))

Allows you to package your assets such as javascript libraries and css as part of your uber-jar.

### Description

This feature enable serving static content provided by `webjars`. It allows you to package your assets such as
javascript libraries and css as part of your uber-jar.

### Usage

## Installation

```kotlin
install(Webjars) {
    path = "assets" //defaults to /webjars
    zone = ZoneId.of("EST") //defaults to ZoneId.systemDefault()
}
```

This configures the feature to serve any webjars assets on the `/assets/` path. The `zone` argument configures the
correct time zone to be used with the `Last-Modified` header to support caching (only if `Conditional Headers` feature
is also installed).

## Versioning support

Webjars allow developers to change the versions of the dependencies without requiring a change on the path used to load
them on your templates.

Let's assume you have imported `org.webjars:jquery:3.2.1`, you can use the following html code to import it:

```html
<head>
  <script src="/webjars/jquery/jquery.js"></script>
</head>
```

You don't need to specify a version, should you choose to update your dependencies you don't need to modify your
templates.

### Options

* `path` -- URL path for serving webjars
* `zone` -- configures the correct time zone to be used with the `Last-Modified` header to support caching (only
  if `Conditional Headers` feature is also installed).()

## ContentNegotiation Documentation ([JetBrains](https://www.jetbrains.com))

Provides automatic content conversion according to Content-Type and Accept headers.

### Description

The `ContentNegotiation` feature serves two primary purposes:

* Negotiating media types between the client and server. For this, it uses the `Accept` and `Content-Type` headers.
* Serializing/deserializing the content in the specific format, which is provided by either the
  built-in `kotlinx.serialization` library or external ones, such as `Gson` and `Jackson`, amongst others.

### Usage

## Installation

To install the `ContentNegotiation` feature, pass it to the `install` function in the application initialization code.
This can be the `main` function ...

```kotlin
import io.ktor.features.*
// ...
fun Application.main() {
  install(ContentNegotiation)
  // ...
}
```

... or a specified `module`:

```kotlin
import io.ktor.features.*
// ...
fun Application.module() {
    install(ContentNegotiation)
    // ...
}
```

## Register a Converter

To register a converter for a specified `Content-Type`, you need to call the register method. In the example below, two
custom converters are registered to deserialize `application/json` and `application/xml` data:

```kotlin
install(ContentNegotiation) {
    register(ContentType.Application.Json, CustomJsonConverter())
    register(ContentType.Application.Xml, CustomXmlConverter())
}
```

### Built-in Converters

Ktor provides the set of built-in converters for handing various content types without writing your own logic:

* `Gson` for JSON

* `Jackson` for JSON

* `kotlinx.serialization` for JSON, Protobuf, CBOR, and so on

See a corresponding topic to learn how to install the required dependencies, register, and configure a converter.

## Receive and Send Data

### Create a Data Class

To deserialize received data into an object, you need to create a data class, for example:

```kotlin
data class Customer(val id: Int, val firstName: String, val lastName: String)
```

If you use `kotlinx.serialization`, make sure that this class has the `@Serializable` annotation:

```kotlin
import kotlinx.serialization.Serializable

@Serializable
data class Customer(val id: Int, val firstName: String, val lastName: String)
```

### Receive Data

To receive and convert a content for a request, call the `receive` method that accepts a data class as a parameter:

```kotlin
post("/customer") {
    val customer = call.receive<Customer>()
}
```

The `Content-Type` of the request will be used to choose a converter for processing the request. The example below shows
a sample HTTP client request containing JSON data that will be converted to a `Customer` object on the server side:

```kotlin
post http://0.0.0.0:8080/customer
Content-Type: application/json

{
  "id": 1,
  "firstName" : "Jet",
  "lastName": "Brains"
}
```

### Send Data

To pass a data object in a response, you can use the `respond` method:

```kotlin
post("/customer") {
    call.respond(Customer(1, "Jet", "Brains"))
}
```

In this case, Ktor uses the `Accept` header to choose the required converter.

## Implement a Custom Converter

In Ktor, you can write your own converter for serializing/deserializing data. To do this, you need to implement
the `ContentConverter` interface:

```kotlin
interface ContentConverter {
    suspend fun convertForSend(context: PipelineContext<Any, ApplicationCall>, contentType: ContentType, value: Any): Any?
    suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any?
}
```

Take a look at
the [GsonConverter](https://github.com/ktorio/ktor/blob/master/ktor-features/ktor-gson/jvm/src/io/ktor/gson/GsonSupport.kt)
class as an implementation example.

### Options

No options()

## kotlinx.serialization Documentation ([JetBrains](https://www.jetbrains.com))

Handles JSON serialization using kotlinx.serialization library

### Description

ContentNegotiation allows you to use content converters provided by the `kotlinx.serialization` library. This library
supports `JSON`, `CBOR`, `ProtoBuf`, and other formats.

### Usage

## Register the JSON Converter

To register the JSON converter in your application, call the `json` method:

```kotlin
import io.ktor.serialization.*

install(ContentNegotiation) {
    json()
}
```

Inside the `json` method, you can access
the [JsonBuilder](https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-json/kotlinx-serialization-json/kotlinx.serialization.json/-json-builder/index.html)
API, for example:

```kotlin
install(ContentNegotiation) {
    json(Json {
        prettyPrint = true
        isLenient = true
        // ...
    })
}
```

## Register an Arbitrary Converter

To register an arbitrary converter from the kotlinx.serialization library (such as Protobuf or CBOR), call
the `serialization` method and pass two parameters:

* The required `ContentType` value.
* An object of the class implementing the required encoder/decoder.

For example, you can register
the [Cbor](https://kotlin.github.io/kotlinx.serialization/kotlinx-serialization-cbor/kotlinx-serialization-cbor/kotlinx.serialization.cbor/-cbor/index.html)
converter in the following way:

```kotlin
install(ContentNegotiation) {
    serialization(ContentType.Application.Cbor, Cbor.Default)
}
```

### Options

No options()

## GSON Documentation ([JetBrains](https://www.jetbrains.com))

Handles JSON serialization using GSON library

### Description

`ContentNegotiation` provides the built-in `Gson` converter for handing JSON data in your application.

### Usage

To register the Gson converter in your application, call the `gson` method:

```kotlin
import io.ktor.gson.*

install(ContentNegotiation) {
    gson()
}
```

Inside the `gson` block, you can access
the [GsonBuilder](https://www.javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/com/google/gson/GsonBuilder.html)
API, for example:

```kotlin
install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            disableHtmlEscaping()
            // ...
        }
}
```

To learn how to receive and send data,
see [Receive and Send Data](https://ktor.io/docs/json-feature.html#receive_send_data).

### Options

No options()

## Jackson Documentation ([JetBrains](https://www.jetbrains.com))

Handles JSON serialization using Jackson library

### Description

ContentNegotiation provides the built-in `Jackson` converter for handing JSON data in your application.

### Usage

To register the `Jackson` converter in your application, call the `jackson` method:

```kotlin
import io.ktor.jackson.*

install(ContentNegotiation) {
    jackson()
}
```

Inside the `jackson` block, you can access
the [ObjectMapper](https://fasterxml.github.io/jackson-databind/javadoc/2.9/com/fasterxml/jackson/databind/ObjectMapper.html)
API, for example:

```kotlin
install(ContentNegotiation) {
    jackson {
        enable(SerializationFeature.INDENT_OUTPUT)
        dateFormat = DateFormat.getDateInstance()
        // ...
    }
}
```

### Options

No options()

## HTML DSL Documentation ([JetBrains](https://www.jetbrains.com))

Generate HTML using Kotlin code like a pure-core template engine

### Description

HTML DSL integrates the `kotlinx.html` library into Ktor and allows you to respond to a client with HTML blocks. With
HTML DSL, you can write pure HTML in Kotlin, interpolate variables into views, and even build complex HTML layouts using
templates.

### Usage

## Send HTML in Response

To send an HTML response, call the `ApplicationCall.respondHtml` method inside the required route:

```kotlin
get("/") {
    val name = "Ktor"
    call.respondHtml {
        head {
            title {
                +name
            }
        }
        body {
            h1 {
                +"Hello from $name!"
            }
        }
    }
}
```

In this case, the following HTML will be sent to the client:

```html
<head>
    <title>Ktor</title>
</head>
<body>
    <h1>Hello from Ktor!</h1>
</body>
```

To learn more about generating HTML using kotlinx.html, see
the [kotlinx.html wiki](https://github.com/Kotlin/kotlinx.html/wiki).

## Templates

In addition to generating plain HTML, Ktor provides a template engine that can be used to build complex layouts. You can
create a hierarchy of templates for different parts of an HTML page, for example, a root template for the entire page,
child templates for a page header and footer, and so on. Ktor exposes the following API for working with templates:

1. To respond with an HTML built based on a specified template, call the `ApplicationCall.respondHtmlTemplate` method.
2. To create a template, you need to implement the `Template` interface and override the `Template.apply` method
   providing HTML.
3. Inside a created template class, you can define placeholders for different content types:

* `Placeholder` is used to insert the content. `PlaceholderList` can be used to insert the content that appears multiple
  times (for example, list items).
* `TemplatePlaceholder` can be used to insert child templates and create nested layouts.

### Example

Let's see the example of how to create a hierarchical layout using templates. Imagine we have the following HTML:

```html
<body>
<h1>Ktor</h1>
<article>
    <h2>Hello from Ktor!</h2>
    <p>Kotlin Framework for creating connected systems.</p>
</article>
</body>
```

We can split the layout of this page into two parts:

* A root layout template for a page header and a child template for an article.
* A child template for the article content.

Let's implement these layouts step-by-step:

1. Call the `respondHtmlTemplate` method and pass a template class as a parameter. In our case, this is
   the `LayoutTemplate` class that should implement the `Template` interface:

```kotlin
get("/") {
    call.respondHtmlTemplate(LayoutTemplate()) {
        // ...
    }
}
```

Inside the block, we will be able to access a template and specify its property values. These values will substitute
placeholders specified in a template class. We'll create LayoutTemplate and define its properties in the next step.

2. A root layout template will look in the following way:

```kotlin
class LayoutTemplate: Template<HTML> {
    val header = Placeholder<FlowContent>()
    val content = TemplatePlaceholder<ContentTemplate>()
    override fun HTML.apply() {
        body {
            h1 {
                insert(header)
            }
            insert(ContentTemplate(), content)
        }
    }
}
```

The class exposes two properties:

* The `header` property specifies a content inserted within the h1 tag.
* The `content` property specifies a child template for article content.

3. A child template will look as follows:

```kotlin
class ContentTemplate: Template<FlowContent> {
    val articleTitle = Placeholder<FlowContent>()
    val articleText = Placeholder<FlowContent>()
    override fun FlowContent.apply() {
        article {
            h2 {
                insert(articleTitle)
            }
            p {
                insert(articleText)
            }
        }
    }
}
```

This template exposes the `articleTitle` and `articleText` properties, whose values will be inserted inside
the `article`.

4. Now we are ready to send HTML built using the specified property values:

```kotlin
get("/") {
    call.respondHtmlTemplate(LayoutTemplate()) {
        header {
            +"Ktor"
        }
        content {
            articleTitle {
                +"Hello from Ktor!"
            }
            articleText {
                +"Kotlin Framework for creating connected systems."
            }
        }
    }
}
```

### Options

No options()

## CSS DSL Documentation ([JetBrains](https://www.jetbrains.com))

Generate CSS using Kotlin code

### Description

`CSS DSL` extends `HTML DSL` and allows you to author stylesheets in Kotlin by using the `kotlin-css` wrapper.

### Usage

To send a CSS response, you need to extend `ApplicationCall` by adding the `respondCss` method to serialize a stylesheet
into a string and send it to the client with the `CSS` content type:

```kotlin
suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
   this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
```

Then, you can provide CSS inside the required [route](Routing_in_Ktor.md):

```kotlin
get("/styles.css") {
    call.respondCss {
        body {
            backgroundColor = Color.darkBlue
            margin(0.px)
        }
        rule("h1.page-title") {
            color = Color.white
        }
    }
}
```

Finally, you can use the specified CSS for an HTML document created with [HTML DSL](html_dsl.md):

```kotlin
get("/html-dsl") {
    call.respondHtml {
        head {
            link(rel = "stylesheet", href = "/styles.css", type = "text/css")
        }
        body {
            h1(classes = "page-title") {
                +"Hello from Ktor!"
            }
        }
    }
}
```

### Options

No options()

## Freemarker Documentation ([JetBrains](https://www.jetbrains.com))

Serve HTML content using Apache's FreeMarker template engine

### Description

Ktor allows you to use `FreeMarker` templates as views within your application by installing the `Freemarker` feature.

### Usage

## Installation

To install the `FreeMarker` feature, pass it to the `install` function in the application initialization code. This can
be the `main` function ...

```kotlin
import io.ktor.features.*
// ...
fun Application.main() {
  install(FreeMarker)
  // ...
}
```

... or a specified `module`:

```kotlin
import io.ktor.features.*
// ...
fun Application.module() {
    install(FreeMarker)
    // ...
}
```

Inside the `install` block, you can configure the desired `TemplateLoader` for loading `FreeMarker` templates.

## Configure FreeMarker

### Configure Template Loading

To load templates, you need to assign the desired `TemplateLoader` type to the `templateLoader` property. For example,
the code snippet below enables Ktor to look up templates in the templates package relative to the current classpath:

```kotlin
import io.ktor.freemarker.*

install(FreeMarker) {
    templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
}
```

### Send a Template in Response

Imagine you have the `index.ftl` template in `resources/templates`:

```html
<html>
    <body>
        <h1>Hello, ${user.name}!</h1>
    </body>
</html>
```

A data model for a user looks as follows:

```kotlin
data class User(val id: Int, val name: String)
```

To use the template for the specified `route`, pass `FreeMarkerContent` to the `call.respond` method in the following
way:

```kotlin
get("/index") {
    val sampleUser = User(1, "John")
    call.respond(FreeMarkerContent("index.ftl", mapOf("user" to sampleUser)))
}
```

### Options

* `templateLoader` -- sets an
  Apache [TemplateLoading](https://freemarker.apache.org/docs/pgui_config_templateloading.html) object that defines how
  and where to load templates from.()

## Micrometer Metrics Documentation ([JetBrains](https://www.jetbrains.com))

Enables Micrometer metrics in your Ktor server application.

### Description

The [MicrometerMetrics](https://api.ktor.io/%ktor_version%/io.ktor.metrics.micrometer/-micrometer-metrics/index.html)
feature enables [Micrometer](https://micrometer.io/docs) metrics in your Ktor server application and allows you to
choose the required monitoring system, such as Prometheus, JMX, Elastic, and so on. By default, Ktor exposes metrics for
monitoring HTTP requests and a set of low-level metrics for [monitoring the JVM][micrometer_jvm_metrics]. You can
customize these metrics or create new ones.

### Usage

### Install MicrometerMetrics

<var name="feature_name" value="MicrometerMetrics"/>
<include src="lib.md" include-id="install_feature"/>

#### Exposed Metrics

Ktor exposes the following metrics for monitoring HTTP requests:

* `ktor.http.server.requests.active`: a [gauge](https://micrometer.io/docs/concepts#_gauges) that counts the amount of
  concurrent HTTP requests. This metric doesn't provide any tags.
* `ktor.http.server.requests`: a [timer](https://micrometer.io/docs/concepts#_timers) for measuring the time of each
  request. This metric provides a set of tags for monitoring request data, including `address` for a requested
  URL, `method` for an HTTP method, `route` for a Ktor route handling requests, and so on.

> The metric names may be [different](https://micrometer.io/docs/concepts#_naming_meters) depending on the configured monitoring system.

In addition to HTTP metrics, Ktor exposes a set of metrics for [monitoring the JVM](#jvm_metrics).

### Create a Registry

After installing `MicrometerMetrics`, you need to create
a [registry for your monitoring system](https://micrometer.io/docs/concepts#_registry) and assign it to
the [registry](https://api.ktor.io/%ktor_version%/io.ktor.metrics.micrometer/-micrometer-metrics/-configuration/registry.html)
property. In the example below, the `PrometheusMeterRegistry` is created outside the `install` block to have the
capability to reuse this registry in different [route handlers](Routing_in_Ktor.md):

```kotlin
import io.ktor.features.*
// ...
fun Application.module() {
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
    }
}
```

### Prometheus: Expose a Scrape Endpoint

If you use Prometheus as a monitoring system, you need to expose an HTTP endpoint to the Prometheus scraper. In Ktor,
you can do this in the following way:

1. Create a dedicated [route](Routing_in_Ktor.md) that accepts GET requests by the required address (`/metrics` in the
   example below).
1. Use `call.respond` to send scraping data to Prometheus.

```kotlin
fun Application.module() {
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
        // ...
    }

    routing {
        get("/metrics") {
            call.respond(appMicrometerRegistry.scrape())
        }
    }
}
```

### Options

The `MicrometerMetrics` feature provides various configuration options that can be accessed
using [MicrometerMetrics.Configuration](https://api.ktor.io/%ktor_version%/io.ktor.metrics.micrometer/-micrometer-metrics/-configuration/index.html)
.

### Timers

To customize tags for each timer, you can use the `timers` function that is called for each request:

```kotlin
install(MicrometerMetrics) {
    // ...
    timers { call, exception ->
        tag("region", call.request.headers["regionId"])
    }
}
```

### Distribution Statistics

You configure [distribution statistics](https://micrometer.io/docs/concepts#_configuring_distribution_statistics) using
the `distributionStatisticConfig` property, for example:

```kotlin
install(MicrometerMetrics) {
    // ...
    distributionStatisticConfig = DistributionStatisticConfig.Builder()
                .percentilesHistogram(true)
                .maximumExpectedValue(Duration.ofSeconds(20).toNanos())
                .sla(
                    Duration.ofMillis(100).toNanos(),
                    Duration.ofMillis(500).toNanos()
                )
                .build()
}
```

### JVM and System Metrics

In addition to [HTTP metrics](#ktor_metrics), Ktor exposes a set of metrics
for [monitoring the JVM][micrometer_jvm_metrics]. You can customize a list of these metrics using the `meterBinders`
property, for example:

```kotlin
install(MicrometerMetrics) {
    // ...
    meterBinders = listOf(
        JvmMemoryMetrics(),
        JvmGcMetrics(),
        ProcessorMetrics()
    )
}
```

You can also assign an empty list to disable these metrics at all.()

# Reporting Issues / Support

Please use [our issue tracker](https://youtrack.jetbrains.com/issues/KTOR) for filing feature requests and bugs. If
you'd like to ask a question, we recommmend [StackOverflow](https://stackoverflow.com/questions/tagged/ktor) where
members of the team monitor frequently.

There is also community support on the [Kotlin Slack Ktor channel](https://app.slack.com/client/T09229ZC6/C0A974TJ9)

# Reporting Security Vulnerabilities

If you find a security vulnerability in Ktor, we kindly request that you reach out to the JetBrains security team via
our [responsible disclosure process](https://www.jetbrains.com/legal/terms/responsible-disclosure.html).

# Contributing

Please see [the contribution guide](CONTRIBUTING.md) and the [Code of conduct](CODE_OF_CONDUCT.md) before contributing.

TODO: contribution of features guide (link)