output "connection_string" {
  value     = "rediss://${azurerm_managed_redis.store.default_database[0].primary_access_key}@${azurerm_managed_redis.store.hostname}:10000/0"
  sensitive = true
}
