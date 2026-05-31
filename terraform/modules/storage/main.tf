resource "azurerm_storage_account" "store" {
  name                     = "ptus04storageaccount"
  resource_group_name      = var.resource_group_name
  location                 = var.resource_group_location
  account_replication_type = "LRS"
  account_tier             = "Standard"
  blob_properties {
    cors_rule {
      allowed_headers    = ["*"]
      allowed_methods    = ["GET", "PUT", "OPTIONS"]
      allowed_origins    = ["https://ptus04-store-admin-app.azurewebsites.net"]
      exposed_headers    = ["*"]
      max_age_in_seconds = 3600
    }
  }
}

resource "azurerm_storage_container" "store_image" {
  name                  = "images"
  storage_account_id    = azurerm_storage_account.store.id
  container_access_type = "blob"
}

resource "azurerm_storage_container" "store_carousel" {
  name                  = "carousel"
  storage_account_id    = azurerm_storage_account.store.id
  container_access_type = "blob"
}
