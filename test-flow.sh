#!/usr/bin/env bash
set -euo pipefail

GATEWAY_URL="${GATEWAY_URL:-http://localhost:8080}"
GATEWAY_USER="${GATEWAY_USER:-farmer}"
GATEWAY_PASSWORD="${GATEWAY_PASSWORD:-change-this-gateway-password}"

curl --fail-with-body --silent --show-error \
  -u "$GATEWAY_USER:$GATEWAY_PASSWORD" \
  "$GATEWAY_URL/api/portal/dashboard"
printf '\n'

curl --fail-with-body --silent --show-error \
  -u "$GATEWAY_USER:$GATEWAY_PASSWORD" \
  -H "Content-Type: application/json" \
  -d '{"query":"What should I check before treating pests in rice?","language":"English","location":"Thanjavur"}' \
  "$GATEWAY_URL/api/advisory/process"
printf '\n'
