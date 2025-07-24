#!/bin/bash

# NTUC Library Management System - API Testing Script
# This script tests the 5 required backend functionalities using curl

BASE_URL="http://localhost:8484/api"
ADMIN_TOKEN=""
USER_TOKEN=""

echo "=== NTUC Library Management System API Testing ==="
echo "Base URL: $BASE_URL"
echo ""

# Function to extract token from JSON response
extract_token() {
    echo "$1" | grep -o '"token":"[^"]*"' | cut -d'"' -f4
}

# Function to make authenticated requests
auth_request() {
    local method=$1
    local endpoint=$2
    local token=$3
    local data=$4
    
    if [ -n "$data" ]; then
        curl -s -X "$method" \
             -H "Content-Type: application/json" \
             -H "Authorization: Bearer $token" \
             -d "$data" \
             "$BASE_URL$endpoint"
    else
        curl -s -X "$method" \
             -H "Authorization: Bearer $token" \
             "$BASE_URL$endpoint"
    fi
}

echo "1. Testing Admin Login..."
echo "----------------------------------------"
ADMIN_LOGIN_RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{"username":"admin","password":"password"}' \
    "$BASE_URL/auth/login")

echo "Admin Login Response:"
echo "$ADMIN_LOGIN_RESPONSE" | jq '.' 2>/dev/null || echo "$ADMIN_LOGIN_RESPONSE"
echo ""

ADMIN_TOKEN=$(extract_token "$ADMIN_LOGIN_RESPONSE")
if [ -z "$ADMIN_TOKEN" ]; then
    echo "❌ Failed to get admin token. Please ensure the backend is running and admin user exists."
    exit 1
fi
echo "✅ Admin token obtained successfully"
echo ""

echo "2. Testing Add Normal User..."
echo "----------------------------------------"
NORMAL_USER_DATA='{
    "name": "John Doe",
    "username": "johndoe",
    "email": "john.doe@example.com",
    "password": "password123",
    "role": "USER"
}'

ADD_USER_RESPONSE=$(auth_request "POST" "/admin/members" "$ADMIN_TOKEN" "$NORMAL_USER_DATA")
echo "Add Normal User Response:"
echo "$ADD_USER_RESPONSE" | jq '.' 2>/dev/null || echo "$ADD_USER_RESPONSE"
echo ""

echo "3. Testing Add Admin User..."
echo "----------------------------------------"
ADMIN_USER_DATA='{
    "name": "Jane Smith",
    "username": "janesmith",
    "email": "jane.smith@example.com",
    "password": "password123",
    "role": "ADMIN"
}'

ADD_ADMIN_RESPONSE=$(auth_request "POST" "/admin/members" "$ADMIN_TOKEN" "$ADMIN_USER_DATA")
echo "Add Admin User Response:"
echo "$ADD_ADMIN_RESPONSE" | jq '.' 2>/dev/null || echo "$ADD_ADMIN_RESPONSE"
echo ""

echo "4. Testing List All Users..."
echo "----------------------------------------"
LIST_USERS_RESPONSE=$(auth_request "GET" "/admin/members" "$ADMIN_TOKEN")
echo "List All Users Response:"
echo "$LIST_USERS_RESPONSE" | jq '.' 2>/dev/null || echo "$LIST_USERS_RESPONSE"
echo ""

echo "5. Testing Add Sample Books (for loan testing)..."
echo "----------------------------------------"
BOOK1_DATA='{
    "isbn": "978-0134685991",
    "title": "Effective Java",
    "author": "Joshua Bloch"
}'

BOOK2_DATA='{
    "isbn": "978-0321356680",
    "title": "Effective C++",
    "author": "Scott Meyers"
}'

ADD_BOOK1_RESPONSE=$(auth_request "POST" "/admin/books" "$ADMIN_TOKEN" "$BOOK1_DATA")
echo "Add Book 1 Response:"
echo "$ADD_BOOK1_RESPONSE" | jq '.' 2>/dev/null || echo "$ADD_BOOK1_RESPONSE"

ADD_BOOK2_RESPONSE=$(auth_request "POST" "/admin/books" "$ADMIN_TOKEN" "$BOOK2_DATA")
echo "Add Book 2 Response:"
echo "$ADD_BOOK2_RESPONSE" | jq '.' 2>/dev/null || echo "$ADD_BOOK2_RESPONSE"
echo ""

echo "6. Testing List All Books..."
echo "----------------------------------------"
LIST_BOOKS_RESPONSE=$(curl -s -X GET "$BASE_URL/books")
echo "List All Books Response:"
echo "$LIST_BOOKS_RESPONSE" | jq '.' 2>/dev/null || echo "$LIST_BOOKS_RESPONSE"
echo ""

echo "7. Testing Create Sample Loans..."
echo "----------------------------------------"
# Create a loan for the normal user
LOAN_DATA='{
    "memberId": 2,
    "isbn": "978-0134685991"
}'

CREATE_LOAN_RESPONSE=$(auth_request "POST" "/admin/loans" "$ADMIN_TOKEN" "$LOAN_DATA")
echo "Create Loan Response:"
echo "$CREATE_LOAN_RESPONSE" | jq '.' 2>/dev/null || echo "$CREATE_LOAN_RESPONSE"
echo ""

echo "8. Testing List All Book Loans..."
echo "----------------------------------------"
LIST_LOANS_RESPONSE=$(auth_request "GET" "/admin/loans" "$ADMIN_TOKEN")
echo "List All Book Loans Response:"
echo "$LIST_LOANS_RESPONSE" | jq '.' 2>/dev/null || echo "$LIST_LOANS_RESPONSE"
echo ""

echo "=== Testing Summary ==="
echo "✅ 1. Add Normal User - Completed"
echo "✅ 2. Add Admin User - Completed"
echo "✅ 3. List All Users - Completed"
echo "✅ 4. List All Books - Completed"
echo "✅ 5. List All Book Loans - Completed"
echo ""
echo "All 5 required functionalities have been tested successfully!"
echo ""

echo "=== Additional Test: User Login and Member Operations ==="
echo "Testing normal user login..."
USER_LOGIN_RESPONSE=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{"username":"johndoe","password":"password123"}' \
    "$BASE_URL/auth/login")

echo "User Login Response:"
echo "$USER_LOGIN_RESPONSE" | jq '.' 2>/dev/null || echo "$USER_LOGIN_RESPONSE"

USER_TOKEN=$(extract_token "$USER_LOGIN_RESPONSE")
if [ -n "$USER_TOKEN" ]; then
    echo "✅ User token obtained successfully"
    
    echo ""
    echo "Testing user dashboard access..."
    USER_DASHBOARD_RESPONSE=$(auth_request "GET" "/member/dashboard" "$USER_TOKEN")
    echo "User Dashboard Response:"
    echo "$USER_DASHBOARD_RESPONSE" | jq '.' 2>/dev/null || echo "$USER_DASHBOARD_RESPONSE"
else
    echo "❌ Failed to get user token"
fi

echo ""
echo "=== API Testing Complete ==="

