package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.*

enum class UserRole(val title: String, val subtitle: String, val badge: String) {
    CITIZEN(
        title = "CITIZEN",
        subtitle = "Emergency SOS, damage reporting, local volunteer aid, shelter locator & family status",
        badge = "PUBLIC"
    ),
    VOLUNTEER(
        title = "VOLUNTEER",
        subtitle = "Field triage, accept active rescue missions, victim navigation & hazard verification",
        badge = "FIRST RESPONDER"
    ),
    AUTHORITY(
        title = "AUTHORITY / GOVERNMENT",
        subtitle = "Incident command center, tactical live map, asset dispatch, zone alerts & EAS broadcast",
        badge = "COMMAND & CONTROL"
    )
}

enum class DisasterType(
    val displayName: String,
    val description: String,
    val iconName: String
) {
    FLOOD("Flood", "Rising water level, submerged streets or dam overflow", "water_drop"),
    FIRE("Fire", "Structure blaze, wildfire, or explosive fire hazard", "local_fire_department"),
    LANDSLIDE("Landslide", "Slope failure, mudflow, or rockfall hazard", "terrain"),
    BUILDING_COLLAPSE("Building Collapse", "Structural failure, trapped civilians or debris hazard", "domain_disabled"),
    ROAD_BLOCKED("Road Blocked", "Debris, fallen trees or sinkhole blocking emergency vehicles", "block"),
    POWER_LINE("Power Line Down", "Exposed high-voltage wire or transformer explosion", "bolt"),
    EARTHQUAKE("Earthquake Damage", "Fissures, damaged overpasses, severe seismic structural damage", "vibration"),
    CHEMICAL_HAZARD("Chemical / Gas Leak", "Toxic vapors, industrial spill or volatile gas leak", "warning"),
    OTHER("Other Hazard", "Uncategorized critical emergency condition", "emergency")
}

enum class Severity(val label: String, val color: Color, val containerColor: Color, val onContainerColor: Color) {
    LOW("LOW", InfoBlue, InfoBlueContainer, InfoBlueOnContainer),
    MEDIUM("MEDIUM", WarningAmber, WarningAmberContainer, WarningAmberOnContainer),
    HIGH("HIGH", SeverityOrange, SeverityOrangeContainer, SeverityOrangeOnContainer),
    CRITICAL("CRITICAL", EmergencyRed, EmergencyRedContainer, EmergencyRedOnContainer)
}

enum class SosStatus(val label: String, val color: Color, val containerColor: Color) {
    NEW("NEW", EmergencyRed, EmergencyRedContainer),
    ASSIGNED("ASSIGNED", WarningAmber, WarningAmberContainer),
    RESPONDING("RESPONDING", InfoBlue, InfoBlueContainer),
    ON_SITE("ON SITE", SeverityOrange, SeverityOrangeContainer),
    RESOLVED("RESOLVED", SafeGreen, SafeGreenContainer)
}

data class SosIncident(
    val id: String,
    val citizenName: String,
    val citizenPhone: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val severity: Severity,
    val batteryPercentage: Int,
    val timestamp: String,
    val status: SosStatus,
    val assignedVolunteerId: String? = null,
    val assignedVolunteerName: String? = null,
    val description: String = "Urgent citizen distress signal initiated.",
    val isBlackbox: Boolean = false,
    val lastKnownTime: String? = null,
    val accuracyMeters: Float = 4.2f
)

data class DamageReport(
    val id: String,
    val reporterName: String,
    val reporterRole: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val disasterType: DisasterType,
    val severity: Severity,
    val description: String,
    val timestamp: String,
    val photoCount: Int = 1,
    val hasVoiceNote: Boolean = false,
    val voiceNoteDurationSec: Int = 0,
    val syncStatus: SyncStatus = SyncStatus.SENT,
    val verificationStatus: VerificationStatus = VerificationStatus.PENDING_REVIEW,
    val assignedVolunteer: String? = null
)

enum class SyncStatus(val label: String, val color: Color) {
    SENT("SENT", SafeGreen),
    PENDING_SYNC("PENDING SYNC", WarningAmber),
    STORED_OFFLINE("STORED OFFLINE", MutedSlate)
}

enum class VerificationStatus(val label: String, val color: Color) {
    VERIFIED("VERIFIED", SafeGreen),
    PENDING_REVIEW("PENDING REVIEW", WarningAmber),
    REJECTED("REJECTED", EmergencyRed)
}

data class Volunteer(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val isPermanent: Boolean,
    val isVerified: Boolean,
    val isAvailable: Boolean,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val batteryPercentage: Int,
    val skills: List<String>,
    val currentAssignmentId: String? = null,
    val responseHistoryCount: Int,
    val rating: Float = 4.9f,
    val distanceKm: Float = 1.2f,
    val equipment: List<String> = listOf("First Aid Kit", "Flashlight", "VHF Radio")
)

data class HazardZone(
    val id: String,
    val title: String,
    val disasterType: DisasterType,
    val severity: Severity,
    val centerLat: Double,
    val centerLng: Double,
    val radiusMeters: Int,
    val description: String,
    val activeDurationHours: Int,
    val createdAt: String,
    val affectedCitizensCount: Int,
    val isActive: Boolean = true
)

enum class InfrastructureType(val label: String, val icon: String) {
    HOSPITAL("Hospital / Medical", "local_hospital"),
    SHELTER("Emergency Shelter", "night_shelter"),
    EMERGENCY_CENTER("Disaster EOC", "corporate_fare"),
    FIRE_STATION("Fire Station", "fire_hydrant"),
    POLICE_STATION("Police Station", "local_police"),
    RELIEF_CAMP("Relief & Food Depot", "food_bank")
}

data class InfrastructureItem(
    val id: String,
    val name: String,
    val type: InfrastructureType,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val capacityPercentage: Int,
    val capacityTotal: Int,
    val capacityUsed: Int,
    val bedsAvailable: Int,
    val ambulancesCount: Int,
    val foodWaterDaysRemaining: Int,
    val status: String = "Operational",
    val phone: String = "+1 (555) 019-2830"
)

enum class ResourceCategory(val label: String) {
    VEHICLES("Vehicles & Ambulances"),
    RESCUE_TEAMS("Search & Rescue Squads"),
    MEDICAL_SUPPLIES("Medical & Triage Packs"),
    FOOD_RATIONS("Food & Water Supplies"),
    HEAVY_MACHINERY("Heavy Earthmovers & Boats")
}

data class ResourceItem(
    val id: String,
    val name: String,
    val category: ResourceCategory,
    val availableCount: Int,
    val deployedCount: Int,
    val totalCount: Int,
    val status: ResourceStatus,
    val location: String
)

enum class ResourceStatus(val label: String, val color: Color, val containerColor: Color) {
    AVAILABLE("AVAILABLE", SafeGreen, SafeGreenContainer),
    DEPLOYED("DEPLOYED", InfoBlue, InfoBlueContainer),
    CRITICAL_SHORTAGE("CRITICAL SHORTAGE", EmergencyRed, EmergencyRedContainer)
}

enum class SquadMemberStatus(val label: String, val color: Color, val containerColor: Color) {
    SAFE("SAFE", SafeGreen, SafeGreenContainer),
    NEEDS_HELP("NEEDS HELP", EmergencyRed, EmergencyRedContainer),
    UNKNOWN("UNKNOWN", WarningAmber, WarningAmberContainer),
    UNREACHABLE("UNREACHABLE", MutedSlate, SurfaceSecondary)
}

data class FamilyMember(
    val id: String,
    val name: String,
    val relationship: String,
    val status: SquadMemberStatus,
    val lastKnownLocation: String,
    val lastUpdated: String,
    val phone: String,
    val batteryPercentage: Int
)

enum class BroadcastType(val label: String, val icon: String) {
    EVACUATION("Evacuation Order", "emergency_share"),
    FLOOD_WARNING("Flood Warning", "water_drop"),
    FIRE_WARNING("Fire Alert", "local_fire_department"),
    ROAD_CLOSURE("Road Closure", "traffic"),
    HAZARD_ALERT("Hazard Warning", "warning"),
    SHELTER_INFO("Shelter Opening", "night_shelter")
}

data class EmergencyBroadcast(
    val id: String,
    val title: String,
    val type: BroadcastType,
    val targetAudience: String,
    val priority: String, // CRITICAL (EAS), HIGH, ADVISORY
    val message: String,
    val issuedAt: String,
    val issuedBy: String,
    val isConfirmed: Boolean = true
)

data class ChatMessage(
    val id: String,
    val senderName: String,
    val senderRole: String,
    val text: String,
    val timestamp: String,
    val isVoiceNote: Boolean = false,
    val voiceDurationSec: Int = 0,
    val isUrgent: Boolean = false,
    val isFromAuthority: Boolean = false,
    val isSentByMe: Boolean = false
)

data class AssistanceRequest(
    val id: String,
    val assistanceType: String,
    val location: String,
    val description: String,
    val requestedAt: String,
    val status: String = "Pending Volunteer Match"
)
