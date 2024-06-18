package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands

import retrofit2.HttpException
import retrofit2.Response

/**
 * Wrapper for [HttpException] to indicate that the error cannot be recovered from the relevant
 * request should not be retried.
 */
class UnrecoverableHttpException(response: Response<*>) : HttpException(response)
