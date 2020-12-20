INSERT INTO BOOKINGS (id, full_name, email, status, start_date, end_date) VALUES
  (6542, 'test test', 'abc@gmail.com', 'ACTIVE', CURRENT_DATE, DATEADD('DAY',+2, CURRENT_DATE)),
  (6543, 'test2 test2', 'def@gmail.com', 'ACTIVE', DATEADD('DAY',+62, CURRENT_DATE), DATEADD('DAY',+60, CURRENT_DATE));