variable "twilio_account_sid" {
  type      = string
  sensitive = true
}

variable "twilio_auth_token" {
  type      = string
  sensitive = true
}

variable "twilio_verify_service_sid" {
  type      = string
  sensitive = true
}

variable "mail_username" {
  type      = string
  sensitive = true
}

variable "mail_password" {
  type      = string
  sensitive = true
}

variable "jwt_secret" {
  type      = string
  sensitive = true
}

variable "jwt_expiration_ms" {
  type = number
}

variable "sepay_bank" {
  type = string
}

variable "sepay_account_name" {
  type = string
}

variable "sepay_account_number" {
  type      = string
  sensitive = true
}

variable "sepay_username" {
  type      = string
  sensitive = true
}

variable "sepay_password" {
  type      = string
  sensitive = true
}

variable "sepay_invoice_auth_url" {
  type      = string
  sensitive = true
}

variable "sepay_invoice_create_url" {
  type      = string
  sensitive = true
}

variable "sepay_invoice_check_url" {
  type      = string
  sensitive = true
}

variable "gemini_api_key" {
  type      = string
  sensitive = true
}

variable "rabbitmq_host" {
  type = string
}

variable "rabbitmq_port" {
  type = string
}

variable "rabbitmq_username" {
  type      = string
  sensitive = true
}

variable "rabbitmq_password" {
  type      = string
  sensitive = true
}
