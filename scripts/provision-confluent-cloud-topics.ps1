param(
  [string]$ApiKey,
  [string]$ApiSecret,
  [string]$BootstrapServer,
  [string]$ClusterId,
  [string]$EnvironmentId
)

if (-not $ApiKey -or -not $ApiSecret) { throw "ApiKey/ApiSecret required" }

Write-Host "Provisioning topics in Confluent Cloud..."
$topics = @("orders.confirmed.v1","orders.failed.v1","orders.confirmed.dlq","orders.failed.dlq")
foreach ($t in $topics) {
  confluent kafka topic create $t --environment $EnvironmentId --cluster $ClusterId --config retention.ms=604800000 --if-not-exists | Write-Output
}

