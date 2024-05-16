package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints

import com.boltfortesla.teslafleetsdk.net.NetworkExecutor
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.TelemetryConfig.FieldConfig
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.request.FleetStatusRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.request.FleetTelemetryConfigRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.request.RedeemInviteRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.request.SignedCommandRequest

/** Implementation of [VehicleEndpoints]. */
internal class VehicleEndpointsImpl(
  private val vehicleTag: String,
  private val vehicleEndpointsApi: VehicleEndpointsApi,
  private val networkExecutor: NetworkExecutor
) : VehicleEndpoints {
  override suspend fun getDrivers() =
    networkExecutor.execute { vehicleEndpointsApi.getDrivers(vehicleTag) }

  override suspend fun removeDrivers(userId: Int) =
    networkExecutor.execute { vehicleEndpointsApi.removeDriver(vehicleTag, userId) }

  override suspend fun getEligibleSubscriptions(vin: String) =
    networkExecutor.execute { vehicleEndpointsApi.getEligibleSubscriptions(vin) }

  override suspend fun getEligibleUpgrades(vin: String) =
    networkExecutor.execute { vehicleEndpointsApi.getEligibleUpgrades(vin) }

  override suspend fun getFleetStatus(vins: List<String>) =
    networkExecutor.execute { vehicleEndpointsApi.fleetStatus(FleetStatusRequest(vins)) }

  override suspend fun createFleetTelemetryConfig(
    vins: List<String>,
    hostname: String,
    ca: String,
    fields: Map<String, Int>,
    alertTypes: List<String>,
    expirationTimeSeconds: Long
  ) =
    networkExecutor.execute {
      vehicleEndpointsApi.createFleetTelemetryConfig(
        FleetTelemetryConfigRequest(
          vins,
          TelemetryConfig(
            hostname,
            ca,
            expirationTimeSeconds,
            fields.mapValues { FieldConfig(it.value) },
            alertTypes,
          )
        )
      )
    }

  override suspend fun deleteFleetTelemetryConfig() =
    networkExecutor.execute { vehicleEndpointsApi.deleteFleetTelemetryConfig(vehicleTag) }

  override suspend fun getFleetTelemetryConfig() =
    networkExecutor.execute { vehicleEndpointsApi.getFleetTelemetryConfig(vehicleTag) }

  override suspend fun listVehicles(page: Int?, perPage: Int?) =
    networkExecutor.execute { vehicleEndpointsApi.listVehicles(page, perPage) }

  override suspend fun isMobileEnabled() =
    networkExecutor.execute { vehicleEndpointsApi.mobileEnabled(vehicleTag) }

  override suspend fun getNearbyChargingSites(count: Int?, radius: Int?, detail: Boolean?) =
    networkExecutor.execute { vehicleEndpointsApi.nearbyChargingSites(vehicleTag, count, radius, detail) }

  override suspend fun getOptions(vin: String) =
    networkExecutor.execute { vehicleEndpointsApi.getOptions(vin) }

  override suspend fun getRecentAlerts() =
    networkExecutor.execute { vehicleEndpointsApi.getRecentAlerts(vehicleTag) }

  override suspend fun getReleaseNotes(staged: Boolean, language: Int) =
    networkExecutor.execute { vehicleEndpointsApi.getReleaseNotes(vehicleTag, staged, language) }

  override suspend fun getServiceData() =
    networkExecutor.execute { vehicleEndpointsApi.getServiceData(vehicleTag) }

  override suspend fun shareInvites() =
    networkExecutor.execute { vehicleEndpointsApi.getShareInvites(vehicleTag) }

  override suspend fun createShareInvite() =
    networkExecutor.execute { vehicleEndpointsApi.createShareInvite(vehicleTag) }

  override suspend fun redeemShareInvite(code: String) =
    networkExecutor.execute { vehicleEndpointsApi.redeemShareInvite(RedeemInviteRequest(code)) }

  override suspend fun revokeShareInvites(id: String) =
    networkExecutor.execute { vehicleEndpointsApi.revokeShareInvite(vehicleTag, id) }

  override suspend fun signedCommand(routableMessage: String) =
    networkExecutor.execute {
      vehicleEndpointsApi.sendSignedCommand(vehicleTag, SignedCommandRequest(routableMessage))
    }

  override suspend fun getSubscriptions(deviceToken: String, deviceType: String) =
    networkExecutor.execute { vehicleEndpointsApi.getSubscriptions(deviceToken, deviceType) }

  override suspend fun setSubscriptions(deviceToken: String, deviceType: String) =
    networkExecutor.execute { vehicleEndpointsApi.setSubscriptions(deviceToken, deviceType) }

  override suspend fun getVehicle() =
    networkExecutor.execute { vehicleEndpointsApi.getVehicle(vehicleTag) }

  override suspend fun getVehicleData(endpoints: List<VehicleEndpoints.Endpoint>) =
    networkExecutor.execute {
      vehicleEndpointsApi.getVehicleData(vehicleTag, endpoints.joinToString(";") { it.name.lowercase() })
    }

  override suspend fun getVehicleSubscriptions(deviceToken: String, deviceType: String) =
    networkExecutor.execute { vehicleEndpointsApi.getVehicleSubscriptions(deviceToken, deviceType) }

  override suspend fun setVehicleSubscriptions(deviceToken: String, deviceType: String) =
    networkExecutor.execute { vehicleEndpointsApi.setVehicleSubscriptions(deviceToken, deviceType) }

  override suspend fun wakeUp() = networkExecutor.execute { vehicleEndpointsApi.wakeUpVehicle(vehicleTag) }

  override suspend fun getWarrantyDetails(vin: String) =
    networkExecutor.execute { vehicleEndpointsApi.getWarrantyDetails(vin) }
}
