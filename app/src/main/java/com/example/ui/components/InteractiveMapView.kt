package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

enum class MapFilterLayer(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    ALL("All Layers", Icons.Default.Layers),
    SOS("SOS Incidents", Icons.Default.Warning),
    SHELTERS("Shelters", Icons.Default.NightShelter),
    HOSPITALS("Hospitals", Icons.Default.LocalHospital),
    HAZARDS("Hazard Zones", Icons.Default.Dangerous),
    VOLUNTEERS("Volunteers", Icons.Default.PersonPinCircle),
    EVAC_ROUTES("Evac Corridors", Icons.Default.AltRoute)
}

sealed class MapSelectedEntity {
    data class Sos(val incident: SosIncident) : MapSelectedEntity()
    data class Damage(val report: DamageReport) : MapSelectedEntity()
    data class Vol(val volunteer: Volunteer) : MapSelectedEntity()
    data class Infra(val item: InfrastructureItem) : MapSelectedEntity()
    data class Hazard(val zone: HazardZone) : MapSelectedEntity()
}

@Composable
fun InteractiveMapView(
    sosList: List<SosIncident>,
    damageReports: List<DamageReport>,
    volunteers: List<Volunteer>,
    hazardZones: List<HazardZone>,
    infrastructures: List<InfrastructureItem>,
    modifier: Modifier = Modifier,
    userRole: UserRole = UserRole.CITIZEN,
    onSosClick: ((SosIncident) -> Unit)? = null,
    onDamageClick: ((DamageReport) -> Unit)? = null,
    onVolunteerClick: ((Volunteer) -> Unit)? = null,
    onInfraClick: ((InfrastructureItem) -> Unit)? = null,
    onHazardClick: ((HazardZone) -> Unit)? = null
) {
    var selectedFilter by remember { mutableStateOf(MapFilterLayer.ALL) }
    var selectedEntity by remember { mutableStateOf<MapSelectedEntity?>(null) }

    var panOffsetX by remember { mutableStateOf(0f) }
    var panOffsetY by remember { mutableStateOf(0f) }
    var zoomScale by remember { mutableStateOf(1.0f) }

    // Pulsing beacon animation for active emergency SOS
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 32f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(0.dp))
            .background(Color(0xFFF1F5F9))
    ) {
        // Main GIS Canvas Map
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        panOffsetX += dragAmount.x
                        panOffsetY += dragAmount.y
                    }
                }
                .testTag("interactive_map_canvas")
        ) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f + panOffsetX
            val centerY = height / 2f + panOffsetY

            // 1. Draw Land / Terrain Background
            drawRect(color = Color(0xFFF3F4F6))

            // 2. Draw Coastline / River Water body on East / North-East
            val waterPath = Path().apply {
                moveTo(width * 0.82f + panOffsetX * 0.5f, 0f)
                cubicTo(
                    width * 0.78f + panOffsetX * 0.5f, height * 0.35f,
                    width * 0.85f + panOffsetX * 0.5f, height * 0.7f,
                    width * 0.80f + panOffsetX * 0.5f, height
                )
                lineTo(width, height)
                lineTo(width, 0f)
                close()
            }
            drawPath(path = waterPath, color = Color(0xFFBAE6FD))

            // 3. Draw Grid Lines (OpenStreetMap Coordinates)
            val gridStep = 60f * zoomScale
            var gx = (panOffsetX % gridStep)
            while (gx < width) {
                drawLine(
                    color = Color(0xFFE5E7EB),
                    start = Offset(gx, 0f),
                    end = Offset(gx, height),
                    strokeWidth = 1f
                )
                gx += gridStep
            }
            var gy = (panOffsetY % gridStep)
            while (gy < height) {
                drawLine(
                    color = Color(0xFFE5E7EB),
                    start = Offset(0f, gy),
                    end = Offset(width, gy),
                    strokeWidth = 1f
                )
                gy += gridStep
            }

            // 4. Draw Major Road Network / Corridors
            val roadColor = Color(0xFFFFFFFF)
            val roadBorder = Color(0xFFCBD5E1)

            // Diagonal Boulevard (Market St)
            drawLine(
                color = roadBorder,
                start = Offset(0f + panOffsetX, height * 0.85f + panOffsetY),
                end = Offset(width * 0.85f + panOffsetX, height * 0.15f + panOffsetY),
                strokeWidth = 14f * zoomScale
            )
            drawLine(
                color = roadColor,
                start = Offset(0f + panOffsetX, height * 0.85f + panOffsetY),
                end = Offset(width * 0.85f + panOffsetX, height * 0.15f + panOffsetY),
                strokeWidth = 10f * zoomScale
            )

            // Primary Avenue (Van Ness Corridor)
            drawLine(
                color = roadBorder,
                start = Offset(width * 0.35f + panOffsetX, 0f),
                end = Offset(width * 0.35f + panOffsetX, height),
                strokeWidth = 12f * zoomScale
            )
            drawLine(
                color = roadColor,
                start = Offset(width * 0.35f + panOffsetX, 0f),
                end = Offset(width * 0.35f + panOffsetX, height),
                strokeWidth = 8f * zoomScale
            )

            // 3rd / 4th St Crossings
            val streetsY = listOf(0.3f, 0.45f, 0.6f, 0.75f)
            streetsY.forEach { frac ->
                val yPos = height * frac + panOffsetY
                drawLine(
                    color = Color(0xFFE2E8F0),
                    start = Offset(0f, yPos),
                    end = Offset(width * 0.82f + panOffsetX * 0.5f, yPos),
                    strokeWidth = 6f * zoomScale
                )
            }

            val streetsX = listOf(0.18f, 0.52f, 0.68f)
            streetsX.forEach { frac ->
                val xPos = width * frac + panOffsetX
                drawLine(
                    color = Color(0xFFE2E8F0),
                    start = Offset(xPos, 0f),
                    end = Offset(xPos, height),
                    strokeWidth = 6f * zoomScale
                )
            }

            // 5. Draw Evacuation Corridors if enabled
            if (selectedFilter == MapFilterLayer.ALL || selectedFilter == MapFilterLayer.EVAC_ROUTES) {
                val evacPath = Path().apply {
                    moveTo(width * 0.35f + panOffsetX, height * 0.9f + panOffsetY)
                    lineTo(width * 0.35f + panOffsetX, height * 0.5f + panOffsetY)
                    lineTo(width * 0.15f + panOffsetX, height * 0.48f + panOffsetY)
                }
                drawPath(
                    path = evacPath,
                    color = SafeGreen,
                    style = Stroke(
                        width = 4f * zoomScale,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                    )
                )
            }

            // 6. Draw Hazard Zones (Polygons & Radii)
            if (selectedFilter == MapFilterLayer.ALL || selectedFilter == MapFilterLayer.HAZARDS) {
                hazardZones.forEachIndexed { index, zone ->
                    val zoneX = centerX + (index * 70f - 40f) * zoomScale
                    val zoneY = centerY + (if (index % 2 == 0) -80f else 60f) * zoomScale
                    val zoneRad = (zone.radiusMeters / 15f) * zoomScale

                    // Translucent danger circle
                    val zColor = when (zone.severity) {
                        Severity.CRITICAL -> EmergencyRed
                        Severity.HIGH -> SeverityOrange
                        Severity.MEDIUM -> WarningAmber
                        Severity.LOW -> InfoBlue
                    }
                    drawCircle(
                        color = zColor.copy(alpha = 0.18f),
                        radius = zoneRad,
                        center = Offset(zoneX, zoneY)
                    )
                    drawCircle(
                        color = zColor.copy(alpha = 0.7f),
                        radius = zoneRad,
                        center = Offset(zoneX, zoneY),
                        style = Stroke(
                            width = 2.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                        )
                    )
                }
            }

            // 7. Draw User GPS Location (Citizen/Volunteer center)
            val userLocX = centerX
            val userLocY = centerY + 30f * zoomScale
            drawCircle(
                color = InfoBlue.copy(alpha = 0.25f),
                radius = 24f * zoomScale,
                center = Offset(userLocX, userLocY)
            )
            drawCircle(
                color = Color.White,
                radius = 9f * zoomScale,
                center = Offset(userLocX, userLocY)
            )
            drawCircle(
                color = InfoBlue,
                radius = 6f * zoomScale,
                center = Offset(userLocX, userLocY)
            )

            // 8. Draw SOS Incidents with pulsing beacon animation
            if (selectedFilter == MapFilterLayer.ALL || selectedFilter == MapFilterLayer.SOS) {
                sosList.filter { it.status != SosStatus.RESOLVED }.forEachIndexed { idx, sos ->
                    val sx = centerX + ((idx - 1) * 90f + 15f) * zoomScale
                    val sy = centerY + (if (idx % 2 == 0) -60f else 80f) * zoomScale

                    // Pulsing Ring
                    if (sos.severity == Severity.CRITICAL) {
                        drawCircle(
                            color = EmergencyRed.copy(alpha = pulseAlpha),
                            radius = pulseRadius * zoomScale,
                            center = Offset(sx, sy)
                        )
                    }

                    // Solid Base Pin
                    drawCircle(
                        color = Color.White,
                        radius = 12f * zoomScale,
                        center = Offset(sx, sy)
                    )
                    drawCircle(
                        color = if (sos.isBlackbox) SeverityOrange else EmergencyRed,
                        radius = 9f * zoomScale,
                        center = Offset(sx, sy)
                    )
                }
            }

            // 9. Draw Hospitals & Shelters
            if (selectedFilter == MapFilterLayer.ALL || selectedFilter == MapFilterLayer.HOSPITALS || selectedFilter == MapFilterLayer.SHELTERS) {
                infrastructures.forEachIndexed { idx, infra ->
                    val shouldShow = when (infra.type) {
                        InfrastructureType.HOSPITAL -> selectedFilter == MapFilterLayer.ALL || selectedFilter == MapFilterLayer.HOSPITALS
                        InfrastructureType.SHELTER -> selectedFilter == MapFilterLayer.ALL || selectedFilter == MapFilterLayer.SHELTERS
                        else -> selectedFilter == MapFilterLayer.ALL
                    }
                    if (shouldShow) {
                        val ix = centerX + (if (idx % 2 == 0) -130f else 120f) * zoomScale
                        val iy = centerY + (idx * 45f - 90f) * zoomScale
                        val pinColor = when (infra.type) {
                            InfrastructureType.HOSPITAL -> InfoBlue
                            InfrastructureType.SHELTER -> SafeGreen
                            InfrastructureType.EMERGENCY_CENTER -> PrimaryNavy
                            else -> SecondarySlate
                        }
                        drawCircle(
                            color = Color.White,
                            radius = 10f * zoomScale,
                            center = Offset(ix, iy)
                        )
                        drawCircle(
                            color = pinColor,
                            radius = 7f * zoomScale,
                            center = Offset(ix, iy)
                        )
                    }
                }
            }

            // 10. Draw Volunteers on Map
            if (selectedFilter == MapFilterLayer.ALL || selectedFilter == MapFilterLayer.VOLUNTEERS) {
                volunteers.forEachIndexed { idx, vol ->
                    val vx = centerX + (idx * 55f - 110f) * zoomScale
                    val vy = centerY + (if (idx % 3 == 0) 100f else -110f) * zoomScale
                    drawCircle(
                        color = Color.White,
                        radius = 9f * zoomScale,
                        center = Offset(vx, vy)
                    )
                    drawCircle(
                        color = if (vol.isAvailable) SafeGreen else MutedSlate,
                        radius = 6f * zoomScale,
                        center = Offset(vx, vy)
                    )
                }
            }
        }

        // TOP CONTROLS: Layer Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val visibleFilters = when (userRole) {
                UserRole.CITIZEN -> listOf(MapFilterLayer.ALL, MapFilterLayer.SHELTERS, MapFilterLayer.HOSPITALS, MapFilterLayer.HAZARDS)
                UserRole.VOLUNTEER -> listOf(MapFilterLayer.ALL, MapFilterLayer.SOS, MapFilterLayer.HAZARDS, MapFilterLayer.SHELTERS)
                UserRole.AUTHORITY -> listOf(MapFilterLayer.ALL, MapFilterLayer.SOS, MapFilterLayer.VOLUNTEERS, MapFilterLayer.HAZARDS, MapFilterLayer.EVAC_ROUTES)
            }

            visibleFilters.forEach { filter ->
                val isSelected = selectedFilter == filter
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) PrimaryNavy else Color.White.copy(alpha = 0.95f),
                    shadowElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) PrimaryNavy else BorderLight
                    ),
                    modifier = Modifier
                        .clickable { selectedFilter = filter }
                        .testTag("map_filter_${filter.name}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = filter.icon,
                            contentDescription = filter.label,
                            tint = if (isSelected) Color.White else PrimaryNavy,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = filter.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 10.sp
                            ),
                            color = if (isSelected) Color.White else TextPrimary
                        )
                    }
                }
            }
        }

        // MAP CONTROLS (Floating Zoom & Center Buttons on right)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingMapButton(
                icon = Icons.Default.Add,
                onClick = { zoomScale = (zoomScale * 1.25f).coerceAtMost(2.5f) },
                tag = "zoom_in_button"
            )
            FloatingMapButton(
                icon = Icons.Default.Remove,
                onClick = { zoomScale = (zoomScale / 1.25f).coerceAtLeast(0.6f) },
                tag = "zoom_out_button"
            )
            FloatingMapButton(
                icon = Icons.Default.MyLocation,
                onClick = {
                    panOffsetX = 0f
                    panOffsetY = 0f
                    zoomScale = 1.0f
                },
                tag = "center_location_button"
            )
        }

        // MAP LEGEND & QUICK MARKER STRIP (Bottom Overlays)
        Card(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 12.dp)
                .widthIn(max = 240.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
        ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                Text(
                    text = "SECTOR 4 — METRO GIS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = PrimaryNavy
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LegendItem(color = EmergencyRed, label = "SOS (${sosList.count { it.status != SosStatus.RESOLVED }})")
                    LegendItem(color = SafeGreen, label = "Shelters (2)")
                    LegendItem(color = InfoBlue, label = "Hospitals (2)")
                }
            }
        }

        // TAPPABLE PINS OVERLAY CARDS (Interactive Quick Taps)
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = EmergencyRedContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, EmergencyRed.copy(alpha = 0.4f)),
                modifier = Modifier.clickable {
                    val targetSos = sosList.firstOrNull { it.status != SosStatus.RESOLVED }
                    if (targetSos != null) {
                        selectedEntity = MapSelectedEntity.Sos(targetSos)
                        onSosClick?.invoke(targetSos)
                    }
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Inspect SOS",
                        tint = EmergencyRed,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "SOS #2841",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = EmergencyRedOnContainer
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SafeGreenContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, SafeGreen.copy(alpha = 0.4f)),
                modifier = Modifier.clickable {
                    val shelter = infrastructures.firstOrNull { it.type == InfrastructureType.SHELTER }
                    if (shelter != null) {
                        selectedEntity = MapSelectedEntity.Infra(shelter)
                        onInfraClick?.invoke(shelter)
                    }
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NightShelter,
                        contentDescription = "Inspect Shelter",
                        tint = SafeGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Civic Shelter",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = SafeGreenOnContainer
                    )
                }
            }
        }

        // Marker Detail Modal / Bottom Drawer if clicked
        if (selectedEntity != null) {
            MapEntityDetailDialog(
                entity = selectedEntity!!,
                userRole = userRole,
                onDismiss = { selectedEntity = null },
                onNavigate = { selectedEntity = null }
            )
        }
    }
}

@Composable
private fun FloatingMapButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    tag: String
) {
    Surface(
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 3.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        modifier = Modifier
            .size(38.dp)
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryNavy,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
            color = TextSecondary
        )
    }
}

@Composable
fun MapEntityDetailDialog(
    entity: MapSelectedEntity,
    userRole: UserRole,
    onDismiss: () -> Unit,
    onNavigate: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = when (entity) {
                        is MapSelectedEntity.Sos -> "SOS INCIDENT ${entity.incident.id}"
                        is MapSelectedEntity.Damage -> "DAMAGE REPORT ${entity.report.id}"
                        is MapSelectedEntity.Vol -> "VOLUNTEER ${entity.volunteer.id}"
                        is MapSelectedEntity.Infra -> entity.item.name
                        is MapSelectedEntity.Hazard -> entity.zone.title
                    },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (entity) {
                    is MapSelectedEntity.Sos -> {
                        val s = entity.incident
                        SeverityBadge(severity = s.severity)
                        Text(text = "Citizen: ${s.citizenName}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text(text = "Location: ${s.address}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Text(text = "Battery: ${s.batteryPercentage}% • Reported: ${s.timestamp}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        Text(text = s.description, style = MaterialTheme.typography.bodyMedium)
                    }
                    is MapSelectedEntity.Infra -> {
                        val inf = entity.item
                        Text(text = "Type: ${inf.type.label}", style = MaterialTheme.typography.labelSmall, color = InfoBlue)
                        Text(text = "Address: ${inf.address}", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Capacity: ${inf.capacityUsed}/${inf.capacityTotal} (${inf.capacityPercentage}%)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text(text = "Available Beds: ${inf.bedsAvailable} • Ambulances: ${inf.ambulancesCount}", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Emergency Hotline: ${inf.phone}", style = MaterialTheme.typography.bodySmall, color = SafeGreen)
                    }
                    is MapSelectedEntity.Hazard -> {
                        val hz = entity.zone
                        SeverityBadge(severity = hz.severity)
                        Text(text = "Type: ${hz.disasterType.displayName}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text(text = "Perimeter Radius: ${hz.radiusMeters}m (~${hz.affectedCitizensCount} residents)", style = MaterialTheme.typography.bodySmall)
                        Text(text = hz.description, style = MaterialTheme.typography.bodyMedium)
                    }
                    is MapSelectedEntity.Vol -> {
                        val v = entity.volunteer
                        Text(text = v.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text(text = "Skills: ${v.skills.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                        Text(text = "Distance: ${v.distanceKm} km • Battery: ${v.batteryPercentage}%", style = MaterialTheme.typography.bodySmall)
                    }
                    is MapSelectedEntity.Damage -> {
                        val d = entity.report
                        SeverityBadge(severity = d.severity)
                        Text(text = "Disaster: ${d.disasterType.displayName}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text(text = "Address: ${d.address}", style = MaterialTheme.typography.bodySmall)
                        Text(text = d.description, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onNavigate,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
            ) {
                Icon(imageVector = Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("NAVIGATE / FOCUS")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("CLOSE")
            }
        }
    )
}
