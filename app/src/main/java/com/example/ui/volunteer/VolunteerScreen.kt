package com.example.ui.volunteer

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

enum class VolunteerTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HOME("DASHBOARD", Icons.Default.Dashboard),
    MAP("MAP", Icons.Default.Map),
    ASSIGNMENTS("ASSIGNMENTS", Icons.Default.Assignment),
    REPORT("REPORT", Icons.Default.AddAlert),
    PROFILE("PROFILE", Icons.Default.Badge)
}

@Composable
fun VolunteerMainScreen(
    repository: DisasterRepository,
    onSwitchRoleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(VolunteerTab.HOME) }

    val isOnline by repository.isOnline.collectAsState()
    val isAvailable by repository.isVolunteerAvailable.collectAsState()
    val volunteerBattery by repository.volunteerBattery.collectAsState()
    val sosList by repository.sosList.collectAsState()
    val damageReports by repository.damageReports.collectAsState()
    val volunteers by repository.volunteers.collectAsState()
    val hazardZones by repository.hazardZones.collectAsState()
    val infrastructures by repository.infrastructures.collectAsState()
    val activeMission by repository.activeAssignedMission.collectAsState()

    var showLowBatteryAlert by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                EmergencyTopBar(
                    title = currentTab.label,
                    currentRole = UserRole.VOLUNTEER,
                    isOnline = isOnline,
                    onRoleSwitchClick = onSwitchRoleClick,
                    onToggleOnlineClick = { repository.toggleOnline() },
                    batteryPercentage = volunteerBattery,
                    onRoleSelected = { repository.setRole(it) }
                )

                // VOLUNTEER OPERATIONAL STATUS SUBHEADER (AVAILABLE / OFFLINE TOGGLE)
                Surface(
                    color = SurfaceCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isAvailable) SafeGreen else MutedSlate)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isAvailable) "STATUS: AVAILABLE FOR RESPONSE" else "STATUS: OFF-DUTY / RESTING",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.3.sp
                                    ),
                                    color = if (isAvailable) SafeGreen else TextSecondary
                                )
                                Text(
                                    text = "VOL-9428 • Sector 4 Mid-Market Rescue Unit",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    color = TextMuted
                                )
                            }
                        }

                        Button(
                            onClick = { repository.toggleVolunteerAvailability() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAvailable) PrimaryNavy else SafeGreen
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("toggle_volunteer_availability")
                        ) {
                            Text(
                                text = if (isAvailable) "GO OFFLINE" else "GO AVAILABLE",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
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
                    VolunteerTab.values().forEach { tab ->
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
                            modifier = Modifier.testTag("volunteer_nav_${tab.name}")
                        )
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
                VolunteerTab.HOME -> VolunteerDashboardScreen(
                    repository = repository,
                    activeMission = activeMission,
                    sosList = sosList,
                    damageReports = damageReports,
                    hazardZones = hazardZones,
                    infrastructures = infrastructures,
                    onNavigateToAssignments = { currentTab = VolunteerTab.ASSIGNMENTS },
                    onNavigateToMap = { currentTab = VolunteerTab.MAP },
                    onNavigateToReport = { currentTab = VolunteerTab.REPORT }
                )
                VolunteerTab.MAP -> Box(modifier = Modifier.fillMaxSize()) {
                    InteractiveMapView(
                        sosList = sosList,
                        damageReports = damageReports,
                        volunteers = volunteers,
                        hazardZones = hazardZones,
                        infrastructures = infrastructures,
                        userRole = UserRole.VOLUNTEER
                    )
                }
                VolunteerTab.ASSIGNMENTS -> VolunteerAssignmentsScreen(
                    repository = repository,
                    sosList = sosList,
                    activeMission = activeMission
                )
                VolunteerTab.REPORT -> VolunteerFieldReportScreen(
                    repository = repository,
                    onReportSubmitted = { currentTab = VolunteerTab.HOME }
                )
                VolunteerTab.PROFILE -> VolunteerProfileScreen(
                    repository = repository,
                    volunteerBattery = volunteerBattery,
                    isAvailable = isAvailable,
                    activeMission = activeMission
                )
            }
        }
    }
}

// 1. VOLUNTEER DASHBOARD
@Composable
fun VolunteerDashboardScreen(
    repository: DisasterRepository,
    activeMission: SosIncident?,
    sosList: List<SosIncident>,
    damageReports: List<DamageReport>,
    hazardZones: List<HazardZone>,
    infrastructures: List<InfrastructureItem>,
    onNavigateToAssignments: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToReport: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ACTIVE ASSIGNMENT HERO CARD (If assigned)
        if (activeMission != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmergencyRedContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmergencyRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(EmergencyRed)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ACTIVE RESCUE ASSIGNMENT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp
                                    ),
                                    color = EmergencyRedOnContainer
                                )
                            }
                            SeverityBadge(severity = activeMission.severity)
                        }

                        Text(
                            text = "${activeMission.id} • ${activeMission.citizenName}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                            color = PrimaryNavy
                        )
                        Text(
                            text = "Location: ${activeMission.address} (1.4 km away)",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = TextSecondary
                        )
                        Text(
                            text = activeMission.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryNavy
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = onNavigateToAssignments,
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f).testTag("view_mission_actions_button")
                            ) {
                                Icon(imageVector = Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("NAVIGATE & ACTIONS", style = MaterialTheme.typography.labelSmall)
                            }

                            OutlinedButton(
                                onClick = { repository.resolveMission(activeMission.id) },
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(0.8f)
                            ) {
                                Text("RESOLVE", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }

        // QUICK STATS OVERVIEW CARDS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickStatCard(
                    title = "NEARBY SOS",
                    value = "${sosList.count { it.status != SosStatus.RESOLVED }} Active",
                    color = EmergencyRed,
                    containerColor = EmergencyRedContainer,
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    title = "HAZARDS",
                    value = "${hazardZones.size} Zones",
                    color = SeverityOrange,
                    containerColor = SeverityOrangeContainer,
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    title = "SHELTERS",
                    value = "2 Open",
                    color = SafeGreen,
                    containerColor = SafeGreenContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // QUICK FIELD ACTIONS
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
                        text = "FIELD FIRST RESPONDER ACTIONS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = PrimaryNavy
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onNavigateToReport,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.AddAlert, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("SUBMIT REPORT", style = MaterialTheme.typography.labelSmall)
                        }

                        Button(
                            onClick = onNavigateToMap,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("LIVE MAP", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // NEARBY UNASSIGNED SOS QUEUE
        item {
            SectionHeader(
                title = "NEARBY SOS DISPATCH QUEUE",
                subtitle = "Citizens requiring immediate assistance",
                actionLabel = "VIEW ALL",
                onActionClick = onNavigateToAssignments
            )
        }

        items(sosList.filter { it.status == SosStatus.NEW }) { sos ->
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
                                text = "${sos.id} • ${sos.citizenName}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            SeverityBadge(severity = sos.severity)
                        }
                        Text(
                            text = sos.address,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextSecondary
                        )
                        Text(
                            text = "Battery: ${sos.batteryPercentage}% • ${sos.timestamp}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = TextMuted
                        )
                    }

                    Button(
                        onClick = { repository.acceptAssignment(sos.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("ACCEPT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black))
                    }
                }
            }
        }
    }
}

// 2. VOLUNTEER ASSIGNMENTS SCREEN
@Composable
fun VolunteerAssignmentsScreen(
    repository: DisasterRepository,
    sosList: List<SosIncident>,
    activeMission: SosIncident?
) {
    var turnByTurnActive by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "INCIDENT ASSIGNMENTS",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = PrimaryNavy
            )
            Text(
                text = "Accept, navigate, triage and resolve citizen distress calls.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        if (activeMission != null) {
            item {
                SectionHeader(title = "CURRENT ACCEPTED MISSION")
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, EmergencyRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${activeMission.id} — ${activeMission.citizenName}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                color = PrimaryNavy
                            )
                            StatusBadge(
                                text = activeMission.status.label,
                                color = activeMission.status.color,
                                containerColor = activeMission.status.containerColor
                            )
                        }

                        Text(
                            text = "Address: ${activeMission.address} (GPS ±3.5m)",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Distress Details: ${activeMission.description}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Text(
                            text = "Citizen Contact: ${activeMission.citizenPhone} • Battery: ${activeMission.batteryPercentage}%",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextMuted
                        )

                        // SIMULATED TURN-BY-TURN CARD
                        if (turnByTurnActive) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PrimaryNavy,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TurnRight,
                                        contentDescription = null,
                                        tint = SafeGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "In 250m turn right onto Market St",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                        Text(
                                            text = "ETA: 4 mins • Distance: 1.1 km • Avoid 6th St flood",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 10.sp,
                                                color = Color(0xFFCBD5E1)
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // ACTION BUTTON ROW: ACCEPT | NAVIGATE | CONTACT | ARRIVED
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = { turnByTurnActive = !turnByTurnActive },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (turnByTurnActive) "STOP NAV" else "NAVIGATE", style = MaterialTheme.typography.labelSmall)
                            }

                            Button(
                                onClick = { repository.markVolunteerArrived(activeMission.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = SeverityOrange),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Place, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ARRIVED", style = MaterialTheme.typography.labelSmall)
                            }

                            Button(
                                onClick = { repository.resolveMission(activeMission.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("RESOLVE", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(title = "AVAILABLE DISTRESS QUEUE")
        }

        items(sosList.filter { it.id != activeMission?.id }) { sos ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${sos.id} • ${sos.citizenName}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        SeverityBadge(severity = sos.severity)
                    }
                    Text(text = "Location: ${sos.address}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text(text = sos.description, style = MaterialTheme.typography.bodySmall)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatusBadge(
                            text = sos.status.label,
                            color = sos.status.color,
                            containerColor = sos.status.containerColor
                        )

                        if (sos.status != SosStatus.RESOLVED) {
                            Button(
                                onClick = { repository.acceptAssignment(sos.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("ACCEPT MISSION", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }
        }
    }
}

// 3. VOLUNTEER FIELD REPORT SCREEN
@Composable
fun VolunteerFieldReportScreen(
    repository: DisasterRepository,
    onReportSubmitted: () -> Unit
) {
    var disasterType by remember { mutableStateOf(DisasterType.ROAD_BLOCKED) }
    var severity by remember { mutableStateOf(Severity.HIGH) }
    var locationText by remember { mutableStateOf("Market & 5th St (GPS Auto-tagged)") }
    var descriptionText by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "VOLUNTEER FIELD TRIAGE REPORT",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = PrimaryNavy
            )
            Text(
                text = "Official field damage report automatically tagged with verified first responder credentials.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

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
                        text = "1. DISASTER & HAZARD CATEGORY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = PrimaryNavy
                    )

                    DisasterType.values().toList().chunked(2).forEach { rowTypes: List<DisasterType> ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowTypes.forEach { type: DisasterType ->
                                val isSelected = disasterType == type
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) SafeGreen else SurfaceSecondary,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) SafeGreen else BorderLight
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { disasterType = type }
                                ) {
                                    Text(
                                        text = type.displayName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp
                                        ),
                                        color = if (isSelected) Color.White else TextPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "2. TRIAGE SEVERITY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = PrimaryNavy
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Severity.values().forEach { sev ->
                            val isSelected = severity == sev
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) sev.color else sev.containerColor,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { severity = sev }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                ) {
                                    Text(
                                        text = sev.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        color = if (isSelected) Color.White else sev.onContainerColor
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = locationText,
                        onValueChange = { locationText = it },
                        label = { Text("Field Location") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        label = { Text("Field observations, casualties, accessibility for ambulances...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Button(
                        onClick = {
                            repository.submitDamageReport(
                                type = disasterType,
                                severity = severity,
                                address = locationText,
                                description = descriptionText.ifEmpty { "Field report submitted by Volunteer Marcus Vance." },
                                reporterRole = "VOLUNTEER"
                            )
                            showSuccess = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("TRANSMIT TO COMMAND (EOC)")
                    }
                }
            }
        }
    }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = {
                showSuccess = false
                onReportSubmitted()
            },
            title = { Text("Field Report Transmitted") },
            text = { Text("Report logged directly with Disaster Operations Command. Verification badge applied.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccess = false
                        onReportSubmitted()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                ) {
                    Text("OK")
                }
            }
        )
    }
}

// 4. VOLUNTEER PROFILE SCREEN
@Composable
fun VolunteerProfileScreen(
    repository: DisasterRepository,
    volunteerBattery: Int,
    isAvailable: Boolean,
    activeMission: SosIncident?
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ID BADGE CARD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, SafeGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(SafeGreenContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HealthAndSafety,
                                    contentDescription = null,
                                    tint = SafeGreen,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Marcus Vance",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = PrimaryNavy
                                )
                                Text(
                                    text = "ID: VOL-9428 • Certified EMT",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = SafeGreen
                                )
                            }
                        }

                        StatusBadge(
                            text = "VERIFIED",
                            color = SafeGreen,
                            containerColor = SafeGreenContainer
                        )
                    }
                }
            }
        }

        // LOW BATTERY WARNING TESTER & STATUS
        item {
            if (volunteerBattery <= 20) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmergencyRedContainer),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmergencyRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.BatteryAlert, contentDescription = null, tint = EmergencyRed)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LOW BATTERY ALERT ($volunteerBattery%)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = EmergencyRed
                                )
                            )
                        }
                        Text(
                            text = "Save battery and seek nearby assistance. Switch to Blackbox tracking if under 10%.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = EmergencyRedOnContainer
                        )
                    }
                }
            }
        }

        // SIMULATE BATTERY DRAIN BUTTON FOR DEMO
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "FIELD TELEMETRY SIMULATOR",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = PrimaryNavy
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { repository.setVolunteerBattery(68) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Normal (68%)", style = MaterialTheme.typography.labelSmall)
                        }
                        OutlinedButton(
                            onClick = { repository.setVolunteerBattery(14) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Low (14%)", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(title = "CREDENTIALS & DEPLOYED GEAR")
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
                    ProfileInfoRow(label = "Volunteer Classification", value = "Permanent Certified Responder")
                    ProfileInfoRow(label = "Primary Skills", value = "EMT, USAR, 4x4, Water Rescue")
                    ProfileInfoRow(label = "Responses Completed", value = "34 Missions")
                    ProfileInfoRow(label = "Community Rating", value = "★ 4.95 / 5.0")
                    ProfileInfoRow(label = "Field Equipment", value = "Level 3 Trauma Bag, VHF Radio, 4x4 Winch")
                }
            }
        }
    }
}
