resource "azurerm_resource_group" "store" {
  name     = "ptus04-store-rg"
  location = "Southeast Asia"
}

module "database" {
  source                  = "./modules/mysql_database"
  resource_group_name     = azurerm_resource_group.store.name
  resource_group_location = azurerm_resource_group.store.location
}

module "redis" {
  source                  = "./modules/managed_redis"
  resource_group_name     = azurerm_resource_group.store.name
  resource_group_location = azurerm_resource_group.store.location
}

module "storage" {
  source                  = "./modules/storage"
  resource_group_name     = azurerm_resource_group.store.name
  resource_group_location = azurerm_resource_group.store.location
}

resource "azurerm_service_plan" "store" {
  name                = "ptus04-store-app-plan"
  resource_group_name = azurerm_resource_group.store.name
  location            = azurerm_resource_group.store.location
  os_type             = "Linux"
  sku_name            = "B2"
}

resource "azurerm_linux_web_app" "store" {
  name                = "ptus04-store-app"
  resource_group_name = azurerm_resource_group.store.name
  location            = azurerm_resource_group.store.location
  service_plan_id     = azurerm_service_plan.store.id

  site_config {
    application_stack {
      docker_image_name = "phungtu081/store:latest"
    }
    # health_check_path = ""
    always_on = false
  }

  app_settings = {
    WEBSITES_PORT                   = "8080"
    DOCKER_ENABLE_CI                = true
    DATABASE_CONNECTION_STRING      = module.database.connection_string
    DATABASE_USERNAME               = module.database.administrator_login
    DATABASE_PASSWORD               = module.database.administrator_password
    REDIS_CONNECTION_STRING         = module.redis.connection_string
    AZURE_STORAGE_CONNECTION_STRING = module.storage.connection_string
    TWILIO_ACCOUNT_SID              = var.twilio_account_sid
    TWILIO_AUTH_TOKEN               = var.twilio_auth_token
    TWILIO_VERIFY_SERVICE_SID       = var.twilio_verify_service_sid
    ALLOWED_ORIGINS                 = "*"
    ALLOWED_METHODS                 = "GET,POST,PUT,DELETE"
    ALLOWED_HEADERS                 = "Content-Type,Authorization"
    ALLOW_CREDENTIALS               = true
  }
}
