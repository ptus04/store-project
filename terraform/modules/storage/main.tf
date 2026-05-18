resource "azurerm_storage_account" "store" {
  name                     = "ptus04storageaccount"
  resource_group_name      = var.resource_group_name
  location                 = var.resource_group_location
  account_replication_type = "LRS"
  account_tier             = "Standard"
}

resource "azurerm_storage_container" "store_image" {
  name                  = "images"
  storage_account_id    = azurerm_storage_account.store.id
  container_access_type = "blob"
}
