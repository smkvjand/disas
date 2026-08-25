package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun RoleSelectionScreen(
    onSelectRole: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = BackgroundLight,
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Official Emergency Shield Emblem in clean rounded pill
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(InfoBlueContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Emergency Shield",
                        tint = InfoBlue,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "DISASTER RESPONSE",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp,
                        fontSize = 16.sp
                    ),
                    color = PrimaryNavy
                )

                Text(
                    text = "INTEGRATED CRISIS MANAGEMENT NETWORK",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "SELECT OPERATIONAL ROLE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        fontSize = 11.sp
                    ),
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Role 1: CITIZEN
            item {
                RoleSelectionCard(
                    role = UserRole.CITIZEN,
                    title = "Citizen",
                    badge = "Public Response",
                    subtitle = "One-tap SOS distress beacon, real-time damage reporting, open shelters finder, and squad safety check-ins.",
                    accentColor = EmergencyRed,
                    containerColor = EmergencyRedContainer,
                    icon = Icons.Default.Emergency,
                    tag = "select_citizen_role",
                    onClick = { onSelectRole(UserRole.CITIZEN) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Role 2: VOLUNTEER
            item {
                RoleSelectionCard(
                    role = UserRole.VOLUNTEER,
                    title = "Volunteer",
                    badge = "First Responder",
                    subtitle = "Operational field responder triage, accept active rescue missions, live GPS routing & hazard verification reports.",
                    accentColor = SafeGreen,
                    containerColor = SafeGreenContainer,
                    icon = Icons.Default.HealthAndSafety,
                    tag = "select_volunteer_role",
                    onClick = { onSelectRole(UserRole.VOLUNTEER) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Role 3: AUTHORITY / GOVERNMENT
            item {
                RoleSelectionCard(
                    role = UserRole.AUTHORITY,
                    title = "Authority",
                    badge = "Command & Control",
                    subtitle = "Incident Command System (ICS). Tactical GIS map, multi-layer overlays, volunteer dispatch & EAS emergency broadcasts.",
                    accentColor = InfoBlue,
                    containerColor = InfoBlueContainer,
                    icon = Icons.Default.Security,
                    tag = "select_authority_role",
                    onClick = { onSelectRole(UserRole.AUTHORITY) }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Surface(
                    color = SurfaceCard,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
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
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = InfoBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "You can quickly toggle between Citizen, Volunteer, and Command roles anytime via the top bar.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoleSelectionCard(
    role: UserRole,
    title: String,
    badge: String,
    subtitle: String,
    accentColor: Color,
    containerColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(tag)
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
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(containerColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = PrimaryNavy
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = containerColor,
                    border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CONTINUE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp
                    ),
                    color = accentColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}
