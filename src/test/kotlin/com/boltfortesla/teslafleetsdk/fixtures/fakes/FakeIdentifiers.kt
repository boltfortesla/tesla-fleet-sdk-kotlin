package com.boltfortesla.teslafleetsdk.fixtures.fakes

import com.boltfortesla.teslafleetsdk.Identifiers
import com.boltfortesla.teslafleetsdk.encoding.HexCodec.decodeHex
import com.boltfortesla.teslafleetsdk.fixtures.Constants.HANDSHAKE_ROUTING_ADDRESS
import com.boltfortesla.teslafleetsdk.fixtures.Constants.REQUEST_UUID

/** Fake implementation of [Identifiers]. */
class FakeIdentifiers : Identifiers {
  /** The return value of [randomUuid]. */
  var uuid = REQUEST_UUID.decodeHex()

  /** The return value of [randomRoutingAddress]. */
  var routingAddress = HANDSHAKE_ROUTING_ADDRESS.decodeHex()

  override fun randomUuid() = uuid

  override fun randomRoutingAddress() = routingAddress
}
