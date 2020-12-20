output=$(curl -H "Content-Type: application/json" -d @concurrent-body.json http://localhost:8080/v1/bookings & \
 curl -H "Content-Type: application/json" -d @concurrent-body-2.json http://localhost:8080/v1/bookings)
echo "$output";
