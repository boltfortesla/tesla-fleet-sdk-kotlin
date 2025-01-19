# Kotlin Tesla Fleet SDK

An implementation of the [Tesla Fleet API](https://developer.tesla.com/docs/fleet-api) in Kotlin.

![Logo](https://avatars.githubusercontent.com/u/160552401)

## Features

- Supports both Signed Commands for Model 3/Y and 2021+ Model S and X Vehicles, in addition to pre-2021 Model S and X Vehicles
  - Switching between communication protocols is handled automatically
- Handles transient network and vehicle communication errors with a configurable built-in retry-with-backoff mechanism
- Kotlin first, API is entirely async, utilizing Coroutines


## Installation

### Gradle
```kts
dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }
}

dependencies {
  implementation("com.boltfortesla:tesla-fleet-sdk-kotlin:3.1.3")
  // For Android projects:
  implementation("com.boltfortesla:tesla-fleet-sdk-kotlin-android:3.1.3")
}
```

### Maven
```xml
<project>
  <repositories>
    <repository>
      <id>central</id>
      <url>https://repo1.maven.org/maven2</url>  
    </repository>
  </repositories>
  
  <dependency>
    <groupId>com.boltfortesla</groupId>
    <artifactId>tesla-fleet-sdk-kotlin</artifactId>
    <version>3.1.3</version>
  </dependency>
</project>
```
## Contributing

Contributions are always welcome!

There is currently no formal contribution process. Simply fork and propose a pull request.

## Usage/Examples

### Creating an instance of the API

`tesla-developer-public-key` is the public key registered with Tesla for a third party application from [developer.tesla.com](https://developer.tesla.com/).
```kotlin
import com.boltfortesla.teslafleetsdk.TeslaFleetAPi

val teslaFleetApi = TeslaFleetApi.newInstance("tesla-developer-public-key".toByteArray())
```

If you have your public key as a raw PEM string you can use:


```kotlin
import com.boltfortesla.teslafleetsdk.TeslaFleetAPi
import com.boltfortesla.teslafleetsdk.keys.Pem

val PUBLIC_KEY =
  "-----BEGIN PUBLIC KEY-----\n" +
    "Qpk+RaWQsuaYFrTmjRyb8SWmMWApGoj1FKz9C+kqxqgGTZzcUCXxBT/stP9Sy3YD\n" +
    "ITOBY8yiAmSzeAd9XrkScU6DTmPoV46XGCVXyXkadZovUj6K2Fr8rSQurfBr9n7N2jKNZg==\n"
    "-----END PUBLIC KEY-----"
val teslaFleetApi = TeslaFleetApi.newInstance(Pem(PUBLIC_KEY).byteArray())
```

### Session Management
As part of the Command Protocol, a Session must be established with a vehicle. Sessions are specific to Vehicle and a [domain](https://github.com/teslamotors/vehicle-command/blob/main/pkg/protocol/domains.go). These sessions are long lived, e.g. a session may persist until the vehicle is rebooted. In order to avoid excessive handshakes, you can backup and restore session info. This is useful if your usage of this library is not a long-running process (think a mobile app).

**!!! WARNING !!!**  
This data should be handled the same as credentials. While on its own is not enough information to interact with a Vehicle, it is sensitive information

```kotlin
// Returns a base64 encoded string of all the Sesssions that were established.
val sessionInfo = fleetApi.saveSessionInfo()

// ...
// Loads the base64 string returned by "saveSessionInfo" into memory
fleetApi.loadSessionInfo(sessionInfo)
```

### Calling APIs
Once you have an instace of `TeslaFleetApi`, you can make API calls. They are grouped into classes similarly to how they are at [developer.tesla.com](https://developer.tesla.com/). Methods and parameters are based on the official API documentation. See the API docs or the source for more information.

```kotlin
// This depends on the current User's account
val region = Region.NA_APAC
// The oAuth Access Token for the User who's account will be used to make API Calls.
// Make sure the Token is not expired. This SDK does not handle token refreshes.
val accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
// Optional, by default will retry indefinitely, with a maximum of a 2 second backoff with jitter.
// See source for configuration options
val retryConfig = RetryConfig()
// An optional OkHttpClient.Builder, to customize network requests. For example, to add additional
// interceptors.
val clientBuilder = OkHttpClient.Builder()
```

### `FleetApiEndpoints`
Returns an API for performing actions not related to a specific product
```kotlin
TeslaFleetApi.fleetApiEndpoints(region, accessToken, retryConfig, clientbuilder)
```

#### `TeslaOAuth`
Returns an API for performing OAuth related actions (currently only `refreshToken`).
```kotlin
TeslaFleetApi.oAuth(region, accessToken, retryConfig, clientbuilder)
```

#### `ChargingEndpoints`
Returns an API for performing actions related to Charging history and sessions
```kotlin
TeslaFleetApi.oAuth(region, accessToken, retryConfig, clientbuilder)
```

#### `EnergyEndpoints`
Returns an API for performing actions related to Energy products (Solar & Powerwall)
```kotlin
TeslaFleetApi.energyEndpoints(region, accessToken, retryConfig, clientbuilder)
```

#### `UserEndpoints`
Returns an API for performing actions related to the current User
```kotlin
TeslaFleetApi.userEndpoints(region, accessToken, retryConfig, clientbuilder)
```

#### `VehicleEndpoints`
Returns an API for performing actions related to a Vehicle
Note: This class also contains a `listVehicles` call that does NOT require a valid VIN.
```kotlin
TeslaFleetApi.vehicleEndpoints(
  vin = "5YJ3000000NEXUS01",
  region,
  accessToken,
  retryConfig,
  clientbuilder
)
```

#### `VehicleCommands`
Returns an API for executing commands on a Vehicle.

##### `SharedSecretFetcher`
As part of the [Command Protocol](https://github.com/teslamotors/vehicle-command/blob/main/pkg/protocol/protocol.md#key-agreement), a "Shared Secret" must be calcuated using the Tesla Developer Application's Client Private Key and the Vehicle's Public Key. Because the Private Key should be kept private, this may not be a value that you will have stored alongside your usage of this SDK, and should not be shared over the internet.

`SharedSecretFetcher` must be implemented. It takes in the Vehicle's public key, which the SDK will provide, and requres that you use that to return the hex-encoded SHA1 digest of the ECDH shared secret.
See the [Key Agreement](https://github.com/teslamotors/vehicle-command/blob/main/pkg/protocol/protocol.md#key-agreement) section of the official documentation for more info on the algorithm.
```kotlin
val sharedSecretFetcher = { vehiclePublicKey ->
  /** A pseudocode example:
  val ecdh = createECDH(curveName = "p256")
  ecdh.setPrivateKey(YOUR_CLIENT_PRIVATE_KEY)
  val sharedSecret = ecdh.computeSecretAsHex(vehiclePublicKey)
  sharedSecret.sha1Hash().toHex()
   */
}
```
If you know _ahead of time_ that the vehicle does not support the Command Protocol, set `commandProtocolSupported `
to `false`. Otherwise, it should be `true` it will be automatically determined by the SDK.
```
// Returns an API for executing commands on 
TeslaFleetApi.vehicleCommands(
  vin = "5YJ3000000NEXUS01",
  sharedSecretFetcher,
  commandProtocolSupported = true,
  region,
  accessToken,
  retryConfig,
  clientbuilder
)

```
## License

[GNU GPLv3](https://choosealicense.com/licenses/gpl-3.0/)

