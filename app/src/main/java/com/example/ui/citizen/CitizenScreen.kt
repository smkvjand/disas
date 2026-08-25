package com.example.ui.citizen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DisasterRepository
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

enum class CitizenTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    MAP("MAP", Icons.Default.Map),
    REPORT("REPORT", Icons.Default.AddAlert),
    VOLUNTEERS("VOLUNTEERS", Icons.Default.PeopleAlt),
    MESSAGES("MESSAGES", Icons.Default.Chat),
    SQUAD("SQUAD", Icons.Default.FamilyRestroom),
    PROFILE("PROFILE", Icons.Default.Person)
}

@Composable
fun CitizenMainScreen(
    repository: DisasterRepository,
    onSwitchRoleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(CitizenTab.MAP) }
    var showSosModal by remember { mutableStateOf(false) }

    val isOnline by repository.isOnline.collectAsState()
    val isCitizenSosActive by repository.isCitizenSosActive.collectAsState()
    val currentSosIncident by repository.currentSosIncident.collectAsState()
    val sosList by repository.sosList.collectAsState()
    val damageReports by repository.damageReports.collectAsState()
    val volunteers by repository.volunteers.collectAsState()
    val hazardZones by repository.hazardZones.collectAsState()
    val infrastructures by repository.infrastructures.collectAsState()
    val familyMembers by repository.familyMembers.collectAsState()
    val broadcasts by repository.broadcasts.collectAsState()
    val chatMessages by repository.chatMessages.collectAsState()
    val offlineQueuedReports by repository.offlineQueuedReports.collectAsState()

    Scaffold(
        topBar = {
            EmergencyTopBar(
                title = currentTab.label,
                currentRole = UserRole.CITIZEN,
                isOnline = isOnline,
                onRoleSwitchClick = onSwitchRoleClick,
                onToggleOnlineClick = { repository.toggleOnline() },
                batteryPercentage = 74,
                onRoleSelected = { repository.setRole(it) }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // PERSISTENT LARGE RED SOS BUTTON (Above bottom nav)
                Surface(
                    color = Color.Transparent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Button(
                        onClick = {
                            if (!isCitizenSosActive) {
                                repository.triggerCitizenSos()
                            }
                            showSosModal = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCitizenSosActive) EmergencyRedLight else EmergencyRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("citizen_persistent_sos_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "SOS",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isCitizenSosActive) "SOS ACTIVE — VIEW RESCUE STATUS" else "I'M IN DANGER — SOS",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    letterSpacing = 0.8.sp
                                )
                            )
                        }
                    }
                }

                // CITIZEN BOTTOM NAVIGATION (MAP | REPORT | VOLUNTEERS | MESSAGES | SQUAD | PROFILE)
                Surface(
                    color = SurfaceCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(58.dp)
                    ) {
                        CitizenTab.values().forEach { tab ->
                            val isSelected = currentTab == tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentTab = tab },
                                icon = {
                                    Icon(
                                        imageVector = tab.icon,
                                        contentDescription = tab.label,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 9.sp,
                                            letterSpacing = 0.3.sp
                                        )
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = InfoBlue,
                                    selectedTextColor = InfoBlue,
                                    indicatorColor = InfoBlueContainer,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("citizen_nav_${tab.name}")
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundLight)
        ) {
            when (currentTab) {
                CitizenTab.MAP -> CitizenMapScreen(
                    repository = repository,
                    sosList = sosList,
                    damageReports = damageReports,
                    volunteers = volunteers,
                    hazardZones = hazardZones,
                    infrastructures = infrastructures,
                    offlineQueued = offlineQueuedReports.size
                )
                CitizenTab.REPORT -> CitizenDamageReportFlow(
                    repository = repository,
                    isOnline = isOnline,
                    onReportSubmitted = { currentTab = CitizenTab.MAP }
                )
                CitizenTab.VOLUNTEERS -> CitizenVolunteerHelpScreen(
                    repository = repository,
                    volunteers = volunteers
                )
                CitizenTab.MESSAGES -> CitizenMessagesScreen(
                    repository = repository,
                    chatMessages = chatMessages,
                    broadcasts = broadcasts
                )
                CitizenTab.SQUAD -> CitizenSquadScreen(
                    repository = repository,
                    familyMembers = familyMembers
                )
                CitizenTab.PROFILE -> CitizenProfileScreen(
                    isOnline = isOnline,
                    isSosActive = isCitizenSosActive,
                    onTriggerSos = {
                        repository.triggerCitizenSos()
                        showSosModal = true
                    }
                )
            }

            // SOS Full Screen Sheet / Modal
            if (showSosModal || isCitizenSosActive) {
                CitizenSosModal(
                    sosIncident = currentSosIncident,
                    onCancelSos = {
                        repository.cancelCitizenSos()
                        showSosModal = false
                    },
                    onDismiss = { showSosModal = false }
                )
            }
        }
    }
}

// 1. CITIZEN HOME (LARGE MAP DOMINATED)
@Composable
fun CitizenMapScreen(
    repository: DisasterRepository,
    sosList: List<SosIncident>,
    damageReports: List<DamageReport>,
    volunteers: List<Volunteer>,
    hazardZones: List<HazardZone>,
    infrastructures: List<InfrastructureItem>,
    offlineQueued: Int
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (offlineQueued > 0) {
            OfflineNoticeBanner(
                queuedCount = offlineQueued,
                onSyncClick = { repository.syncOfflineReports() },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        // Dominant Map
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            InteractiveMapView(
                sosList = sosList,
                damageReports = damageReports,
                volunteers = volunteers,
                hazardZones = hazardZones,
                infrastructures = infrastructures,
                userRole = UserRole.CITIZEN
            )
        }

        // Quick Nearby Shelter & Emergency Card Strip
        Surface(
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "NEAREST SAFE SHELTERS & HOSPITALS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = PrimaryNavy
                    )
                    Text(
                        text = "AUTO UPDATING",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        color = SafeGreen
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        QuickFacilityCard(
                            name = "Civic Auditorium Shelter #1",
                            address = "99 Grove St • 0.4 km away",
                            badge = "216 BEDS AVAILABLE",
                            badgeColor = SafeGreen,
                            badgeContainer = SafeGreenContainer,
                            icon = Icons.Default.NightShelter
                        )
                    }
                    item {
                        QuickFacilityCard(
                            name = "General Trauma Hospital",
                            address = "1001 Potrero Ave • 1.2 km away",
                            badge = "ER CAPACITY: 86%",
                            badgeColor = InfoBlue,
                            badgeContainer = InfoBlueContainer,
                            icon = Icons.Default.LocalHospital
                        )
                    }
                    item {
                        QuickFacilityCard(
                            name = "Mission High Shelter #2",
                            address = "3750 18th St • 1.8 km away",
                            badge = "234 BEDS AVAILABLE",
                            badgeColor = SafeGreen,
                            badgeContainer = SafeGreenContainer,
                            icon = Icons.Default.NightShelter
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickFacilityCard(
    name: String,
    address: String,
    badge: String,
    badgeColor: Color,
    badgeContainer: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = SurfaceSecondary,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        modifier = Modifier.width(220.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = TextMuted,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                StatusBadge(text = badge, color = badgeColor, containerColor = badgeContainer)
            }
        }
    }
}

// 2. CITIZEN SOS MODAL SCREEN
@Composable
fun CitizenSosModal(
    sosIncident: SosIncident?,
    onCancelSos: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(EmergencyRedContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "SOS",
                        tint = EmergencyRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SOS DISTRESS ACTIVE",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = EmergencyRed
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmergencyRedContainer,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmergencyRed.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "SOS BROADCAST SENT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            ),
                            color = EmergencyRedOnContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "✓ GPS Coordinates shared (37.7749° N, 122.4194° W)",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = EmergencyRedOnContainer
                        )
                        Text(
                            text = "✓ Incident Command Center notified",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = EmergencyRedOnContainer
                        )
                        Text(
                            text = "✓ 4 Verified nearby volunteers alerted",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = EmergencyRedOnContainer
                        )
                        Text(
                            text = "✓ Battery telemetry: 74% • GPS Accuracy: ±3.2m",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = EmergencyRedOnContainer
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceSecondary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "EMERGENCY ADVICE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy
                            )
                        )
                        Text(
                            text = "Stay in a safe elevated position. Keep your phone screen brightness low to save battery. First responders are tracking your location.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextSecondary
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { /* Simulated dialer */ },
                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("call_911_button")
            ) {
                Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("CALL 911 / EOC")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onCancelSos,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                modifier = Modifier.testTag("cancel_sos_button")
            ) {
                Text("CANCEL SOS")
            }
        }
    )
}

// 3. CITIZEN DAMAGE REPORT FLOW (5 STEPS)
@Composable
fun CitizenDamageReportFlow(
    repository: DisasterRepository,
    isOnline: Boolean,
    onReportSubmitted: () -> Unit
) {
    var selectedDisaster by remember { mutableStateOf(DisasterType.FLOOD) }
    var selectedSeverity by remember { mutableStateOf(Severity.HIGH) }
    var addressText by remember { mutableStateOf("428 Market St (Current GPS)") }
    var descriptionText by remember { mutableStateOf("") }
    var hasVoiceNote by remember { mutableStateOf(false) }
    var isRecordingVoice by remember { mutableStateOf(false) }
    var hasPhoto by remember { mutableStateOf(false) }
    var showReceiptDialog by remember { mutableStateOf(false) }
    var submittedReport by remember { mutableStateOf<DamageReport?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "REPORT DISASTER OR DAMAGE",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = PrimaryNavy
            )
            Text(
                text = "Provide immediate situational triage data to emergency response teams.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        // STEP 1: SELECT LOCATION
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "1. SELECT INCIDENT LOCATION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = PrimaryNavy
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = addressText,
                        onValueChange = { addressText = it },
                        modifier = Modifier.fillMaxWidth().testTag("report_location_input"),
                        label = { Text("Incident Address / GPS Pin") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = EmergencyRed)
                        },
                        trailingIcon = {
                            TextButton(onClick = { addressText = "37.7749° N, 122.4194° W (Locked)" }) {
                                Text("GPS", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    )
                }
            }
        }

        // STEP 2: SELECT DISASTER TYPE
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "2. SELECT DISASTER TYPE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = PrimaryNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        DisasterType.values().toList().chunked(2).forEach { rowTypes: List<DisasterType> ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowTypes.forEach { type: DisasterType ->
                                    val isSelected = selectedDisaster == type
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) PrimaryNavy else SurfaceSecondary,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) PrimaryNavy else BorderLight
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedDisaster = type }
                                            .testTag("disaster_type_${type.name}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = when (type) {
                                                    DisasterType.FLOOD -> Icons.Default.WaterDrop
                                                    DisasterType.FIRE -> Icons.Default.LocalFireDepartment
                                                    DisasterType.LANDSLIDE -> Icons.Default.Terrain
                                                    DisasterType.BUILDING_COLLAPSE -> Icons.Default.DomainDisabled
                                                    DisasterType.ROAD_BLOCKED -> Icons.Default.Block
                                                    DisasterType.POWER_LINE -> Icons.Default.Bolt
                                                    DisasterType.EARTHQUAKE -> Icons.Default.Vibration
                                                    DisasterType.CHEMICAL_HAZARD -> Icons.Default.Warning
                                                    else -> Icons.Default.Emergency
                                                },
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else EmergencyRed,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = type.displayName,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    fontSize = 11.sp
                                                ),
                                                color = if (isSelected) Color.White else TextPrimary,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // STEP 3: SELECT SEVERITY
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "3. SELECT SEVERITY LEVEL",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = PrimaryNavy
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Severity.values().forEach { sev ->
                            val isSelected = selectedSeverity == sev
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) sev.color else sev.containerColor,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) sev.color else sev.color.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedSeverity = sev }
                                    .testTag("severity_${sev.name}")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = sev.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp
                                        ),
                                        color = if (isSelected) Color.White else sev.onContainerColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // STEP 4: ADD EVIDENCE & DETAILS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "4. ADD EVIDENCE & DESCRIPTION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = PrimaryNavy
                    )

                    OutlinedTextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        modifier = Modifier.fillMaxWidth().testTag("report_description_input"),
                        label = { Text("Short description of damage, victims, hazard...") },
                        minLines = 3
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Photo Attach Button
                        OutlinedButton(
                            onClick = { hasPhoto = !hasPhoto },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (hasPhoto) SafeGreenContainer else SurfaceSecondary
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (hasPhoto) SafeGreen else BorderLight
                            ),
                            modifier = Modifier.weight(1f).testTag("attach_photo_button")
                        ) {
                            Icon(
                                imageVector = if (hasPhoto) Icons.Default.Check else Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = if (hasPhoto) SafeGreen else PrimaryNavy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (hasPhoto) "PHOTO ATTACHED" else "TAKE PHOTO",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                            )
                        }

                        // Voice Note Simulator
                        OutlinedButton(
                            onClick = {
                                isRecordingVoice = !isRecordingVoice
                                if (!isRecordingVoice) hasVoiceNote = true
                            },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isRecordingVoice) EmergencyRedContainer else if (hasVoiceNote) SafeGreenContainer else SurfaceSecondary
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isRecordingVoice) EmergencyRed else if (hasVoiceNote) SafeGreen else BorderLight
                            ),
                            modifier = Modifier.weight(1f).testTag("record_voice_button")
                        ) {
                            Icon(
                                imageVector = if (isRecordingVoice) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = null,
                                tint = if (isRecordingVoice) EmergencyRed else if (hasVoiceNote) SafeGreen else PrimaryNavy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isRecordingVoice) "RECORDING..." else if (hasVoiceNote) "VOICE ATTACHED" else "VOICE NOTE",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                            )
                        }
                    }
                }
            }
        }

        // STEP 5: SUBMIT REPORT BUTTON
        item {
            Button(
                onClick = {
                    val report = repository.submitDamageReport(
                        type = selectedDisaster,
                        severity = selectedSeverity,
                        address = addressText.ifEmpty { "428 Market St" },
                        description = descriptionText.ifEmpty { "Reported ${selectedDisaster.displayName} at this location." },
                        hasVoiceNote = hasVoiceNote,
                        reporterRole = "CITIZEN"
                    )
                    submittedReport = report
                    showReceiptDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_report_button")
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SUBMIT DAMAGE REPORT",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }

    // Submission Receipt Dialog
    if (showReceiptDialog && submittedReport != null) {
        val r = submittedReport!!
        AlertDialog(
            onDismissRequest = {
                showReceiptDialog = false
                onReportSubmitted()
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SafeGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "REPORT RECEIVED",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "Report Reference: ${r.id}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    Text(text = "Disaster: ${r.disasterType.displayName}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Severity: ${r.severity.label}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Location: ${r.address}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    StatusBadge(
                        text = if (isOnline) "STATUS: SENT TO DISASTER COMMAND" else "STATUS: STORED OFFLINE (SYNC QUEUED)",
                        color = if (isOnline) SafeGreen else WarningAmber,
                        containerColor = if (isOnline) SafeGreenContainer else WarningAmberContainer
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showReceiptDialog = false
                        onReportSubmitted()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                ) {
                    Text("RETURN TO MAP")
                }
            }
        )
    }
}

// 4. CITIZEN VOLUNTEER HELP (I NEED HELP & I CAN HELP)
@Composable
fun CitizenVolunteerHelpScreen(
    repository: DisasterRepository,
    volunteers: List<Volunteer>
) {
    var volunteerTab by remember { mutableStateOf(0) } // 0: I Need Help, 1: I Can Help

    // I Need Help form state
    var selectedAssistance by remember { mutableStateOf("Medical / First Aid") }
    var helpDescription by remember { mutableStateOf("") }
    var requestSent by remember { mutableStateOf(false) }

    // I Can Help form state
    var isRegisteredVolunteer by remember { mutableStateOf(false) }
    var selectedSkills by remember { mutableStateOf(setOf("First Aid", "Physical Labor")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Large Top Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { volunteerTab = 0 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (volunteerTab == 0) EmergencyRed else SurfaceSecondary,
                    contentColor = if (volunteerTab == 0) Color.White else TextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("i_need_help_tab")
            ) {
                Icon(imageVector = Icons.Default.Emergency, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("I NEED HELP", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black))
            }

            Button(
                onClick = { volunteerTab = 1 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (volunteerTab == 1) SafeGreen else SurfaceSecondary,
                    contentColor = if (volunteerTab == 1) Color.White else TextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("i_can_help_tab")
            ) {
                Icon(imageVector = Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("I CAN HELP", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black))
            }
        }

        if (volunteerTab == 0) {
            // I NEED HELP SECTION
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "REQUEST VOLUNTEER ASSISTANCE",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                color = PrimaryNavy
                            )

                            val assistanceTypes = listOf(
                                "Medical / First Aid",
                                "Evacuation & Escort",
                                "Food & Clean Water",
                                "Debris / Fallen Tree",
                                "Elderly Assistance"
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                assistanceTypes.forEach { type ->
                                    val isSelected = selectedAssistance == type
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSelected) InfoBlueContainer else SurfaceSecondary,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) InfoBlue else BorderLight
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedAssistance = type }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = { selectedAssistance = type },
                                                colors = RadioButtonDefaults.colors(selectedColor = InfoBlue)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = type,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = helpDescription,
                                onValueChange = { helpDescription = it },
                                label = { Text("Details (floor number, number of people...)") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )

                            Button(
                                onClick = { requestSent = true },
                                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("REQUEST NEARBY VOLUNTEER")
                            }

                            if (requestSent) {
                                Surface(
                                    color = SafeGreenContainer,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "✓ Request broadcasted to 5 nearby active volunteers.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = SafeGreenOnContainer,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    SectionHeader(title = "AVAILABLE NEARBY VOLUNTEERS")
                }

                items(volunteers.filter { it.isAvailable }) { vol ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = vol.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (vol.isVerified) {
                                        StatusBadge(
                                            text = "VERIFIED",
                                            color = SafeGreen,
                                            containerColor = SafeGreenContainer
                                        )
                                    }
                                }
                                Text(
                                    text = "Skills: ${vol.skills.joinToString(", ")}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = TextSecondary
                                )
                                Text(
                                    text = "Distance: ${vol.distanceKm} km away • Rating: ★${vol.rating}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = TextMuted
                                )
                            }
                            Button(
                                onClick = { requestSent = true },
                                colors = ButtonDefaults.buttonColors(containerColor = InfoBlue),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("REQUEST", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        } else {
            // I CAN HELP (REGISTER TEMPORARY VOLUNTEER)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "REGISTER AS TEMPORARY VOLUNTEER",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                color = PrimaryNavy
                            )
                            Text(
                                text = "Help your community with basic assistance during emergency surge periods.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )

                            val skillOptions = listOf(
                                "First Aid / CPR",
                                "4x4 Vehicle Transport",
                                "Boat / Water Rescue",
                                "Physical Labor & Sandbags",
                                "Food & Water Logistics",
                                "Language Translation"
                            )

                            Text(
                                text = "Select your capabilities:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            skillOptions.forEach { skill ->
                                val isChecked = selectedSkills.contains(skill)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedSkills = if (isChecked) selectedSkills - skill else selectedSkills + skill
                                        }
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = {
                                            selectedSkills = if (it) selectedSkills + skill else selectedSkills - skill
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = skill, style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            Button(
                                onClick = { isRegisteredVolunteer = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("REGISTER AS ACTIVE TEMPORARY VOLUNTEER")
                            }

                            if (isRegisteredVolunteer) {
                                Surface(
                                    color = SafeGreenContainer,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "✓ You are active as Volunteer VOL-TMP-${(100..999).random()}. You will receive local assistance requests.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = SafeGreenOnContainer,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 5. CITIZEN MESSAGES SCREEN
@Composable
fun CitizenMessagesScreen(
    repository: DisasterRepository,
    chatMessages: List<ChatMessage>,
    broadcasts: List<EmergencyBroadcast>
) {
    var messageInput by remember { mutableStateOf("") }
    var isSimulatingVoice by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // High Priority Broadcast Alert Banner
        if (broadcasts.isNotEmpty()) {
            val topBroadcast = broadcasts.first()
            Surface(
                color = EmergencyRedContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, EmergencyRed.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = EmergencyRed)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = topBroadcast.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            ),
                            color = EmergencyRedOnContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = topBroadcast.message,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = EmergencyRedOnContainer
                    )
                }
            }
        }

        // Chat Message List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatMessages) { msg ->
                val isMe = msg.isSentByMe
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isMe) PrimaryNavy else if (msg.isFromAuthority) EmergencyRedContainer else SurfaceSecondary,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isMe) PrimaryNavy else BorderLight
                        ),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = msg.senderName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = if (isMe) Color(0xFF94A3B8) else if (msg.isFromAuthority) EmergencyRed else InfoBlue
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            if (msg.isVoiceNote) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = if (isMe) Color.White else PrimaryNavy,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Voice Note (${msg.voiceDurationSec}s)",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isMe) Color.White else TextPrimary
                                        )
                                    )
                                }
                            } else {
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isMe) Color.White else TextPrimary
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = msg.timestamp,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 9.sp,
                                    color = if (isMe) Color(0xFF94A3B8) else TextMuted
                                )
                            )
                        }
                    }
                }
            }
        }

        // Message Input Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutlinedTextField(
                value = messageInput,
                onValueChange = { messageInput = it },
                placeholder = { Text("Emergency message to responders...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("citizen_message_input"),
                shape = RoundedCornerShape(8.dp)
            )

            IconButton(
                onClick = {
                    isSimulatingVoice = !isSimulatingVoice
                    if (!isSimulatingVoice) {
                        repository.sendChatMessage("Emergency voice update.", isVoice = true, durationSec = 6)
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSimulatingVoice) EmergencyRedContainer else SurfaceSecondary)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Note",
                    tint = if (isSimulatingVoice) EmergencyRed else PrimaryNavy
                )
            }

            IconButton(
                onClick = {
                    if (messageInput.isNotBlank()) {
                        repository.sendChatMessage(messageInput)
                        messageInput = ""
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryNavy)
                    .testTag("send_message_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}

// 6. CITIZEN FAMILY / SQUAD SCREEN
@Composable
fun CitizenSquadScreen(
    repository: DisasterRepository,
    familyMembers: List<FamilyMember>
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ONE-TAP "I'M SAFE" CHECK-IN BUTTON
        Card(
            colors = CardDefaults.cardColors(containerColor = SafeGreenContainer),
            border = androidx.compose.foundation.BorderStroke(1.dp, SafeGreen.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "FAMILY & SQUAD CHECK-IN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = SafeGreenOnContainer
                    )
                )
                Text(
                    text = "Let your family members and authorities know you are unharmed.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = SafeGreenOnContainer
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { repository.updateMyFamilyStatus(SquadMemberStatus.SAFE) },
                        colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f).testTag("im_safe_button")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("I'M SAFE")
                    }

                    Button(
                        onClick = { repository.updateMyFamilyStatus(SquadMemberStatus.NEEDS_HELP) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f).testTag("i_need_help_squad_button")
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("I NEED HELP")
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SectionHeader(
                title = "FAMILY / SQUAD MEMBERS",
                subtitle = "Status and last known coordinates"
            )
            TextButton(onClick = { showAddDialog = true }) {
                Text("+ ADD MEMBER", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(familyMembers) { member ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = member.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                StatusBadge(
                                    text = member.status.label,
                                    color = member.status.color,
                                    containerColor = member.status.containerColor
                                )
                            }
                            Text(
                                text = "Last location: ${member.lastKnownLocation}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextSecondary
                            )
                            Text(
                                text = "Updated: ${member.lastUpdated} • Battery: ${member.batteryPercentage}%",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = TextMuted
                            )
                        }

                        IconButton(
                            onClick = { /* simulated call */ },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SurfaceSecondary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Call",
                                tint = PrimaryNavy,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var memberName by remember { mutableStateOf("") }
        var memberRelation by remember { mutableStateOf("Family") }
        var memberPhone by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Squad Member", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = memberName,
                        onValueChange = { memberName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = memberRelation,
                        onValueChange = { memberRelation = it },
                        label = { Text("Relationship (e.g. Spouse, Child)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = memberPhone,
                        onValueChange = { memberPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (memberName.isNotBlank()) {
                            repository.addFamilyCheckIn(
                                name = memberName,
                                relation = memberRelation,
                                phone = memberPhone.ifEmpty { "+1 (555) 000-0000" },
                                status = SquadMemberStatus.UNKNOWN
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                ) {
                    Text("ADD")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

// 7. CITIZEN PROFILE SCREEN
@Composable
fun CitizenProfileScreen(
    isOnline: Boolean,
    isSosActive: Boolean,
    onTriggerSos: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(EmergencyRedContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = EmergencyRed,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Citizen Profile (You)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = PrimaryNavy
                            )
                            Text(
                                text = "Device ID: CTZ-9821 • Phone: +1 (555) 012-9988",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(title = "EMERGENCY PREFERENCES & TELEMETRY")
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProfileInfoRow(label = "Emergency Blood Type", value = "O+ (Positive)")
                    ProfileInfoRow(label = "Medical Allergies", value = "Penicillin, Latex")
                    ProfileInfoRow(label = "Primary Emergency Contact", value = "Helen Miller (+1 555-332-1100)")
                    ProfileInfoRow(label = "Offline Mesh Relay", value = "Ready (BLE / Wi-Fi Direct)")
                    ProfileInfoRow(label = "Location Tracking", value = "High Accuracy (GPS ±3.2m)")
                }
            }
        }

        item {
            SectionHeader(title = "NATIONAL EMERGENCY HOTLINES")
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceSecondary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "• 911 — Police, Fire & Emergency Paramedic Services", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    Text(text = "• 311 — Municipal Public Works & Non-Emergency City Services", style = MaterialTheme.typography.bodySmall)
                    Text(text = "• 211 — Community Shelter, Food & Disaster Relief Directory", style = MaterialTheme.typography.bodySmall)
                    Text(text = "• 988 — Suicide & Crisis Lifeline 24/7", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = PrimaryNavy)
    }
}
