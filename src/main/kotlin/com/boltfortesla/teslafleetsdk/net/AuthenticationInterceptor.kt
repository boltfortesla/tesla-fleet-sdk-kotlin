package com.boltfortesla.teslafleetsdk.net

import com.boltfortesla.teslafleetsdk.log.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthenticationInterceptor(private val authToken: String) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    Log.d("Adding accessToken to request")
    val builder = chain.request().newBuilder()

    builder.addHeader(AUTH_KEY, String.format(AUTH_VALUE, authToken.trim()))
    builder.addHeader(USER_AGENT_KEY, USER_AGENT_VALUE)

    val augmentedRequest = builder.build()
    return chain.proceed(augmentedRequest)
  }

  private companion object {
    const val AUTH_KEY = "Authorization"
    const val AUTH_VALUE = "Bearer %s"

    const val USER_AGENT_KEY = "user-agent"
    const val USER_AGENT_VALUE = "TeslaFleetSdk/2.0.1"
  }
}
