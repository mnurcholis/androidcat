package com.cat.androidcat.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// OPSI B: EMERALD DARK THEME (Tema Gelap Sejuk, Anti-Silau, Nyaman Membaca)
// =========================================================================

// 1. Aksen Utama: Emerald Green (Hijau Zamrud Sejuk)
val PrimaryEmerald = Color(0xFF10B981)
val PrimaryEmeraldDark = Color(0xFF059669)
val PrimaryEmeraldLight = Color(0xFF133E2F)     // Wadah/Badge Aksen Gelap
val PrimaryEmeraldText = Color(0xFF6EE7B7)      // Teks Aksen Cerah di atas Latar Gelap

// Kompatibilitas Alias (Menggantikan Biru secara Otomatis di Seluruh Komponen)
val PrimaryBlue = PrimaryEmerald
val PrimaryBlueDark = PrimaryEmeraldDark
val PrimaryBlueLight = PrimaryEmeraldLight

// 2. Aksen Kedua: Golden Amber (Khas Jurus Al Faiz & Poin Highlight)
val AccentAmber = Color(0xFFF59E0B)
val AccentAmberDark = Color(0xFFD97706)
val AccentAmberLight = Color(0xFF382606)        // Latar Kotak Jurus Cepat Al Faiz
val AccentAmberText = Color(0xFFFCD34D)         // Teks Kuning Keemasan Menyala

// Kompatibilitas Alias
val AccentIndigo = AccentAmber
val AccentIndigoLight = AccentAmberLight

// 3. Status Colors (Dark Mode Friendly)
val CatGreen = Color(0xFF10B981)
val CatGreenLight = Color(0xFF0D3829)
val CatGreenText = Color(0xFF34D399)

val CatYellow = Color(0xFFF59E0B)
val CatYellowLight = Color(0xFF3D2A08)
val CatYellowText = Color(0xFFFDE047)

val CatRed = Color(0xFFEF4444)
val CatRedLight = Color(0xFF3B1515)
val CatRedText = Color(0xFFFCA5A5)

// 4. Latar & Kartu Gelap (Deep Slate Charcoal - Tidak Melelahkan Retina Mata)
val BackgroundLight = Color(0xFF0F172A)         // Slate 900 (Dasar Layar Gelap Sejuk)
val SurfaceCard = Color(0xFF1E293B)             // Slate 800 (Kartu Soal, Dialog, Bottom Sheet)
val SurfaceCardElevated = Color(0xFF334155)     // Slate 700 (Opsi Belum Dipilih, Chip, Divider)
val SurfaceCardPressed = Color(0xFF243044)

// 5. Tipografi & Kontras Teks (Jelas, Tajam, Anti-Silau)
val TextPrimary = Color(0xFFF8FAFC)             // Putih Tulang Jernih (Membaca Lama Tanpa Lelah)
val TextSecondary = Color(0xFF94A3B8)           // Abu-abu Lembut Muted
val BorderColor = Color(0xFF334155)             // Garis Batas Halus Slate
val BorderFocus = Color(0xFF10B981)             // Garis Batas Saat Dipilih (Emerald)
