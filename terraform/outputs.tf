output "app_url" {
  value = "https://${azurerm_linux_web_app.store.default_hostname}"
}
