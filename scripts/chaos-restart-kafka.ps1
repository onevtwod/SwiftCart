param(
  [int]$IntervalSeconds = 30
)

Write-Host "Starting chaos: restarting kafka every $IntervalSeconds seconds. Press Ctrl+C to stop."
while ($true) {
  docker restart swiftcart-kafka 2>$null
  Start-Sleep -Seconds $IntervalSeconds
}

