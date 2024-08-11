package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints

import com.boltfortesla.teslafleetsdk.net.NetworkExecutor
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.TelemetryConfig.FieldConfig
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.request.FleetStatusRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.request.FleetTelemetryConfigRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.request.RedeemInviteRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.request.SignedCommandRequest

/** Implementation of [VehicleEndpoints]. */
internal class VehicleEndpointsImpl(
  private val vin: String,
  private val vehicleEndpointsApi: VehicleEndpointsApi,
  private val networkExecutor: NetworkExecutor
) : VehicleEndpoints {
  override suspend fun getDrivers() =
    networkExecutor.execute { vehicleEndpointsApi.getDrivers(vin) }

  override suspend fun removeDrivers(userId: Int) =
    networkExecutor.execute { vehicleEndpointsApi.removeDriver(vin, userId) }

  override suspend fun getEligibleSubscriptions() =
    networkExecutor.execute { vehicleEndpointsApi.getEligibleSubscriptions(vin) }

  override suspend fun getEligibleUpgrades() =
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
    networkExecutor.execute { vehicleEndpointsApi.deleteFleetTelemetryConfig(vin) }

  override suspend fun getFleetTelemetryConfig() =
    networkExecutor.execute { vehicleEndpointsApi.getFleetTelemetryConfig(vin) }

  override suspend fun listVehicles(page: Int?, perPage: Int?) =
    networkExecutor.execute { vehicleEndpointsApi.listVehicles(page, perPage) }

  override suspend fun isMobileEnabled() =
    networkExecutor.execute { vehicleEndpointsApi.mobileEnabled(vin) }

  override suspend fun getNearbyChargingSites(count: Int?, radius: Int?, detail: Boolean?) =
    networkExecutor.execute { vehicleEndpointsApi.nearbyChargingSites(vin, count, radius, detail) }

  override suspend fun getOptions() =
    networkExecutor.execute { vehicleEndpointsApi.getOptions(vin) }

  override suspend fun getRecentAlerts() =
    networkExecutor.execute { vehicleEndpointsApi.getRecentAlerts(vin) }

  override suspend fun getReleaseNotes(staged: Boolean, language: Int) =
    networkExecutor.execute { vehicleEndpointsApi.getReleaseNotes(vin, staged, language) }

  override suspend fun getServiceData() =
    networkExecutor.execute { vehicleEndpointsApi.getServiceData(vin) }

  override suspend fun shareInvites() =
    networkExecutor.execute { vehicleEndpointsApi.getShareInvites(vin) }

  override suspend fun createShareInvite() =
    networkExecutor.execute { vehicleEndpointsApi.createShareInvite(vin) }

  override suspend fun redeemShareInvite(code: String) =
    networkExecutor.execute { vehicleEndpointsApi.redeemShareInvite(RedeemInviteRequest(code)) }

  override suspend fun revokeShareInvites(id: String) =
    networkExecutor.execute { vehicleEndpointsApi.revokeShareInvite(vin, id) }

  override suspend fun signedCommand(routableMessage: String) =
    networkExecutor.execute {
      vehicleEndpointsApi.sendSignedCommand(vin, SignedCommandRequest(routableMessage))
    }

  override suspend fun getSubscriptions(deviceToken: String, deviceType: String) =
    networkExecutor.execute { vehicleEndpointsApi.getSubscriptions(deviceToken, deviceType) }

  override suspend fun setSubscriptions(deviceToken: String, deviceType: String) =
    networkExecutor.execute { vehicleEndpointsApi.setSubscriptions(deviceToken, deviceType) }

  override suspend fun getVehicle() =
    networkExecutor.execute { vehicleEndpointsApi.getVehicle(vin) }

  override suspend fun getVehicleData(endpoints: List<VehicleEndpoints.Endpoint>) =
    networkExecutor.execute {
      vehicleEndpointsApi.getVehicleData(vin, endpoints.joinToString(";") { it.name.lowercase() })
    }

  override suspend fun getVehicleSubscriptions(deviceToken: String, deviceType: String) =
    networkExecutor.execute { vehicleEndpointsApi.getVehicleSubscriptions(deviceToken, deviceType) }

  override suspend fun setVehicleSubscriptions(deviceToken: String, deviceType: String) =
    networkExecutor.execute { vehicleEndpointsApi.setVehicleSubscriptions(deviceToken, deviceType) }

  override suspend fun wakeUp() = networkExecutor.execute { vehicleEndpointsApi.wakeUpVehicle(vin) }

  override suspend fun getWarrantyDetails() =
    networkExecutor.execute { vehicleEndpointsApi.getWarrantyDetails(vin) }
}
