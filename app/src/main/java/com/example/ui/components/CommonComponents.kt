package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Severity
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun EmergencyTopBar(
    title: String,
    currentRole: UserRole,
    isOnline: Boolean,
    onRoleSwitchClick: () -> Unit,
    onToggleOnlineClick: () -> Unit,
    batteryPercentage: Int? = null,
    onRoleSelected: ((UserRole) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        shadowElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Upper Header: Title, Status Indicator, GPS & Connectivity Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "DISASTER RESPONSE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            fontSize = 10.sp
                        ),
                        color = TextMuted
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        // Pulsing status dot
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val pulseAlpha by infiniteTransition.animateFloat(
                            initialValue = 0.4f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(900, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "dotAlpha"
                        )

                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isOnline) SafeGreen.copy(alpha = pulseAlpha) else MutedSlate)
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = if (isOnline) "SYSTEM ONLINE" else "OFFLINE MODE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = if (isOnline) SafeGreen else TextSecondary
                        )
                    }
                }

                // Right Side: GPS Pill and Telemetry Chip
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // GPS Active Pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = InfoBlueContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, InfoBlue.copy(alpha = 0.2f)),
                        modifier = Modifier.clickable { onToggleOnlineClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.GpsFixed,
                                contentDescription = "GPS",
                                tint = InfoBlueOnContainer,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "GPS Active",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp
                                ),
                                color = InfoBlueOnContainer
                            )
                        }
                    }

                    // Battery / Telemetry Pill
                    val batt = batteryPercentage ?: 84
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceSecondary,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderMedium)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (batt <= 15) Icons.Default.BatteryAlert else Icons.Default.BatteryFull,
                                contentDescription = "Battery",
                                tint = if (batt <= 15) EmergencyRed else if (batt <= 30) WarningAmber else SafeGreen,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$batt%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                ),
                                color = if (batt <= 15) EmergencyRed else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Clean Minimalist Segmented Role Switcher: [ CITIZEN | VOLUNTEER | AUTHORITY ]
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SurfaceSegmented,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    UserRole.values().forEach { role ->
                        val isSelected = currentRole == role
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) SurfaceCard else Color.Transparent,
                            shadowElevation = if (isSelected) 1.dp else 0.dp,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    if (!isSelected) {
                                        onRoleSelected?.invoke(role) ?: onRoleSwitchClick()
                                    }
                                }
                                .testTag("top_role_tab_${role.name}")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            ) {
                                Text(
                                    text = role.name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = if (isSelected) InfoBlue else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SeverityBadge(
    severity: Severity,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = severity.containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, severity.color.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Text(
            text = severity.label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.3.sp
            ),
            color = severity.onContainerColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun StatusBadge(
    text: String,
    color: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.3.sp
            ),
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun OfflineNoticeBanner(
    queuedCount: Int,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = WarningAmberContainer,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber.copy(alpha = 0.3f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(WarningAmber.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = "Offline",
                        tint = WarningAmberOnContainer,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "OFFLINE MODE ACTIVE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.3.sp
                        ),
                        color = WarningAmberOnContainer
                    )
                    Text(
                        text = if (queuedCount > 0) "$queuedCount report(s) cached locally in storage" else "Map cached. SOS & reports will queue automatically.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = WarningAmberOnContainer
                    )
                }
            }

            if (queuedCount > 0) {
                TextButton(
                    onClick = onSyncClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = WarningAmberOnContainer),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "SYNC NOW",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

// Clean Minimalism Quick Stat Card with soft pill containers and large numbers
@Composable
fun QuickStatCard(
    title: String,
    value: String,
    color: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        shadowElevation = 1.dp,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
                ),
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                ),
                color = color
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp,
                    fontSize = 11.sp
                ),
                color = TextMuted
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary
                )
            }
        }

        if (actionLabel != null && onActionClick != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = InfoBlue,
                modifier = Modifier
                    .clickable { onActionClick() }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextMuted)
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary
        )
    }
}
