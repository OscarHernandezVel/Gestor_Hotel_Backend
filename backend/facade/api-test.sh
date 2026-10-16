#!/usr/bin/env bash
# Walk through the whole flow. Usage: ./api-test.sh [base-url]
set -e
BASE=${1:-http://localhost:8080}
IN=$(date -d "+10 days" +%F); OUT=$(date -d "+13 days" +%F)
U="user$RANDOM"
j() { curl -s -H 'Content-Type: application/json' "$@"; echo; }

echo "== register / login"
j -X POST $BASE/api/auth/register -d "{\"username\":\"$U\",\"password\":\"secret123\"}"
TOKEN=$(curl -s -H 'Content-Type: application/json' -X POST $BASE/api/auth/login \
  -d "{\"username\":\"$U\",\"password\":\"secret123\"}" | sed 's/.*"token":"\([^"]*\)".*/\1/')
A="Authorization: Bearer $TOKEN"

echo "== no token -> 401";  j $BASE/api/hotel/reservations
echo "== availability";     j -H "$A" "$BASE/api/hotel/rooms/availability?from=$IN&to=$OUT&type=DOUBLE"
echo "== quote";            j -H "$A" "$BASE/api/hotel/quote?type=SUITE&checkIn=$IN&checkOut=$OUT"
echo "== reserve"
j -H "$A" -X POST $BASE/api/hotel/reservations \
  -d "{\"guestName\":\"Ana Ruiz\",\"guestEmail\":\"ana@mail.com\",\"roomType\":\"DOUBLE\",\"checkIn\":\"$IN\",\"checkOut\":\"$OUT\",\"extras\":[{\"type\":\"BREAKFAST\",\"days\":3}]}"
echo "== add spa";          j -H "$A" -X POST $BASE/api/hotel/reservations/1/services -d '{"type":"SPA","days":1}'
echo "== check-in";         j -H "$A" -X POST $BASE/api/hotel/reservations/1/check-in
echo "== check-out";        j -H "$A" -X POST $BASE/api/hotel/reservations/1/check-out
