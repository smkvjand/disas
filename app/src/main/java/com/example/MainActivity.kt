package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.DisasterRepository
import com.example.model.UserRole
import com.example.ui.RoleSelectionScreen
import com.example.ui.authority.AuthorityMainScreen
import com.example.ui.citizen.CitizenMainScreen
import com.example.ui.theme.DisasterResponseTheme
import com.example.ui.volunteer.VolunteerMainScreen

class MainActivity : ComponentActivity() {

    private val repository = DisasterRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DisasterResponseTheme {
                val currentRole by repository.currentRole.collectAsState()

                Surface(modifier = Modifier.fillMaxSize()) {
                    AnimatedContent(
                        targetState = currentRole,
                        label = "RoleTransition"
                    ) { role ->
                        when (role) {
                            null -> RoleSelectionScreen(
                                onSelectRole = { selected ->
                                    repository.setRole(selected)
                                }
                            )
                            UserRole.CITIZEN -> CitizenMainScreen(
                                repository = repository,
                                onSwitchRoleClick = {
                                    repository.setRole(null)
                                }
                            )
                            UserRole.VOLUNTEER -> VolunteerMainScreen(
                                repository = repository,
                                onSwitchRoleClick = {
                                    repository.setRole(null)
                                }
                            )
                            UserRole.AUTHORITY -> AuthorityMainScreen(
                                repository = repository,
                                onSwitchRoleClick = {
                                    repository.setRole(null)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
