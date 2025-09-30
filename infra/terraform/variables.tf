variable "resource_group_name" { type = string default = "rg-swiftcart" }
variable "location" { type = string default = "eastus" }
variable "aks_name" { type = string default = "aks-swiftcart" }
variable "node_count" { type = number default = 2 }
variable "node_size" { type = string default = "Standard_DS2_v2" }

