package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ==================================================
// Element Messenger Design System Color Tokens
// ==================================================

// Primary Emerald Branding (#00C896 / #00E676 / #10B981)
val ElementEmerald = Color(0xFF00C896)       // Vibrant Element Emerald Green
val ElementEmeraldLight = Color(0xFF26E0B0)
val ElementEmeraldDark = Color(0xFF00A37A)
val ElementEmeraldGlow = Color(0x3300C896)

// Aliases for compatibility
val VerbaPrimary = ElementEmerald
val VerbaPrimaryLight = ElementEmeraldLight
val VerbaPrimaryDark = ElementEmeraldDark
val WhatsAppEmerald = ElementEmerald
val WhatsAppBlueCheck = Color(0xFF00E676)

// Background & Frosted Glass Surfaces (Dark Mode)
val VerbaDarkBackground = Color(0xFF0D1311) // Deep Slate Obsidian with subtle emerald undertone
val VerbaCard = Color(0xD917221E)           // Frosted translucent surface
val VerbaPanel = Color(0xEB141C18)          // Bottom sheets & dialogs
val VerbaSidebar = Color(0xF2101714)        // Top / Bottom App Bar
val VerbaInputBg = Color(0xCC1A2722)        // Floating input pill background
val VerbaGlassSurface = Color(0xEB141C18)   // Frosted top bar
val VerbaGlassCard = Color(0xD918231F)

// Text Tokens
val VerbaTextPrimary = Color(0xFFFFFFFF)    // Main crisp white text
val VerbaTextSecondary = Color(0xFFA5B4AC)  // Subtitle / Muted emerald-tinted text
val VerbaTextMuted = Color(0xFF6E8077)      // Deeper muted hint text

// Borders & Frosted Glass Dividers
val VerbaBorder = Color(0x1FFFFFFF)         // Translucent panel border
val VerbaBorderFocused = Color(0x6600C896)  // Emerald focus glow
val VerbaGlassBorder = Color(0x2E00C896)    // Frosted glass border
val VerbaGlassBorderGlow = Color(0x5900C896)
val VerbaIncomingBorder = Color(0x1AFFFFFF)
val VerbaOutgoingBorder = Color(0x4D00C896)

// Status & Accents
val VerbaDanger = Color(0xFFFF5252)         // Danger / Red End Call
val VerbaOnline = Color(0xFF00E676)         // Online Status Dot
val VerbaWarning = Color(0xFFFFB300)        // Warning
val VerbaSuccess = Color(0xFF00E676)        // Success

// Message Bubbles
val VerbaIncomingBubble = Color(0xD91B2621) // Dark Frosted Obsidian
val VerbaOutgoingBubble = Color(0xFF00A876) // Rich Emerald Glass
val VerbaOutgoingText = Color(0xFFFFFFFF)
val VerbaIncomingText = Color(0xFFFFFFFF)

// Read Receipt Double Checkmark
val VerbaBlueCheck = Color(0xFF00E676)      // Emerald double check

// Gradients
val PrimaryButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF00C896),
        Color(0xFF00E676)
    )
)
val LiquidGlassEmeraldGradient = PrimaryButtonGradient

val DarkMeshBackground = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF0D1311),
        Color(0xFF090D0C)
    )
)
