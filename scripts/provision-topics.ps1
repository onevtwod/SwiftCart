param(
  [string]$BootstrapServers = "localhost:9092"
)

$topics = @(
  "orders.confirmed.v1",
  "orders.failed.v1",
  "orders.confirmed.dlq",
  "orders.failed.dlq"
)

foreach ($t in $topics) {
  docker exec kafka kafka-topics.sh --bootstrap-server $BootstrapServers --create --if-not-exists --topic $t --partitions 3 --replication-factor 1 | Write-Output
}
