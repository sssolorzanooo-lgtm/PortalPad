package com.portalpad.app.ui.trackpad

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.portalpad.app.ui.theme.AbPrimaryBright
import com.portalpad.app.ui.theme.AbOnSurfaceMuted
import com.portalpad.app.ui.theme.AbSurface

@Composable
fun DisplaySelectorPill(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val contentColor = if (enabled) AbPrimaryBright else AbOnSurfaceMuted.copy(alpha = 0.55f)
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(AbSurface)
            // Global context selector: visible as a control, but quieter than
            // the active purple Input selector inside the panel.
            .border(
                1.dp,
                if (enabled) {
                    AbPrimaryBright.copy(alpha = 0.22f)
                } else {
                    AbOnSurfaceMuted.copy(alpha = 0.12f)
                },
                RoundedCornerShape(24.dp),
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = label,
            color = contentColor,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.size(6.dp))
        Icon(
            Icons.Default.ArrowDropDown,
            contentDescription = "Open display settings",
            tint = contentColor,
        )
    }
}
