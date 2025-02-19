package com.android.shoppinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class ShoppingItem(
    val id: Int,
    var name: String,
    var quantity: Int,
    var isEditing: Boolean = false,
)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ShoppingListUI() {

    var shoppingItem by remember { mutableStateOf(emptyList<ShoppingItem>()) }
    var newShoppingItem by remember { mutableStateOf("") }
    var newQuantity by remember { mutableStateOf("1") }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Shopping List App") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog = true
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerpadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerpadding)
        ) {

            LazyColumn() {
                items(shoppingItem) { item ->
                    if (item.isEditing) {
                        EditableShoppingItem(
                            item = item,
                            onEditComplete = { name, quantity ->
                                shoppingItem = shoppingItem.map {
                                    if (it.id == item.id) {
                                        it.copy(name = name, quantity = quantity, isEditing = false)
                                    } else {
                                        it.copy(isEditing = false)
                                    }
                                }
                            }
                        )
                    } else {
                        ShoppingListItem(
                            item = item,
                            onEdit = {
                                shoppingItem = shoppingItem.map {
                                    it.copy(isEditing = it.id == item.id)
                                }
                            },
                            onDelete = {
                                shoppingItem = shoppingItem.filter { it.id != item.id }
                            }
                        )
                    }
                }
            }

            if (showDialog) {
                AddItemDialog(
                    itemName = newShoppingItem,
                    itemQuantity = newQuantity,
                    onItemNameChange = { newShoppingItem = it },
                    onItemQUantityChange = { newQuantity = it },
                    onDismiss = { showDialog = false },
                    onConfirm = {
                        newQuantity.toIntOrNull() ?: 1
                        shoppingItem = shoppingItem + ShoppingItem(
                            id = shoppingItem.size + 1,
                            name = newShoppingItem,
                            quantity = newQuantity.toInt()
                        )
                        showDialog = false
                        newShoppingItem = ""
                        newQuantity = "1"
                    }
                )
            }
        }
    }
}


@Composable
fun AddItemDialog(
    itemName: String,
    itemQuantity: String,
    onItemNameChange: (String) -> Unit,
    onItemQUantityChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Shopping Item") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = onItemNameChange,
                    label = { Text(text = "Item Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = itemQuantity,
                    onValueChange = onItemQUantityChange,
                    label = { Text(text = "Quantity") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            FloatingActionButton(onClick = onConfirm) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
    )
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = item.name, style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "Quantity: ${item.quantity}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onEdit
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(
                    onClick = onDelete
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Edit")
                }
            }
        }
    }
}

@Composable
fun EditableShoppingItem(
    item: ShoppingItem,
    onEditComplete: (String, Int) -> Unit,
) {
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text(text = "Item Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = editedQuantity,
                onValueChange = { editedQuantity = it },
                label = { Text(text = "Item Quantity") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                modifier = Modifier.align(Alignment.End),
                onClick = {
                    val quantity = editedQuantity.toIntOrNull() ?: 1
                    onEditComplete(editedName, quantity)
                }
            ) {
                Text(text = "Update")
            }
        }
    }
}
