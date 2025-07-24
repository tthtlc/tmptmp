#!/bin/bash

# Individual curl test scripts for each required functionality
# Run these commands individually to test specific features

BASE_URL="http://localhost:8484/api"

echo "=== Individual Test Commands ==="
echo "Copy and paste these commands to test each functionality individually"
echo ""

echo "1. ADD NORMAL USER:"
echo "First, get admin token:"
echo 'ADMIN_TOKEN=$(curl -s -X POST -H "Content-Type: application/json" -d '"'"'{"username":"admin","password":"password"}'"'"' '"$BASE_URL"'/auth/login | grep -o '"'"'"token":"[^"]*"'"'"' | cut -d'"'"''"'"' -f4)'
echo ""
echo "Then add normal user:"
echo 'curl -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '"'"'{
    "name": "John Doe",
    "username": "johndoe",
    "email": "john.doe@example.com",
    "password": "password123",
    "role": "USER"
  }'"'"' \
  '"$BASE_URL"'/admin/members'
echo ""

echo "2. ADD ADMIN USER:"
echo 'curl -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '"'"'{
    "name": "Jane Smith",
    "username": "janesmith",
    "email": "jane.smith@example.com",
    "password": "password123",
    "role": "ADMIN"
  }'"'"' \
  '"$BASE_URL"'/admin/members'
echo ""

echo "3. LIST ALL USERS:"
echo 'curl -X GET \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  '"$BASE_URL"'/admin/members'
echo ""

echo "4. LIST ALL BOOKS:"
echo 'curl -X GET '"$BASE_URL"'/books'
echo ""

echo "5. LIST ALL BOOK LOANS:"
echo 'curl -X GET \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  '"$BASE_URL"'/admin/loans'
echo ""

echo "=== Additional Useful Commands ==="
echo ""

echo "Add a sample book:"
echo 'curl -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '"'"'{
    "isbn": "978-0134685991",
    "title": "Effective Java",
    "author": "Joshua Bloch"
  }'"'"' \
  '"$BASE_URL"'/admin/books'
echo ""

echo "Create a loan:"
echo 'curl -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '"'"'{
    "memberId": 2,
    "isbn": "978-0134685991"
  }'"'"' \
  '"$BASE_URL"'/admin/loans'
echo ""

echo "User login:"
echo 'curl -X POST \
  -H "Content-Type: application/json" \
  -d '"'"'{
    "username": "johndoe",
    "password": "password123"
  }'"'"' \
  '"$BASE_URL"'/auth/login'
echo ""

echo "=== Notes ==="
echo "- Make sure the backend server is running on port 8484"
echo "- The admin user (username: admin, password: password) should be created automatically"
echo "- Replace member IDs and ISBNs with actual values from your database"
echo "- Use jq to format JSON responses: | jq '.'"

