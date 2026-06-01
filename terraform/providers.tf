terraform {
  cloud {
    organization = "DragonSharpOrg"
    workspaces {
      name = "store-prod-deploy"
    }
  }

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "4.75.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "3.7.2"
    }
  }
}

provider "azurerm" {
  resource_provider_registrations = "none"
  use_cli                         = false
  features {}
}
