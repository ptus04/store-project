terraform {
  # cloud {
  #   organization = "ptus04-test-org"
  #   workspaces {
  #     name = "store-deploy"
  #   }
  # }

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "4.71.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "3.7.2"
    }
  }
}

provider "azurerm" {
  resource_provider_registrations = "none"
  features {}
}
