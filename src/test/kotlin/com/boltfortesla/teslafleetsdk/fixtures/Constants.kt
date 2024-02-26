package com.boltfortesla.teslafleetsdk.fixtures

/**
 * Constant values for testing. From
 * https://github.com/teslamotors/vehicle-command/blob/main/pkg/protocol/protocol.md
 */
object Constants {
  const val SHARED_SECRET = "1b2fce19967b79db696f909cff89ea9a"
  const val HANDSHAKE_HMAC = "fceb679ee7bca756fcd441bf238bf2f338629b41d9eb9c67be1b32c9672ce300"
  const val REQUEST_UUID = "1588d5a30eabc6f8fc9a951b11f6fd11"
  const val HANDSHAKE_TLV =
    "000106021135594a333031323334353637383941424306101588d5a30eabc6f8fc9a951b11f6fd11ff"
  const val HANDSHAKE_ROUTING_ADDRESS = "2c907bd76c640d360b3027dc7404efde"
  const val HANDSHAKE_SESSION_INFO_TAG =
    "996c1fe38331be138f8039c194b14db2198846ed7d8251e6749284d7b32ea002"
  const val HANDSHAKE_SESSION_INFO =
    "0806124104c7a1f47138486aa4729971494878d33b1a24e39571f748a6e16c5955b3d877d3a6aaa0e955166474af5d32c410f439a2234137ad1bb085fd4e8813c958f11d971a104c463f9cc0d3d26906e982ed224adde6255a0a0000"
  const val EPOCH = "4c463f9cc0d3d26906e982ed224adde6"
  const val VIN = "5YJ30123456789ABC"
  const val METADATA =
    "000106010103021135594a333031323334353637383941424303104c463f9cc0d3d26906e982ed224adde6040400000a5f050400000007ff"
}
