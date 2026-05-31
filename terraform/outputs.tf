output "app_url" {
  value = "https://${azurerm_linux_web_app.store.default_hostname}"
}

output "app_admin_url" {
  value = "https://${azurerm_linux_web_app.store_admin.default_hostname}"
}
