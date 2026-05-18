output "server_name" {
  value = azurerm_mysql_flexible_server.store.name
}

output "administrator_login" {
  value     = azurerm_mysql_flexible_server.store.administrator_login
  sensitive = true
}

output "administrator_password" {
  value     = azurerm_mysql_flexible_server.store.administrator_password
  sensitive = true
}

output "connection_string" {
  value = "jdbc:mysql://${azurerm_mysql_flexible_server.store.fqdn}:3306/${azurerm_mysql_flexible_database.store.name}?sslMode=REQUIRED"
}
