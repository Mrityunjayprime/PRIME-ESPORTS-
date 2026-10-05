package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.PrimeEsportsViewModel
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkEsportsBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.FlameCrimson
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AuthModalDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    viewModel: PrimeEsportsViewModel
) {
    if (!isOpen) return

    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var authError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isSignUp) "JOIN PRIME ESPORTS" else "WELCOME BACK",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (isSignUp) "Create your pro gaming profile" else "Log in to compete & stream",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Divider(color = DarkSurfaceBorder, thickness = 0.8.dp)

                if (isSignUp) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Gamertag / Username") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CyberCyan) },
                        modifier = Modifier.fillMaxWidth().testTag("auth_username_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = authTextFieldColors()
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email address") },
                    leadingIcon = { Icon(Icons.Default.Mail, contentDescription = null, tint = CyberCyan) },
                    modifier = Modifier.fillMaxWidth().testTag("auth_email_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = authTextFieldColors()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CyberCyan) },
                    modifier = Modifier.fillMaxWidth().testTag("auth_password_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = authTextFieldColors()
                )

                if (authError != null) {
                    Text(text = authError!!, color = FlameCrimson, fontSize = 11.5.sp)
                }

                // Submit Button
                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            authError = "Please enter all required fields"
                            return@Button
                        }
                        authError = null
                        val userNick = if (isSignUp) username else email.substringBefore("@")
                        viewModel.login(email.trim(), userNick.trim())
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("auth_submit_button")
                ) {
                    Text(
                        text = if (isSignUp) "Create Account" else "Sign In",
                        color = DarkEsportsBg,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    )
                }

                // Social Logins (Facebook & X/Twitter as specified in Section 5)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("or continue with social gaming ID", color = TextMuted, fontSize = 11.5.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.login("facebook.player@primeesports.gg", "FacebookGamer")
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Text("Facebook", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.login("x.player@primeesports.gg", "TwitterPro")
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DA1F2)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp)
                        ) {
                            Text("X / Twitter", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Toggle Sign In vs Sign Up
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isSignUp) "Already have an account? " else "Don't have an account? ",
                        color = TextMuted,
                        fontSize = 12.5.sp
                    )
                    Text(
                        text = if (isSignUp) "Sign In" else "Sign Up",
                        color = CyberCyan,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { isSignUp = !isSignUp }
                    )
                }
            }
        }
    }
}

@Composable
fun ReportContentModal(
    reportTarget: Pair<String, String>?,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    if (reportTarget == null) return

    val (type, id) = reportTarget
    var selectedReason by remember { mutableStateOf("Hate speech or harassment") }
    val reasons = listOf(
        "Hate speech or harassment",
        "Cheating / Exploiting / Stream sniping",
        "Spam or misleading content",
        "Copyright violation",
        "Inappropriate media"
    )

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, FlameCrimson.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = FlameCrimson)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "REPORT $type",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Help maintain a competitive and fair esports community. Select reason:",
                    color = TextSecondary,
                    fontSize = 12.5.sp
                )

                reasons.forEach { reason ->
                    val isSelected = selectedReason == reason
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) FlameCrimson.copy(alpha = 0.15f) else DarkSurface)
                            .border(0.8.dp, if (isSelected) FlameCrimson else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedReason = reason }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = reason,
                            color = if (isSelected) FlameCrimson else TextPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurface)
                    ) {
                        Text("Cancel", color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSubmit(selectedReason) },
                        colors = ButtonDefaults.buttonColors(containerColor = FlameCrimson)
                    ) {
                        Text("Submit Report", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun authTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = DarkSurface,
    unfocusedContainerColor = DarkSurface,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedIndicatorColor = CyberCyan,
    unfocusedIndicatorColor = DarkSurfaceBorder
)
