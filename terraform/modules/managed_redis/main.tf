resource "azurerm_managed_redis" "store" {
  name                = "ptus04-store-managed-redis"
  resource_group_name = var.resource_group_name
  location            = var.resource_group_location
  sku_name            = "Balanced_B0"
  default_database {
    access_keys_authentication_enabled = true
    clustering_policy                  = "EnterpriseCluster"
  }
}
