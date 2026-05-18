resource "random_string" "administrator_login" {
  length           = 10
  override_special = "_"
}

resource "random_password" "administrator_password" {
  length = 16
}

resource "azurerm_mysql_flexible_server" "store" {
  name                   = "ptus04-store-mysql-server"
  resource_group_name    = var.resource_group_name
  location               = var.resource_group_location
  sku_name               = "B_Standard_B1ms"
  administrator_login    = random_string.administrator_login.result
  administrator_password = random_password.administrator_password.result
  version                = "8.0.21"
}

resource "azurerm_mysql_flexible_database" "store" {
  name                = "storedb"
  resource_group_name = var.resource_group_name
  server_name         = azurerm_mysql_flexible_server.store.name
  charset             = "utf8mb3"
  collation           = "utf8mb3_general_ci"
}

resource "azurerm_mysql_flexible_server_firewall_rule" "store" {
  name                = "allow-app"
  resource_group_name = var.resource_group_name
  server_name         = azurerm_mysql_flexible_server.store.name
  start_ip_address    = "10.0.0.0"
  end_ip_address      = "239.0.0.0"
}
