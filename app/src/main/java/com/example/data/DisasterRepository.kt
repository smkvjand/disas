package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class DisasterRepository {

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _currentRole = MutableStateFlow<UserRole?>(null)
    val currentRole: StateFlow<UserRole?> = _currentRole.asStateFlow()

    // Citizen SOS state
    private val _isCitizenSosActive = MutableStateFlow(false)
    val isCitizenSosActive: StateFlow<Boolean> = _isCitizenSosActive.asStateFlow()

    private val _currentSosIncident = MutableStateFlow<SosIncident?>(null)
    val currentSosIncident: StateFlow<SosIncident?> = _currentSosIncident.asStateFlow()

    // Volunteer status
    private val _isVolunteerAvailable = MutableStateFlow(true)
    val isVolunteerAvailable: StateFlow<Boolean> = _isVolunteerAvailable.asStateFlow()

    private val _volunteerBattery = MutableStateFlow(68)
    val volunteerBattery: StateFlow<Int> = _volunteerBattery.asStateFlow()

    // Incidents & Data lists
    private val _sosList = MutableStateFlow<List<SosIncident>>(emptyList())
    val sosList: StateFlow<List<SosIncident>> = _sosList.asStateFlow()

    private val _damageReports = MutableStateFlow<List<DamageReport>>(emptyList())
    val damageReports: StateFlow<List<DamageReport>> = _damageReports.asStateFlow()

    private val _volunteers = MutableStateFlow<List<Volunteer>>(emptyList())
    val volunteers: StateFlow<List<Volunteer>> = _volunteers.asStateFlow()

    private val _hazardZones = MutableStateFlow<List<HazardZone>>(emptyList())
    val hazardZones: StateFlow<List<HazardZone>> = _hazardZones.asStateFlow()

    private val _infrastructures = MutableStateFlow<List<InfrastructureItem>>(emptyList())
    val infrastructures: StateFlow<List<InfrastructureItem>> = _infrastructures.asStateFlow()

    private val _resources = MutableStateFlow<List<ResourceItem>>(emptyList())
    val resources: StateFlow<List<ResourceItem>> = _resources.asStateFlow()

    private val _familyMembers = MutableStateFlow<List<FamilyMember>>(emptyList())
    val familyMembers: StateFlow<List<FamilyMember>> = _familyMembers.asStateFlow()

    private val _broadcasts = MutableStateFlow<List<EmergencyBroadcast>>(emptyList())
    val broadcasts: StateFlow<List<EmergencyBroadcast>> = _broadcasts.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _offlineQueuedReports = MutableStateFlow<List<DamageReport>>(emptyList())
    val offlineQueuedReports: StateFlow<List<DamageReport>> = _offlineQueuedReports.asStateFlow()

    private val _activeAssignedMission = MutableStateFlow<SosIncident?>(null)
    val activeAssignedMission: StateFlow<SosIncident?> = _activeAssignedMission.asStateFlow()

    init {
        loadRealisticSampleData()
    }

    private fun loadRealisticSampleData() {
        _sosList.value = listOf(
            SosIncident(
                id = "SOS-2841",
                citizenName = "Sarah Jenkins",
                citizenPhone = "+1 (555) 349-8812",
                latitude = 37.7749,
                longitude = -122.4194,
                address = "428 Market St, Financial District",
                severity = Severity.CRITICAL,
                batteryPercentage = 18,
                timestamp = "3 mins ago",
                status = SosStatus.NEW,
                description = "Elderly person trapped on 2nd floor with rising flood water. Power cut.",
                isBlackbox = false,
                accuracyMeters = 3.5f
            ),
            SosIncident(
                id = "SOS-2839",
                citizenName = "David Chen",
                citizenPhone = "+1 (555) 782-9014",
                latitude = 37.7833,
                longitude = -122.4167,
                address = "850 Howard St, SOMA",
                severity = Severity.CRITICAL,
                batteryPercentage = 4,
                timestamp = "12 mins ago",
                status = SosStatus.ASSIGNED,
                assignedVolunteerId = "VOL-9428",
                assignedVolunteerName = "Marcus Vance (You)",
                description = "Severe building facade collapse, minor leg injury, unable to navigate debris.",
                isBlackbox = true,
                lastKnownTime = "12 mins ago",
                accuracyMeters = 7.8f
            ),
            SosIncident(
                id = "SOS-2835",
                citizenName = "Elena Rodriguez",
                citizenPhone = "+1 (555) 910-4421",
                latitude = 37.7690,
                longitude = -122.4467,
                address = "710 Ashbury St, Haight",
                severity = Severity.HIGH,
                batteryPercentage = 42,
                timestamp = "24 mins ago",
                status = SosStatus.RESPONDING,
                assignedVolunteerId = "VOL-9411",
                assignedVolunteerName = "Rachel Kim",
                description = "Family of 4 with infant requiring urgent evacuation due to gas fumes.",
                isBlackbox = false,
                accuracyMeters = 4.1f
            ),
            SosIncident(
                id = "SOS-2820",
                citizenName = "James Thornton",
                citizenPhone = "+1 (555) 123-7765",
                latitude = 37.7550,
                longitude = -122.4200,
                address = "1120 Valencia St, Mission",
                severity = Severity.HIGH,
                batteryPercentage = 81,
                timestamp = "45 mins ago",
                status = SosStatus.ON_SITE,
                assignedVolunteerId = "VOL-9402",
                assignedVolunteerName = "Carlos Mendez",
                description = "Tree crushed rear entrance, assisting with clearing doorway.",
                isBlackbox = false,
                accuracyMeters = 3.0f
            ),
            SosIncident(
                id = "SOS-2804",
                citizenName = "Maya Lin",
                citizenPhone = "+1 (555) 604-3329",
                latitude = 37.7900,
                longitude = -122.4000,
                address = "Pier 14, Embarcadero",
                severity = Severity.MEDIUM,
                batteryPercentage = 65,
                timestamp = "1 hr ago",
                status = SosStatus.RESOLVED,
                assignedVolunteerId = "VOL-9428",
                assignedVolunteerName = "Marcus Vance (You)",
                description = "Evacuated from shoreline overflow area to Shelter #2.",
                isBlackbox = false,
                accuracyMeters = 5.0f
            )
        )

        _activeAssignedMission.value = _sosList.value[1] // SOS-2839 assigned to volunteer Marcus

        _damageReports.value = listOf(
            DamageReport(
                id = "RPT-501",
                reporterName = "Marcus Vance (Volunteer)",
                reporterRole = "VOLUNTEER",
                latitude = 37.7780,
                longitude = -122.4110,
                address = "5th & Mission St Intersection",
                disasterType = DisasterType.FLOOD,
                severity = Severity.CRITICAL,
                description = "Storm drain ruptured. Water level 3.5 ft deep. Submerged 2 passenger sedans.",
                timestamp = "8 mins ago",
                photoCount = 2,
                hasVoiceNote = true,
                voiceNoteDurationSec = 14,
                syncStatus = SyncStatus.SENT,
                verificationStatus = VerificationStatus.VERIFIED
            ),
            DamageReport(
                id = "RPT-502",
                reporterName = "Liam Walker (Citizen)",
                reporterRole = "CITIZEN",
                latitude = 37.7850,
                longitude = -122.4280,
                address = "Van Ness & Geary Blvd",
                disasterType = DisasterType.BUILDING_COLLAPSE,
                severity = Severity.HIGH,
                description = "Commercial awning and exterior brick wall collapsed across two westbound lanes.",
                timestamp = "15 mins ago",
                photoCount = 1,
                hasVoiceNote = false,
                syncStatus = SyncStatus.SENT,
                verificationStatus = VerificationStatus.PENDING_REVIEW
            ),
            DamageReport(
                id = "RPT-503",
                reporterName = "Aisha Patel (Citizen)",
                reporterRole = "CITIZEN",
                latitude = 37.7620,
                longitude = -122.4350,
                address = "Castro & 18th St",
                disasterType = DisasterType.POWER_LINE,
                severity = Severity.CRITICAL,
                description = "High voltage line sparking on wet pavement near bus stop. Area cordoned by bystanders.",
                timestamp = "22 mins ago",
                photoCount = 1,
                hasVoiceNote = true,
                voiceNoteDurationSec = 9,
                syncStatus = SyncStatus.SENT,
                verificationStatus = VerificationStatus.VERIFIED
            ),
            DamageReport(
                id = "RPT-504",
                reporterName = "Carlos Mendez (Volunteer)",
                reporterRole = "VOLUNTEER",
                latitude = 37.7710,
                longitude = -122.4500,
                address = "Buena Vista West Hillside",
                disasterType = DisasterType.LANDSLIDE,
                severity = Severity.HIGH,
                description = "Mud and retaining wall sliding down towards roadway. Soil unstable.",
                timestamp = "35 mins ago",
                photoCount = 3,
                hasVoiceNote = false,
                syncStatus = SyncStatus.SENT,
                verificationStatus = VerificationStatus.VERIFIED
            ),
            DamageReport(
                id = "RPT-505",
                reporterName = "Anonymous Citizen",
                reporterRole = "CITIZEN",
                latitude = 37.7950,
                longitude = -122.4080,
                address = "Columbus Ave & Broadway",
                disasterType = DisasterType.ROAD_BLOCKED,
                severity = Severity.MEDIUM,
                description = "Fallen construction scaffolding obstructing pedestrian pathway.",
                timestamp = "50 mins ago",
                photoCount = 1,
                hasVoiceNote = false,
                syncStatus = SyncStatus.SENT,
                verificationStatus = VerificationStatus.PENDING_REVIEW
            )
        )

        _volunteers.value = listOf(
            Volunteer(
                id = "VOL-9428",
                name = "Marcus Vance (Active Profile)",
                phone = "+1 (555) 438-9901",
                email = "m.vance@response-vol.org",
                isPermanent = true,
                isVerified = true,
                isAvailable = true,
                latitude = 37.7760,
                longitude = -122.4180,
                address = "Sector 4 — Mid-Market",
                batteryPercentage = 68,
                skills = listOf("EMT / First Aid", "Search & Rescue", "4x4 Vehicle", "Water Safety"),
                currentAssignmentId = "SOS-2839",
                responseHistoryCount = 34,
                rating = 4.95f,
                distanceKm = 0.8f
            ),
            Volunteer(
                id = "VOL-9411",
                name = "Rachel Kim",
                phone = "+1 (555) 201-8843",
                email = "rachel.k@rescuevol.net",
                isPermanent = true,
                isVerified = true,
                isAvailable = false,
                latitude = 37.7700,
                longitude = -122.4450,
                address = "Sector 2 — Haight",
                batteryPercentage = 84,
                skills = listOf("Certified Nurse", "Triage Specialist", "Bilingual (ES/EN)"),
                currentAssignmentId = "SOS-2835",
                responseHistoryCount = 52,
                rating = 4.98f,
                distanceKm = 2.1f
            ),
            Volunteer(
                id = "VOL-9402",
                name = "Carlos Mendez",
                phone = "+1 (555) 670-1129",
                email = "cmendez@relief-corp.org",
                isPermanent = true,
                isVerified = true,
                isAvailable = false,
                latitude = 37.7560,
                longitude = -122.4190,
                address = "Sector 5 — Mission",
                batteryPercentage = 55,
                skills = listOf("Chainsaw / Debris Removal", "Heavy Lifting", "Emergency Driving"),
                currentAssignmentId = "SOS-2820",
                responseHistoryCount = 28,
                rating = 4.88f,
                distanceKm = 2.9f
            ),
            Volunteer(
                id = "VOL-8109",
                name = "Dr. Anita Desai",
                phone = "+1 (555) 902-3341",
                email = "anita.desai.md@gmail.com",
                isPermanent = false,
                isVerified = true,
                isAvailable = true,
                latitude = 37.7810,
                longitude = -122.4120,
                address = "Sector 1 — Downtown",
                batteryPercentage = 92,
                skills = listOf("Physician", "Trauma Care", "Pediatric Emergency"),
                currentAssignmentId = null,
                responseHistoryCount = 14,
                rating = 5.0f,
                distanceKm = 1.1f
            ),
            Volunteer(
                id = "VOL-8114",
                name = "Jordan Miller",
                phone = "+1 (555) 441-2900",
                email = "jmiller.volunteer@yahoo.com",
                isPermanent = false,
                isVerified = false,
                isAvailable = true,
                latitude = 37.7650,
                longitude = -122.4300,
                address = "Sector 3 — Castro",
                batteryPercentage = 77,
                skills = listOf("Boat Navigation", "Supplies Distribution", "Physical Labor"),
                currentAssignmentId = null,
                responseHistoryCount = 6,
                rating = 4.70f,
                distanceKm = 1.9f
            )
        )

        _hazardZones.value = listOf(
            HazardZone(
                id = "HZ-101",
                title = "Downtown Flash Flood Inundation Zone",
                disasterType = DisasterType.FLOOD,
                severity = Severity.CRITICAL,
                centerLat = 37.7770,
                centerLng = -122.4140,
                radiusMeters = 850,
                description = "Storm surge combined with culvert rupture. Evacuation corridor open via 7th St.",
                activeDurationHours = 18,
                createdAt = "2 hours ago",
                affectedCitizensCount = 2840,
                isActive = true
            ),
            HazardZone(
                id = "HZ-102",
                title = "South Market Structural Hazard Perimeter",
                disasterType = DisasterType.BUILDING_COLLAPSE,
                severity = Severity.HIGH,
                centerLat = 37.7840,
                centerLng = -122.4190,
                radiusMeters = 400,
                description = "Damaged brick facades. Risk of aftershock secondary collapse. No pedestrian traffic.",
                activeDurationHours = 24,
                createdAt = "4 hours ago",
                affectedCitizensCount = 1120,
                isActive = true
            ),
            HazardZone(
                id = "HZ-103",
                title = "Hillside Mudslide Warning Sector",
                disasterType = DisasterType.LANDSLIDE,
                severity = Severity.HIGH,
                centerLat = 37.7695,
                centerLng = -122.4490,
                radiusMeters = 550,
                description = "Slope saturation at critical threshold. Heavy vehicles prohibited.",
                activeDurationHours = 12,
                createdAt = "1 hour ago",
                affectedCitizensCount = 680,
                isActive = true
            )
        )

        _infrastructures.value = listOf(
            InfrastructureItem(
                id = "INF-01",
                name = "San Francisco General Trauma Hospital",
                type = InfrastructureType.HOSPITAL,
                latitude = 37.7555,
                longitude = -122.4045,
                address = "1001 Potrero Ave",
                capacityPercentage = 86,
                capacityTotal = 420,
                capacityUsed = 361,
                bedsAvailable = 19,
                ambulancesCount = 6,
                foodWaterDaysRemaining = 14,
                status = "High Inflow - Operational",
                phone = "+1 (555) 206-8000"
            ),
            InfrastructureItem(
                id = "INF-02",
                name = "UCSF Parnassus Medical Center",
                type = InfrastructureType.HOSPITAL,
                latitude = 37.7631,
                longitude = -122.4580,
                address = "505 Parnassus Ave",
                capacityPercentage = 72,
                capacityTotal = 380,
                capacityUsed = 274,
                bedsAvailable = 46,
                ambulancesCount = 8,
                foodWaterDaysRemaining = 21,
                status = "Fully Operational",
                phone = "+1 (555) 476-1000"
            ),
            InfrastructureItem(
                id = "INF-03",
                name = "Civic Auditorium Emergency Shelter #1",
                type = InfrastructureType.SHELTER,
                latitude = 37.7785,
                longitude = -122.4172,
                address = "99 Grove St",
                capacityPercentage = 64,
                capacityTotal = 600,
                capacityUsed = 384,
                bedsAvailable = 216,
                ambulancesCount = 2,
                foodWaterDaysRemaining = 9,
                status = "Accepting Evacuees",
                phone = "+1 (555) 554-6000"
            ),
            InfrastructureItem(
                id = "INF-04",
                name = "Mission High School Relief Shelter #2",
                type = InfrastructureType.SHELTER,
                latitude = 37.7610,
                longitude = -122.4270,
                address = "3750 18th St",
                capacityPercentage = 48,
                capacityTotal = 450,
                capacityUsed = 216,
                bedsAvailable = 234,
                ambulancesCount = 1,
                foodWaterDaysRemaining = 11,
                status = "Accepting Evacuees",
                phone = "+1 (555) 241-6240"
            ),
            InfrastructureItem(
                id = "INF-05",
                name = "Central Disaster Operations Command (EOC)",
                type = InfrastructureType.EMERGENCY_CENTER,
                latitude = 37.7750,
                longitude = -122.4220,
                address = "1011 Turk St",
                capacityPercentage = 95,
                capacityTotal = 100,
                capacityUsed = 95,
                bedsAvailable = 0,
                ambulancesCount = 12,
                foodWaterDaysRemaining = 30,
                status = "Command Active 24/7",
                phone = "+1 (555) 558-3800"
            ),
            InfrastructureItem(
                id = "INF-06",
                name = "Station 1 Fire & Rescue Depot",
                type = InfrastructureType.FIRE_STATION,
                latitude = 37.7815,
                longitude = -122.4030,
                address = "935 Folsom St",
                capacityPercentage = 90,
                capacityTotal = 50,
                capacityUsed = 45,
                bedsAvailable = 0,
                ambulancesCount = 4,
                foodWaterDaysRemaining = 15,
                status = "All Units Deployed",
                phone = "+1 (555) 558-3200"
            )
        )

        _resources.value = listOf(
            ResourceItem(
                id = "RES-01",
                name = "Advance Life Support Ambulances",
                category = ResourceCategory.VEHICLES,
                availableCount = 7,
                deployedCount = 22,
                totalCount = 29,
                status = ResourceStatus.DEPLOYED,
                location = "Deployed across Sectors 1-4"
            ),
            ResourceItem(
                id = "RES-02",
                name = "Urban Search & Rescue Squads (USAR)",
                category = ResourceCategory.RESCUE_TEAMS,
                availableCount = 2,
                deployedCount = 8,
                totalCount = 10,
                status = ResourceStatus.DEPLOYED,
                location = "Active in Flood & Structural Collapse Zones"
            ),
            ResourceItem(
                id = "RES-03",
                name = "Emergency Trauma Medical Kits (Level 3)",
                category = ResourceCategory.MEDICAL_SUPPLIES,
                availableCount = 140,
                deployedCount = 360,
                totalCount = 500,
                status = ResourceStatus.AVAILABLE,
                location = "EOC Logistics Warehouse"
            ),
            ResourceItem(
                id = "RES-04",
                name = "Clean Drinking Water Tankers (5000L)",
                category = ResourceCategory.FOOD_RATIONS,
                availableCount = 3,
                deployedCount = 12,
                totalCount = 15,
                status = ResourceStatus.DEPLOYED,
                location = "Distributing at Shelter #1 and #2"
            ),
            ResourceItem(
                id = "RES-05",
                name = "Heavy Excavation & Debris Clearing Equipment",
                category = ResourceCategory.HEAVY_MACHINERY,
                availableCount = 1,
                deployedCount = 6,
                totalCount = 7,
                status = ResourceStatus.CRITICAL_SHORTAGE,
                location = "Mission St & Van Ness Corridors"
            )
        )

        _familyMembers.value = listOf(
            FamilyMember(
                id = "FAM-01",
                name = "Helen Miller (Mother)",
                relationship = "Mother",
                status = SquadMemberStatus.SAFE,
                lastKnownLocation = "Civic Auditorium Shelter #1",
                lastUpdated = "10 mins ago",
                phone = "+1 (555) 332-1100",
                batteryPercentage = 78
            ),
            FamilyMember(
                id = "FAM-02",
                name = "Tom Miller (Brother)",
                relationship = "Brother",
                status = SquadMemberStatus.SAFE,
                lastKnownLocation = "UCSF Parnassus Campus",
                lastUpdated = "25 mins ago",
                phone = "+1 (555) 771-4499",
                batteryPercentage = 62
            ),
            FamilyMember(
                id = "FAM-03",
                name = "Chloe Miller (Sister)",
                relationship = "Sister",
                status = SquadMemberStatus.UNKNOWN,
                lastKnownLocation = "Near 850 Howard St (Blackbox Zone)",
                lastUpdated = "1 hour ago",
                phone = "+1 (555) 998-2234",
                batteryPercentage = 12
            )
        )

        _broadcasts.value = listOf(
            EmergencyBroadcast(
                id = "EAS-901",
                title = "MANDATORY EVACUATION ORDER: SOMA & MARKET CORRIDOR",
                type = BroadcastType.EVACUATION,
                targetAudience = "All Citizens in Sectors 1 & 4",
                priority = "CRITICAL (EAS SIREN)",
                message = "Immediate evacuation mandated due to rapid storm inundation. Move westward towards Civic Shelter or elevated ground via 8th St.",
                issuedAt = "15 mins ago",
                issuedBy = "Mayor's Office of Emergency Services",
                isConfirmed = true
            ),
            EmergencyBroadcast(
                id = "EAS-902",
                title = "DRINKING WATER BOIL ADVISORY IN EFFECT",
                type = BroadcastType.HAZARD_ALERT,
                targetAudience = "All Metro Citizens",
                priority = "HIGH",
                message = "Water main pressure drop detected in Mission and Downtown. Boil tap water for minimum 3 minutes before consumption.",
                issuedAt = "45 mins ago",
                issuedBy = "Public Utilities Commission",
                isConfirmed = true
            ),
            EmergencyBroadcast(
                id = "EAS-903",
                title = "CIVIC AUDITORIUM SHELTER CAPACITY EXPANDED",
                type = BroadcastType.SHELTER_INFO,
                targetAudience = "All Displaced Citizens",
                priority = "ADVISORY",
                message = "Additional 250 cots, warm meals, and pediatric triage available at 99 Grove St.",
                issuedAt = "2 hours ago",
                issuedBy = "Red Cross & Disaster EOC",
                isConfirmed = true
            )
        )

        _chatMessages.value = listOf(
            ChatMessage(
                id = "MSG-01",
                senderName = "Disaster Operations Command",
                senderRole = "AUTHORITY",
                text = "EMERGENCY BROADCAST: Flash flood warning extended through 04:00 AM. Emergency rescue units active.",
                timestamp = "15 mins ago",
                isFromAuthority = true,
                isUrgent = true
            ),
            ChatMessage(
                id = "MSG-02",
                senderName = "Marcus Vance (Volunteer)",
                senderRole = "VOLUNTEER",
                text = "Citizen Sarah Jenkins, this is First Responder Marcus. I am en route to Market St with medical supplies. Stay on high floor.",
                timestamp = "5 mins ago",
                isSentByMe = false
            ),
            ChatMessage(
                id = "MSG-03",
                senderName = "You",
                senderRole = "CITIZEN",
                text = "Understood. We are on the 2nd floor balcony with flashlight visible.",
                timestamp = "3 mins ago",
                isSentByMe = true
            )
        )
    }

    // Role Navigation
    fun setRole(role: UserRole?) {
        _currentRole.value = role
    }

    // Connectivity
    fun toggleOnline() {
        val next = !_isOnline.value
        _isOnline.value = next
        if (next && _offlineQueuedReports.value.isNotEmpty()) {
            // Auto sync
            syncOfflineReports()
        }
    }

    fun syncOfflineReports() {
        val queued = _offlineQueuedReports.value
        val updated = _damageReports.value.toMutableList()
        queued.forEach { report ->
            updated.add(0, report.copy(syncStatus = SyncStatus.SENT))
        }
        _damageReports.value = updated
        _offlineQueuedReports.value = emptyList()
    }

    // Citizen SOS
    fun triggerCitizenSos(address: String = "Current GPS: 428 Market St") {
        _isCitizenSosActive.value = true
        val newSos = SosIncident(
            id = "SOS-" + (2850 + (0..99).random()),
            citizenName = "Citizen (You)",
            citizenPhone = "+1 (555) 012-9988",
            latitude = 37.7749,
            longitude = -122.4194,
            address = address,
            severity = Severity.CRITICAL,
            batteryPercentage = 74,
            timestamp = "Just now",
            status = SosStatus.NEW,
            description = "Direct SOS distress beacon activated by citizen device.",
            accuracyMeters = 3.2f
        )
        _currentSosIncident.value = newSos
        _sosList.value = listOf(newSos) + _sosList.value
    }

    fun cancelCitizenSos() {
        _isCitizenSosActive.value = false
        val current = _currentSosIncident.value
        if (current != null) {
            _sosList.value = _sosList.value.map {
                if (it.id == current.id) it.copy(status = SosStatus.RESOLVED) else it
            }
            _currentSosIncident.value = null
        }
    }

    // Volunteer Actions
    fun toggleVolunteerAvailability() {
        _isVolunteerAvailable.value = !_isVolunteerAvailable.value
    }

    fun setVolunteerBattery(pct: Int) {
        _volunteerBattery.value = pct
    }

    fun acceptAssignment(sosId: String) {
        val target = _sosList.value.find { it.id == sosId } ?: return
        val updated = target.copy(
            status = SosStatus.ASSIGNED,
            assignedVolunteerId = "VOL-9428",
            assignedVolunteerName = "Marcus Vance (You)"
        )
        _activeAssignedMission.value = updated
        _sosList.value = _sosList.value.map { if (it.id == sosId) updated else it }
    }

    fun markVolunteerArrived(sosId: String) {
        val target = _sosList.value.find { it.id == sosId } ?: return
        val updated = target.copy(status = SosStatus.ON_SITE)
        _activeAssignedMission.value = updated
        _sosList.value = _sosList.value.map { if (it.id == sosId) updated else it }
    }

    fun resolveMission(sosId: String) {
        val target = _sosList.value.find { it.id == sosId } ?: return
        val updated = target.copy(status = SosStatus.RESOLVED)
        _sosList.value = _sosList.value.map { if (it.id == sosId) updated else it }
        if (_activeAssignedMission.value?.id == sosId) {
            _activeAssignedMission.value = null
        }
    }

    // Damage Reports
    fun submitDamageReport(
        type: DisasterType,
        severity: Severity,
        address: String,
        description: String,
        hasVoiceNote: Boolean = false,
        reporterRole: String = "CITIZEN"
    ): DamageReport {
        val newReport = DamageReport(
            id = "RPT-" + (510 + (0..99).random()),
            reporterName = if (reporterRole == "VOLUNTEER") "Marcus Vance (Volunteer)" else "Citizen (You)",
            reporterRole = reporterRole,
            latitude = 37.7750 + ((-30..30).random() / 1000.0),
            longitude = -122.4180 + ((-30..30).random() / 1000.0),
            address = address,
            disasterType = type,
            severity = severity,
            description = description,
            timestamp = "Just now",
            photoCount = 1,
            hasVoiceNote = hasVoiceNote,
            voiceNoteDurationSec = if (hasVoiceNote) 8 else 0,
            syncStatus = if (_isOnline.value) SyncStatus.SENT else SyncStatus.STORED_OFFLINE,
            verificationStatus = VerificationStatus.PENDING_REVIEW
        )

        if (_isOnline.value) {
            _damageReports.value = listOf(newReport) + _damageReports.value
        } else {
            _offlineQueuedReports.value = listOf(newReport) + _offlineQueuedReports.value
        }
        return newReport
    }

    // Authority Actions
    fun assignVolunteerToSos(sosId: String, volunteerId: String) {
        val vol = _volunteers.value.find { it.id == volunteerId }
        _sosList.value = _sosList.value.map {
            if (it.id == sosId) {
                it.copy(
                    status = SosStatus.ASSIGNED,
                    assignedVolunteerId = volunteerId,
                    assignedVolunteerName = vol?.name ?: "Assigned Unit"
                )
            } else it
        }
    }

    fun escalateSos(sosId: String) {
        _sosList.value = _sosList.value.map {
            if (it.id == sosId) it.copy(severity = Severity.CRITICAL) else it
        }
    }

    fun verifyDamageReport(reportId: String) {
        _damageReports.value = _damageReports.value.map {
            if (it.id == reportId) it.copy(verificationStatus = VerificationStatus.VERIFIED) else it
        }
    }

    fun rejectDamageReport(reportId: String) {
        _damageReports.value = _damageReports.value.map {
            if (it.id == reportId) it.copy(verificationStatus = VerificationStatus.REJECTED) else it
        }
    }

    fun createHazardZone(
        title: String,
        type: DisasterType,
        severity: Severity,
        radiusMeters: Int,
        description: String,
        durationHours: Int
    ) {
        val newZone = HazardZone(
            id = "HZ-" + (110 + (0..99).random()),
            title = title,
            disasterType = type,
            severity = severity,
            centerLat = 37.7760 + ((-20..20).random() / 1000.0),
            centerLng = -122.4180 + ((-20..20).random() / 1000.0),
            radiusMeters = radiusMeters,
            description = description,
            activeDurationHours = durationHours,
            createdAt = "Just now",
            affectedCitizensCount = (radiusMeters * 3.4).toInt(),
            isActive = true
        )
        _hazardZones.value = listOf(newZone) + _hazardZones.value
    }

    fun sendEmergencyBroadcast(
        title: String,
        type: BroadcastType,
        targetAudience: String,
        priority: String,
        message: String
    ) {
        val newBroadcast = EmergencyBroadcast(
            id = "EAS-" + (910 + (0..99).random()),
            title = title,
            type = type,
            targetAudience = targetAudience,
            priority = priority,
            message = message,
            issuedAt = "Just now",
            issuedBy = "Emergency Incident Commander (You)",
            isConfirmed = true
        )
        _broadcasts.value = listOf(newBroadcast) + _broadcasts.value
    }

    fun addFamilyCheckIn(name: String, relation: String, phone: String, status: SquadMemberStatus) {
        val newMember = FamilyMember(
            id = "FAM-" + (10 + (0..99).random()),
            name = name,
            relationship = relation,
            status = status,
            lastKnownLocation = "Checked in near current sector",
            lastUpdated = "Just now",
            phone = phone,
            batteryPercentage = 88
        )
        _familyMembers.value = _familyMembers.value + newMember
    }

    fun updateMyFamilyStatus(status: SquadMemberStatus) {
        // Broadcast user's check in
        val updated = _familyMembers.value.toMutableList()
        val index = updated.indexOfFirst { it.name.contains("You") }
        if (index >= 0) {
            updated[index] = updated[index].copy(status = status, lastUpdated = "Just now")
        } else {
            updated.add(
                0,
                FamilyMember(
                    id = "FAM-ME",
                    name = "You (Self)",
                    relationship = "Self",
                    status = status,
                    lastKnownLocation = "Sector 4 — 428 Market St",
                    lastUpdated = "Just now",
                    phone = "+1 (555) 012-9988",
                    batteryPercentage = 74
                )
            )
        }
        _familyMembers.value = updated
    }

    fun sendChatMessage(text: String, isVoice: Boolean = false, durationSec: Int = 0) {
        val role = _currentRole.value ?: UserRole.CITIZEN
        val msg = ChatMessage(
            id = "MSG-" + System.currentTimeMillis(),
            senderName = when (role) {
                UserRole.CITIZEN -> "You (Citizen)"
                UserRole.VOLUNTEER -> "Marcus Vance (Volunteer)"
                UserRole.AUTHORITY -> "Incident Command (Authority)"
            },
            senderRole = role.name,
            text = text,
            timestamp = "Just now",
            isVoiceNote = isVoice,
            voiceDurationSec = durationSec,
            isSentByMe = true,
            isFromAuthority = role == UserRole.AUTHORITY
        )
        _chatMessages.value = _chatMessages.value + msg
    }
}
