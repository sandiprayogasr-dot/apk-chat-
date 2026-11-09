package com.example.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MarketItem
import com.example.ui.theme.*

@Composable
fun MarketplaceScreen(
    items: List<MarketItem>,
    onChatWithSeller: (MarketItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    var detailItem by remember { mutableStateOf<MarketItem?>(null) }

    val categories = listOf("Semua", "Desain", "Gadgets", "Domain", "Audio")

    val filteredItems = remember(items, searchQuery, selectedCategory) {
        items.filter { item ->
            val matchCategory = selectedCategory == "Semua" || item.category.equals(selectedCategory, ignoreCase = true)
            val matchSearch = searchQuery.isBlank() || item.title.contains(searchQuery, ignoreCase = true) || item.description.contains(searchQuery, ignoreCase = true)
            matchCategory && matchSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VerbaDarkBackground)
            .testTag("marketplace_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerbaSidebar)
                .border(1.dp, VerbaBorder, RoundedCornerShape(0.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Verba Marketplace",
                    color = VerbaTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Jual beli produk & aset digital antar anggota",
                    color = VerbaTextMuted,
                    fontSize = 12.sp
                )
            }
            Icon(
                imageVector = Icons.Default.Storefront,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari produk, aset digital, atau jasa...", color = VerbaTextMuted, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = VerbaTextSecondary, modifier = Modifier.size(18.dp)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = VerbaInputBg,
                unfocusedContainerColor = VerbaInputBg,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = VerbaBorder,
                focusedTextColor = VerbaTextPrimary,
                unfocusedTextColor = VerbaTextPrimary
            ),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .height(48.dp)
        )

        // Categories Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val selected = selectedCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) MaterialTheme.colorScheme.primary else VerbaCard)
                        .clickable { selectedCategory = cat }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (selected) Color.Black else VerbaTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Product Cards List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredItems, key = { it.id }) { item ->
                MarketItemCard(
                    item = item,
                    onClick = { detailItem = item },
                    onChatClick = { onChatWithSeller(item) }
                )
            }
        }
    }

    // Detail Dialog
    detailItem?.let { item ->
        AlertDialog(
            onDismissRequest = { detailItem = null },
            containerColor = VerbaPanel,
            title = {
                Text(text = item.title, color = VerbaTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = item.price, color = MaterialTheme.colorScheme.primary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = item.description, color = VerbaTextSecondary, fontSize = 13.sp)
                    HorizontalDivider(color = VerbaBorder, modifier = Modifier.padding(vertical = 4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(VerbaCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = item.sellerAvatar, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = "Penjual: ${item.sellerName}", color = VerbaTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text(text = "Rating ⭐ ${item.rating}", color = MaterialTheme.colorScheme.primary, fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val current = detailItem
                        detailItem = null
                        if (current != null) onChatWithSeller(current)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Chat, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chat dengan Penjual", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { detailItem = null }) {
                    Text("Tutup", color = VerbaTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun MarketItemCard(
    item: MarketItem,
    onClick: () -> Unit,
    onChatClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VerbaCard)
            .border(1.dp, VerbaBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            // Category Icon box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(VerbaPanel)
                    .border(1.dp, VerbaBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (item.category) {
                        "Desain" -> Icons.Default.DesignServices
                        "Gadgets" -> Icons.Default.Devices
                        "Domain" -> Icons.Default.Public
                        else -> Icons.Default.Headphones
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = item.title,
                    color = VerbaTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    text = item.price,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Oleh ${item.sellerName} • ⭐ ${item.rating}",
                    color = VerbaTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Button(
            onClick = onChatClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(Icons.Default.Chat, null, tint = Color.Black, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Chat", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
