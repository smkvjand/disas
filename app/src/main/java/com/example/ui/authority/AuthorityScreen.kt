package com.example.ui.authority

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

enum class AuthorityTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    TACTICAL_MAP("TACTICAL MAP", Icons.Default.Map),
    SOS_COMMAND("SOS OPS", Icons.Default.Warning),
    VOLUNTEERS("UNITS", Icons.Default.Groups),
    DAMAGE_REVIEW("REPORTS", Icons.Default.AssignmentLate),
    HAZARDS("HAZARDS", Icons.Default.Dangerous),
    INFRASTRUCTURE("FACILITIES", Icons.Default.Apartment),
    BROADCAST("EAS BROADCAST", Icons.Default.Campaign),
    BLACKBOX("BLACKBOX", Icons.Default.SensorsOff),
    ANALYTICS("ANALYTICS", Icons.Default.Analytics),
    RESOURCES("LOGISTICS", Icons.Default.Inventory2)
}

@Composable
fun AuthorityMainScreen(
    repository: DisasterRepository,
    onSwitchRoleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(AuthorityTab.TACTICAL_MAP) }

    val isOnline by repository.isOnline.collectAsState()
    val sosList by repository.sosList.collectAsState()
    val damageReports by repository.damageReports.collectAsState()
    val volunteers by repository.volunteers.collectAsState()
    val hazardZones by repository.hazardZones.collectAsState()
    val infrastructures by repository.infrastructures.collectAsState()
    val resources by repository.resources.collectAsState()
    val broadcasts by repository.broadcasts.collectAsState()
    val familyMembers by repository.familyMembers.collectAsState()

    Scaffold(
        topBar = {
            Column {
                EmergencyTopBar(
                    title = currentTab.label,
                    currentRole = UserRole.AUTHORITY,
                    isOnline = isOnline,
                    onRoleSwitchClick = onSwitchRoleClick,
                    onToggleOnlineClick = { repository.toggleOnline() },
                    onRoleSelected = { repository.setRole(it) }
                )

                // EOC STATUS BAR WITH LIVE CRISIS TICKER
                Surface(
                    color = SurfaceCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(EmergencyRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "INCIDENT COMMAND SYSTEM (ICS-EOC 24/7)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp,
                                    color = PrimaryNavy
                                )
                            )
                        }

                        Text(
                            text = "SEVERITY: DEFCON 2 • METRO CRISIS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = SeverityOrange
                            )
                        )
                    }
                }

                // SCROLLABLE AUTHORITY MODULE NAVIGATION TABS
                ScrollableTabRow(
                    selectedTabIndex = AuthorityTab.values().indexOf(currentTab),
                    containerColor = SurfaceCard,
                    edgePadding = 12.dp,
                    divider = {},
                    indicator = {},
                    modifier = Modifier.height(42.dp)
                ) {
                    AuthorityTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) InfoBlueContainer else Color.Transparent,
                            modifier = Modifier
                                .padding(horizontal = 3.dp, vertical = 4.dp)
                                .clickable { currentTab = tab }
                                .testTag("authority_tab_${tab.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) InfoBlue else TextMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 10.sp,
                                        letterSpacing = 0.3.sp
                                    ),
                                    color = if (isSelected) InfoBlue else TextSecondary
                                )
                            }
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
                AuthorityTab.TACTICAL_MAP -> AuthorityTacticalMapScreen(
                    repository = repository,
                    sosList = sosList,
                    damageReports = damageReports,
                    volunteers = volunteers,
                    hazardZones = hazardZones,
                    infrastructures = infrastructures,
                    onNavigateToSos = { currentTab = AuthorityTab.SOS_COMMAND },
                    onNavigateToHazards = { currentTab = AuthorityTab.HAZARDS }
                )
                AuthorityTab.SOS_COMMAND -> AuthoritySosManagementScreen(
                    repository = repository,
                    sosList = sosList,
                    volunteers = volunteers
                )
                AuthorityTab.VOLUNTEERS -> AuthorityVolunteerManagementScreen(
                    repository = repository,
                    volunteers = volunteers,
                    sosList = sosList
                )
                AuthorityTab.DAMAGE_REVIEW -> AuthorityDamageReviewScreen(
                    repository = repository,
                    damageReports = damageReports
                )
                AuthorityTab.HAZARDS -> AuthorityHazardManagementScreen(
                    repository = repository,
                    hazardZones = hazardZones
                )
                AuthorityTab.INFRASTRUCTURE -> AuthorityInfrastructureScreen(
                    infrastructures = infrastructures
                )
                AuthorityTab.BROADCAST -> AuthorityEmergencyBroadcastScreen(
                    repository = repository,
                    broadcasts = broadcasts
                )
                AuthorityTab.BLACKBOX -> AuthorityBlackboxScreen(
                    sosList = sosList,
                    repository = repository
                )
                AuthorityTab.ANALYTICS -> AuthorityAnalyticsScreen(
                    sosList = sosList,
                    damageReports = damageReports,
                    volunteers = volunteers,
                    hazardZones = hazardZones
                )
                AuthorityTab.RESOURCES -> AuthorityResourcesScreen(
                    resources = resources
                )
            }
        }
    }
}

// 1. AUTHORITY TACTICAL MAP (~70% MAP + ~30% CRITICAL PANEL)
@Composable
fun AuthorityTacticalMapScreen(
    repository: DisasterRepository,
    sosList: List<SosIncident>,
    damageReports: List<DamageReport>,
    volunteers: List<Volunteer>,
    hazardZones: List<HazardZone>,
    infrastructures: List<InfrastructureItem>,
    onNavigateToSos: () -> Unit,
    onNavigateToHazards: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // ~70% Dominant GIS Tactical Map with Clean Minimalist HUD Overlays
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.68f)
        ) {
            InteractiveMapView(
                sosList = sosList,
                damageReports = damageReports,
                volunteers = volunteers,
                hazardZones = hazardZones,
                infrastructures = infrastructures,
                userRole = UserRole.AUTHORITY
            )

            // Top-Left Clean Minimalist HUD Badges (as in Design HTML)
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 48.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Card 1: Critical SOS
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceCard.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .widthIn(min = 120.dp)
                        .clickable { onNavigateToSos() }
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text(
                            text = "CRITICAL SOS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = TextMuted
                        )
                        val criticalCount = sosList.count { it.severity == Severity.CRITICAL && it.status != SosStatus.RESOLVED }
                        Text(
                            text = if (criticalCount < 10) "0$criticalCount" else "$criticalCount",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            ),
                            color = EmergencyRed
                        )
                        Text(
                            text = "+2 in last 10 mins",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                            color = TextSecondary
                        )
                    }
                }

                // Card 2: Hazards
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceCard.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .widthIn(min = 120.dp)
                        .clickable { onNavigateToHazards() }
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text(
                            text = "HAZARDS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = TextMuted
                        )
                        val hazardCount = hazardZones.size
                        Text(
                            text = if (hazardCount < 10) "0$hazardCount" else "$hazardCount",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            ),
                            color = SeverityOrange
                        )
                        Text(
                            text = "4 high severity",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // ~30% Critical Command Intelligence Panel with Clean Minimalist Styling
        Surface(
            color = SurfaceCard,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.32f)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    // Resources Deployment Header with Live Feed Chip (from design)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "RESOURCES DEPLOYMENT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 0.6.sp
                            ),
                            color = PrimaryNavy
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = InfoBlueContainer
                        ) {
                            Text(
                                text = "LIVE FEED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = InfoBlueOnContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // 2 Large Resource Cards (Teams & Hospitals as in HTML) + Intelligence Grid
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Teams Active Card
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SurfaceSecondary,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(SafeGreenContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = SafeGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "TEAMS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            letterSpacing = 0.4.sp
                                        ),
                                        color = TextMuted
                                    )
                                    Text(
                                        text = "42 Active",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp
                                        ),
                                        color = PrimaryNavy
                                    )
                                }
                            }
                        }

                        // Hospitals Capacity Card
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SurfaceSecondary,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(InfoBlueContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalHospital,
                                        contentDescription = null,
                                        tint = InfoBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "HOSPITALS",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            letterSpacing = 0.4.sp
                                        ),
                                        color = TextMuted
                                    )
                                    Text(
                                        text = "84% Cap.",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp
                                        ),
                                        color = PrimaryNavy
                                    )
                                }
                            }
                        }
                    }
                }

                // 4 KEY INTELLIGENCE METRIC BOXES
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        IntelligenceTile(
                            label = "CRITICAL SOS",
                            value = "${sosList.count { it.severity == Severity.CRITICAL && it.status != SosStatus.RESOLVED }}",
                            subtext = "${sosList.count { it.status == SosStatus.NEW }} New Unassigned",
                            color = EmergencyRed,
                            container = EmergencyRedContainer,
                            modifier = Modifier.weight(1f).clickable { onNavigateToSos() }
                        )

                        IntelligenceTile(
                            label = "HAZARDS",
                            value = "${hazardZones.size}",
                            subtext = "2 Flood, 1 Collapse",
                            color = SeverityOrange,
                            container = SeverityOrangeContainer,
                            modifier = Modifier.weight(1f).clickable { onNavigateToHazards() }
                        )

                        IntelligenceTile(
                            label = "RESPONDERS",
                            value = "${volunteers.count { it.isAvailable }} / ${volunteers.size}",
                            subtext = "38 Available in field",
                            color = SafeGreen,
                            container = SafeGreenContainer,
                            modifier = Modifier.weight(1f)
                        )

                        IntelligenceTile(
                            label = "FREE BEDS",
                            value = "65",
                            subtext = "3 Hospitals open",
                            color = InfoBlue,
                            container = InfoBlueContainer,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // SYSTEM HEALTH TELEMETRY
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SurfaceSecondary,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "EOC System: Central API Online • Mesh Network: 100% • EAS Siren: Armed",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IntelligenceTile(
    label: String,
    value: String,
    subtext: String,
    color: Color,
    container: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    letterSpacing = 0.3.sp
                ),
                color = TextMuted
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                ),
                color = color
            )
            Text(
                text = subtext,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 8.sp),
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}

// 2. AUTHORITY SOS MANAGEMENT SCREEN
@Composable
fun AuthoritySosManagementScreen(
    repository: DisasterRepository,
    sosList: List<SosIncident>,
    volunteers: List<Volunteer>
) {
    var selectedFilter by remember { mutableStateOf<SosStatus?>(null) }
    var selectedSosForDispatch by remember { mutableStateOf<SosIncident?>(null) }

    val filteredList = if (selectedFilter == null) sosList else sosList.filter { it.status == selectedFilter }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "ACTIVE SOS INCIDENT LOG",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = PrimaryNavy
            )
            Text(
                text = "Track high-priority distress signals, dispatch field volunteers and escalate search & rescue missions.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        // Filter status chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (selectedFilter == null) PrimaryNavy else SurfaceSecondary,
                    modifier = Modifier.clickable { selectedFilter = null }
                ) {
                    Text(
                        text = "ALL (${sosList.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = if (selectedFilter == null) Color.White else TextPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                    )
                }

                SosStatus.values().forEach { st ->
                    val isSelected = selectedFilter == st
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isSelected) PrimaryNavy else SurfaceSecondary,
                        modifier = Modifier.clickable { selectedFilter = st }
                    ) {
                        Text(
                            text = "${st.label} (${sosList.count { it.status == st }})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = if (isSelected) Color.White else TextPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        items(filteredList) { sos ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (sos.severity == Severity.CRITICAL && sos.status == SosStatus.NEW) EmergencyRed else BorderLight
                ),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = sos.id,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = PrimaryNavy
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            SeverityBadge(severity = sos.severity)
                        }

                        StatusBadge(
                            text = sos.status.label,
                            color = sos.status.color,
                            containerColor = sos.status.containerColor
                        )
                    }

                    Text(
                        text = "Citizen: ${sos.citizenName} • ${sos.citizenPhone}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "GPS: ${sos.latitude}° N, ${sos.longitude}° W (${sos.address})",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondary
                    )
                    Text(
                        text = "Reported: ${sos.timestamp} • Battery: ${sos.batteryPercentage}% • Accuracy: ±${sos.accuracyMeters}m",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = TextMuted
                    )
                    Text(
                        text = sos.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryNavy
                    )

                    if (sos.assignedVolunteerName != null) {
                        Surface(
                            color = InfoBlueContainer,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Assigned Unit: ${sos.assignedVolunteerName} (${sos.assignedVolunteerId})",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = InfoBlueOnContainer,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }

                    // Authority Actions: ASSIGN VOLUNTEER | ESCALATE | RESOLVE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { selectedSosForDispatch = sos },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("ASSIGN VOLUNTEER", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = { repository.escalateSos(sos.id) },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EmergencyRed),
                            modifier = Modifier.weight(0.8f)
                        ) {
                            Text("ESCALATE", style = MaterialTheme.typography.labelSmall)
                        }

                        if (sos.status != SosStatus.RESOLVED) {
                            OutlinedButton(
                                onClick = { repository.resolveMission(sos.id) },
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
    }

    // Assign Volunteer Picker Modal
    if (selectedSosForDispatch != null) {
        val target = selectedSosForDispatch!!
        AlertDialog(
            onDismissRequest = { selectedSosForDispatch = null },
            title = { Text("Assign Unit to ${target.id}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select closest available volunteer:")
                    volunteers.forEach { vol ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (vol.isAvailable) SurfaceSecondary else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    repository.assignVolunteerToSos(target.id, vol.id)
                                    selectedSosForDispatch = null
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "${vol.name} (${vol.id})", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                    Text(text = "Distance: ${vol.distanceKm} km • Battery: ${vol.batteryPercentage}%", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp))
                                }
                                Text(
                                    text = if (vol.isAvailable) "AVAILABLE" else "BUSY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (vol.isAvailable) SafeGreen else MutedSlate,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedSosForDispatch = null }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

// 3. AUTHORITY VOLUNTEER MANAGEMENT SCREEN
@Composable
fun AuthorityVolunteerManagementScreen(
    repository: DisasterRepository,
    volunteers: List<Volunteer>,
    sosList: List<SosIncident>
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Permanent, 1: Temporary
    val filtered = volunteers.filter { if (selectedTab == 0) it.isPermanent else !it.isPermanent }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "VOLUNTEER & FIRST RESPONDER CORPS",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = PrimaryNavy
            )
            Text(
                text = "Monitor field responder statuses, battery telemetry, and allocate mission sectors.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { selectedTab = 0 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 0) PrimaryNavy else SurfaceSecondary,
                        contentColor = if (selectedTab == 0) Color.White else TextPrimary
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("PERMANENT (${volunteers.count { it.isPermanent }})", style = MaterialTheme.typography.labelSmall)
                }

                Button(
                    onClick = { selectedTab = 1 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 1) PrimaryNavy else SurfaceSecondary,
                        contentColor = if (selectedTab == 1) Color.White else TextPrimary
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("TEMPORARY (${volunteers.count { !it.isPermanent }})", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        items(filtered) { vol ->
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${vol.name} (${vol.id})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (vol.isVerified) {
                                StatusBadge(text = "VERIFIED", color = SafeGreen, containerColor = SafeGreenContainer)
                            }
                        }

                        StatusBadge(
                            text = if (vol.isAvailable) "AVAILABLE" else "ENGAGED",
                            color = if (vol.isAvailable) SafeGreen else InfoBlue,
                            containerColor = if (vol.isAvailable) SafeGreenContainer else InfoBlueContainer
                        )
                    }

                    Text(text = "Location: ${vol.address} (GPS ±4m) • Distance: ${vol.distanceKm} km", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text(text = "Skills: ${vol.skills.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Battery: ${vol.batteryPercentage}% • Rating: ★${vol.rating} • Missions: ${vol.responseHistoryCount}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = TextMuted)

                    if (vol.currentAssignmentId != null) {
                        Text(
                            text = "Assigned Incident: ${vol.currentAssignmentId}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = EmergencyRed
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { /* Simulated tactical dispatch message */ },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("TACTICAL MSG", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = { /* Reassign */ },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("REASSIGN", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

// 4. AUTHORITY DAMAGE REVIEW SCREEN
@Composable
fun AuthorityDamageReviewScreen(
    repository: DisasterRepository,
    damageReports: List<DamageReport>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "INCIDENT DAMAGE & HAZARD TRIAGE",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = PrimaryNavy
            )
            Text(
                text = "Review incoming field damage reports, verify hazards, and escalate to active perimeter zones.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        items(damageReports) { report ->
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = report.id,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            SeverityBadge(severity = report.severity)
                        }

                        StatusBadge(
                            text = report.verificationStatus.label,
                            color = report.verificationStatus.color,
                            containerColor = when (report.verificationStatus) {
                                VerificationStatus.VERIFIED -> SafeGreenContainer
                                VerificationStatus.PENDING_REVIEW -> WarningAmberContainer
                                VerificationStatus.REJECTED -> EmergencyRedContainer
                            }
                        )
                    }

                    Text(
                        text = "Disaster: ${report.disasterType.displayName} • Reported by ${report.reporterName}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Location: ${report.address} (${report.timestamp})",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondary
                    )
                    Text(
                        text = report.description,
                        style = MaterialTheme.typography.bodySmall
                    )

                    if (report.hasVoiceNote) {
                        Surface(
                            color = SurfaceSecondary,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Voice note attached (${report.voiceNoteDurationSec}s)", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp))
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { repository.verifyDamageReport(report.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("VERIFY", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = { repository.rejectDamageReport(report.id) },
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EmergencyRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("REJECT", style = MaterialTheme.typography.labelSmall)
                        }

                        Button(
                            onClick = {
                                repository.createHazardZone(
                                    title = "${report.disasterType.displayName} Zone (${report.address})",
                                    type = report.disasterType,
                                    severity = report.severity,
                                    radiusMeters = 500,
                                    description = report.description,
                                    durationHours = 24
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Text("ESCALATE ZONE", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

// 5. AUTHORITY HAZARD ZONE MANAGEMENT SCREEN
@Composable
fun AuthorityHazardManagementScreen(
    repository: DisasterRepository,
    hazardZones: List<HazardZone>
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "ACTIVE HAZARD PERIMETERS",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = PrimaryNavy
                    )
                    Text(
                        text = "Geofenced danger corridors and evacuation warning radiuses.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("NEW ZONE", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        items(hazardZones) { zone ->
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = zone.id,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            SeverityBadge(severity = zone.severity)
                        }

                        StatusBadge(text = "ACTIVE PERIMETER", color = EmergencyRed, containerColor = EmergencyRedContainer)
                    }

                    Text(text = zone.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    Text(text = "Threat: ${zone.disasterType.displayName} • Radius: ${zone.radiusMeters}m", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text(text = "Estimated Citizens Affected: ~${zone.affectedCitizensCount} people", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = SeverityOrange)
                    Text(text = zone.description, style = MaterialTheme.typography.bodySmall)

                    Button(
                        onClick = {
                            repository.sendEmergencyBroadcast(
                                title = "EVACUATION NOTICE FOR ${zone.id}",
                                type = BroadcastType.EVACUATION,
                                targetAudience = "Citizens inside ${zone.radiusMeters}m of ${zone.title}",
                                priority = "CRITICAL (EAS SIREN)",
                                message = "Zone perimeter active. Evacuate via nearest safe corridor."
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("TRIGGER IMMEDIATE ZONE EVACUATION WARNING")
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        var zoneTitle by remember { mutableStateOf("") }
        var zoneType by remember { mutableStateOf(DisasterType.FLOOD) }
        var zoneSeverity by remember { mutableStateOf(Severity.CRITICAL) }
        var radiusMeters by remember { mutableStateOf("600") }
        var descriptionText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Hazard Zone Perimeter") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = zoneTitle,
                        onValueChange = { zoneTitle = it },
                        label = { Text("Zone Title (e.g. Market St Inundation)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = radiusMeters,
                        onValueChange = { radiusMeters = it },
                        label = { Text("Radius (Meters)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        label = { Text("Operational notes / Evacuation instructions") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (zoneTitle.isNotBlank()) {
                            repository.createHazardZone(
                                title = zoneTitle,
                                type = zoneType,
                                severity = zoneSeverity,
                                radiusMeters = radiusMeters.toIntOrNull() ?: 500,
                                description = descriptionText.ifEmpty { "Official Hazard Zone created." },
                                durationHours = 24
                            )
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                ) {
                    Text("CREATE")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCreateDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

// 6. AUTHORITY INFRASTRUCTURE CAPACITY SCREEN
@Composable
fun AuthorityInfrastructureScreen(
    infrastructures: List<InfrastructureItem>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "INFRASTRUCTURE & FACILITY CAPACITIES",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = PrimaryNavy
            )
            Text(
                text = "Real-time bed availability, emergency hospital capacity, and shelter supplies.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        items(infrastructures) { inf ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = inf.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black))
                            Text(text = "${inf.type.label} • ${inf.address}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }

                        StatusBadge(
                            text = "${inf.capacityPercentage}% CAPACITY",
                            color = if (inf.capacityPercentage > 85) EmergencyRed else if (inf.capacityPercentage > 65) WarningAmber else SafeGreen,
                            containerColor = if (inf.capacityPercentage > 85) EmergencyRedContainer else if (inf.capacityPercentage > 65) WarningAmberContainer else SafeGreenContainer
                        )
                    }

                    // Linear Capacity Progress Indicator
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Occupancy: ${inf.capacityUsed} / ${inf.capacityTotal} spaces", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                            Text(text = "${inf.bedsAvailable} Beds Available", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { inf.capacityPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (inf.capacityPercentage > 85) EmergencyRed else if (inf.capacityPercentage > 65) WarningAmber else SafeGreen,
                            trackColor = SurfaceSecondary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Ambulances: ${inf.ambulancesCount} units", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = TextMuted)
                        Text(text = "Food/Water Supply: ${inf.foodWaterDaysRemaining} days", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = TextMuted)
                        Text(text = "Hotline: ${inf.phone}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = InfoBlue)
                    }
                }
            }
        }
    }
}

// 7. AUTHORITY EMERGENCY BROADCAST SCREEN (EAS)
@Composable
fun AuthorityEmergencyBroadcastScreen(
    repository: DisasterRepository,
    broadcasts: List<EmergencyBroadcast>
) {
    var title by remember { mutableStateOf("MANDATORY EVACUATION NOTICE: SOMA SECTOR") }
    var broadcastType by remember { mutableStateOf(BroadcastType.EVACUATION) }
    var targetAudience by remember { mutableStateOf("All Citizens in Sectors 1 & 4") }
    var priority by remember { mutableStateOf("CRITICAL (EAS SIREN OVERRIDE)") }
    var messageText by remember { mutableStateOf("Flash flood waters rising rapidly. Evacuate immediately via 8th St corridor to Civic Auditorium.") }

    var showConfirmationDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "EMERGENCY BROADCAST SYSTEM (EAS)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = PrimaryNavy
            )
            Text(
                text = "Transmit high-priority sirens, cell broadcast push alerts, and zone evacuation mandates.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        // Broadcast Form
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
                    Text(
                        text = "NEW PUBLIC EMERGENCY TRANSMISSION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = EmergencyRed
                    )

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Broadcast Header Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = targetAudience,
                        onValueChange = { targetAudience = it },
                        label = { Text("Target Audience (All Citizens, Sector 4, Volunteers, Squads...)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        label = { Text("Emergency message instructions") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Button(
                        onClick = { showConfirmationDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("broadcast_emergency_button")
                    ) {
                        Icon(imageVector = Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TRANSMIT EMERGENCY BROADCAST",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }
        }

        item {
            SectionHeader(title = "PAST ISSUED BROADCASTS")
        }

        items(broadcasts) { b ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = b.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black), color = EmergencyRed)
                        StatusBadge(text = b.priority, color = EmergencyRed, containerColor = EmergencyRedContainer)
                    }
                    Text(text = "Target: ${b.targetAudience} • Issued: ${b.issuedAt}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = TextSecondary)
                    Text(text = b.message, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }

    // MANDATORY 2-STEP CONFIRMATION DIALOG BEFORE EAS BROADCAST
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = EmergencyRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CONFIRM EAS BROADCAST TRANSMISSION",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = EmergencyRed
                        )
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("This transmission will trigger sirens on all citizen and responder mobile devices within the targeted area.")
                    Text("Title: $title", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Target: $targetAudience", style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.sendEmergencyBroadcast(
                            title = title,
                            type = broadcastType,
                            targetAudience = targetAudience,
                            priority = priority,
                            message = messageText
                        )
                        showConfirmationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Text("CONFIRM & TRANSMIT NOW")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showConfirmationDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

// 8. AUTHORITY BLACKBOX MODE (CRITICALLY LOW BATTERY TRIAGE)
@Composable
fun AuthorityBlackboxScreen(
    sosList: List<SosIncident>,
    repository: DisasterRepository
) {
    val blackboxIncidents = sosList.filter { it.isBlackbox || it.batteryPercentage <= 10 }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = PrimaryNavy),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.SensorsOff, contentDescription = null, tint = WarningAmber)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BLACKBOX MODE ACTIVE",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tracks citizens whose mobile batteries dropped below 10% or went abruptly offline. Coordinates show verified LAST KNOWN LOCATION rather than live beacon.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFFCBD5E1))
                    )
                }
            }
        }

        items(blackboxIncidents) { bb ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, WarningAmber),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = bb.id, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black))
                            Spacer(modifier = Modifier.width(6.dp))
                            StatusBadge(text = "LAST KNOWN LOCATION", color = WarningAmber, containerColor = WarningAmberContainer)
                        }

                        Text(
                            text = "BATTERY: ${bb.batteryPercentage}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = EmergencyRed
                            )
                        )
                    }

                    Text(text = "Citizen: ${bb.citizenName} (${bb.citizenPhone})", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    Text(text = "Last Ping: ${bb.address} (37.7833° N, 122.4167° W)", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text(text = "Last Broadcast: ${bb.lastKnownTime ?: bb.timestamp} • Last Cell Tower: SOMA Sector Tower #4", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = TextMuted)
                    Text(text = bb.description, style = MaterialTheme.typography.bodySmall)

                    Button(
                        onClick = { repository.assignVolunteerToSos(bb.id, "VOL-9428") },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.DirectionsRun, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("DISPATCH SEARCH SQUAD TO LAST KNOWN COORDS")
                    }
                }
            }
        }
    }
}

// 9. AUTHORITY ANALYTICS SCREEN
@Composable
fun AuthorityAnalyticsScreen(
    sosList: List<SosIncident>,
    damageReports: List<DamageReport>,
    volunteers: List<Volunteer>,
    hazardZones: List<HazardZone>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "CRISIS ANALYTICS & SITUATIONAL METRICS",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = PrimaryNavy
            )
            Text(
                text = "Incident severity distribution, volunteer triage response curves, and sector damage load.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        // Top Metrics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickStatCard(
                    title = "AVG RESPONSE TIME",
                    value = "8.4 Mins",
                    color = SafeGreen,
                    containerColor = SafeGreenContainer,
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    title = "RESOLVED RATE",
                    value = "82.5%",
                    color = InfoBlue,
                    containerColor = InfoBlueContainer,
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    title = "SECTOR DAMAGE",
                    value = "5 Active",
                    color = SeverityOrange,
                    containerColor = SeverityOrangeContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // INCIDENT SEVERITY DISTRIBUTION BAR CHART
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "INCIDENT SEVERITY BREAKDOWN",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = PrimaryNavy
                    )

                    SeverityRow(label = "CRITICAL (Immediate Life Threat)", count = 4, total = 10, color = EmergencyRed)
                    SeverityRow(label = "HIGH (Severe Structural / Medical)", count = 3, total = 10, color = SeverityOrange)
                    SeverityRow(label = "MEDIUM (Obstruction / Non-fatal)", count = 2, total = 10, color = WarningAmber)
                    SeverityRow(label = "LOW (Advisory / Minor Damage)", count = 1, total = 10, color = InfoBlue)
                }
            }
        }

        // DAMAGE REPORTS BY DISASTER TYPE
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "REPORTS BY DISASTER CATEGORY",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = PrimaryNavy
                    )

                    SeverityRow(label = "Flash Flood & Storm Inundation", count = 5, total = 8, color = InfoBlue)
                    SeverityRow(label = "Building & Structural Collapse", count = 2, total = 8, color = SeverityOrange)
                    SeverityRow(label = "Downed High-Voltage Power Lines", count = 1, total = 8, color = WarningAmber)
                }
            }
        }
    }
}

@Composable
fun SeverityRow(label: String, count: Int, total: Int, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
            Text(text = "$count cases", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold))
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { count.toFloat() / total.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = SurfaceSecondary
        )
    }
}

// 10. AUTHORITY RESOURCES & LOGISTICS
@Composable
fun AuthorityResourcesScreen(
    resources: List<ResourceItem>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "EMERGENCY RESOURCE & LOGISTICS INVENTORY",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = PrimaryNavy
            )
            Text(
                text = "Deploy, track and restock ambulances, rescue teams, water tankers and trauma supplies.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }

        items(resources) { res ->
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
                        Text(text = res.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        StatusBadge(
                            text = res.status.label,
                            color = res.status.color,
                            containerColor = res.status.containerColor
                        )
                    }

                    Text(text = "Category: ${res.category.label} • ${res.location}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text(
                        text = "Available: ${res.availableCount} • Deployed: ${res.deployedCount} • Total Stock: ${res.totalCount}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryNavy
                    )
                }
            }
        }
    }
}
