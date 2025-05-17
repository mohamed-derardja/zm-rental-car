# API Documentation for ZM Rental Car App

This document outlines the API requirements and integration points between the ZM Rental Car app and the backend services.

## Base URL
The app is configured to use the following base URL for all API calls:
```
https://api.zmrentals.com/api/v1/
```

> **Note to Backend Team**: Please confirm the actual production API base URL and update it in `ApiClient.kt` if different.

## API Endpoints

### Authentication

#### Login
- **Endpoint**: `POST /auth/login`
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "userPassword"
  }
  ```
- **Response**:
  ```json
  {
    "token": "jwt_token_here",
    "user": {
      "id": "user_id",
      "firstName": "John",
      "lastName": "Doe",
      "email": "user@example.com",
      "phone": "123456789",
      "profileImage": "http://example.com/profile.jpg",
      "address": {
        "street": "123 Main St",
        "city": "City",
        "state": "State",
        "zipCode": "12345",
        "country": "Country"
      },
      "drivingLicense": "License123",
      "dateOfBirth": "1990-01-01",
      "favorites": ["car_id_1", "car_id_2"]
    }
  }
  ```

#### Register
- **Endpoint**: `POST /auth/register`
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "userPassword",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "123456789"
  }
  ```
- **Response**: Same as login response

#### Password Reset
- **Endpoint**: `POST /auth/password-reset`
- **Request Body**:
  ```json
  {
    "email": "user@example.com"
  }
  ```
- **Response**: 200 OK (No body)

### User Management

#### Get User
- **Endpoint**: `GET /users/{id}`
- **Headers**: Authorization: Bearer {token}
- **Response**: User object

#### Update User
- **Endpoint**: `PUT /users/{id}`
- **Headers**: Authorization: Bearer {token}
- **Request Body**: User object (fields to update)
- **Response**: Updated User object

#### Get User Favorites
- **Endpoint**: `GET /users/{id}/favorites`
- **Headers**: Authorization: Bearer {token}
- **Response**: Array of Car objects

#### Add to Favorites
- **Endpoint**: `POST /users/{id}/favorites/{carId}`
- **Headers**: Authorization: Bearer {token}
- **Response**: 200 OK (No body)

#### Remove from Favorites
- **Endpoint**: `DELETE /users/{id}/favorites/{carId}`
- **Headers**: Authorization: Bearer {token}
- **Response**: 200 OK (No body)

### Cars

#### Get All Cars
- **Endpoint**: `GET /cars`
- **Response**: Array of Car objects
  ```json
  [
    {
      "id": "car_id_1",
      "name": "Toyota RAV4 2024",
      "brand": "Toyota",
      "model": "RAV4",
      "year": 2024,
      "category": "SUV",
      "imageUrl": "http://example.com/car.jpg",
      "rating": 4.9,
      "transmission": "Manual",
      "fuel": "Petrol",
      "seats": 5,
      "price": 100.00,
      "priceUnit": "DA/day",
      "description": "Description text",
      "availability": true,
      "features": ["GPS", "Bluetooth", "Backup Camera"],
      "location": "Algiers"
    }
  ]
  ```

#### Get Car by ID
- **Endpoint**: `GET /cars/{id}`
- **Response**: Car object

#### Search Cars
- **Endpoint**: `GET /cars/search?query={searchText}`
- **Response**: Array of Car objects

### Bookings

#### Get User Bookings
- **Endpoint**: `GET /bookings/user/{userId}`
- **Headers**: Authorization: Bearer {token}
- **Response**: Array of Booking objects

#### Get Booking by ID
- **Endpoint**: `GET /bookings/{id}`
- **Headers**: Authorization: Bearer {token}
- **Response**: Booking object

#### Create Booking
- **Endpoint**: `POST /bookings`
- **Headers**: Authorization: Bearer {token}
- **Request Body**:
  ```json
  {
    "userId": "user_id",
    "carId": "car_id",
    "startDate": "2023-06-15T10:00:00Z",
    "endDate": "2023-06-20T10:00:00Z",
    "totalPrice": 500.00
  }
  ```
- **Response**: Created Booking object

#### Cancel Booking
- **Endpoint**: `PUT /bookings/{id}/cancel`
- **Headers**: Authorization: Bearer {token}
- **Response**: Updated Booking object

#### Get Upcoming Bookings
- **Endpoint**: `GET /bookings/user/{userId}/upcoming`
- **Headers**: Authorization: Bearer {token}
- **Response**: Array of Booking objects with status CONFIRMED or ACTIVE

#### Get Completed Bookings
- **Endpoint**: `GET /bookings/user/{userId}/completed`
- **Headers**: Authorization: Bearer {token}
- **Response**: Array of Booking objects with status COMPLETED

## Data Models

### Car Model
```json
{
  "id": "string",
  "name": "string",
  "brand": "string",
  "model": "string",
  "year": "number",
  "category": "string",
  "imageUrl": "string",
  "rating": "number",
  "transmission": "string",
  "fuel": "string",
  "seats": "number",
  "price": "number",
  "priceUnit": "string",
  "description": "string",
  "availability": "boolean",
  "features": ["string"],
  "location": "string"
}
```

### User Model
```json
{
  "id": "string",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "phone": "string",
  "profileImage": "string (optional)",
  "address": {
    "street": "string",
    "city": "string",
    "state": "string",
    "zipCode": "string",
    "country": "string"
  },
  "drivingLicense": "string (optional)",
  "dateOfBirth": "string (optional)",
  "favorites": ["string (car ids)"]
}
```

### Booking Model
```json
{
  "id": "string",
  "userId": "string",
  "carId": "string",
  "startDate": "date",
  "endDate": "date",
  "totalPrice": "number",
  "status": "PENDING | CONFIRMED | ACTIVE | COMPLETED | CANCELLED",
  "paymentStatus": "PENDING | PAID | REFUNDED | FAILED",
  "createdAt": "date",
  "updatedAt": "date",
  "car": "Car object (optional)",
  "user": "User object (optional)"
}
```

## Authentication

The app uses JWT token authentication. All authenticated endpoints require the Authorization header with a Bearer token:

```
Authorization: Bearer {jwt_token}
```

## Error Handling

The API should return appropriate HTTP status codes:

- 200 OK: Successful request
- 201 Created: Resource created successfully
- 400 Bad Request: Invalid input
- 401 Unauthorized: Authentication failure
- 403 Forbidden: Permission denied
- 404 Not Found: Resource not found
- 500 Internal Server Error: Server error

All error responses should include a descriptive message:

```json
{
  "error": "Error message",
  "code": "ERROR_CODE"
}
```

## Implementation Notes

1. The Android app is using Retrofit for API communication
2. API responses need to match the data models exactly to avoid parsing issues
3. Dates are expected in ISO 8601 format
4. The app handles token storage and automatically includes it in authenticated requests

## Testing

For testing and development, the app expects the backend to support CORS and provide a test environment.

## Questions?

If you have any questions or need clarification on the API requirements, please contact the mobile development team.

---

*This documentation is intended for the backend development team of the ZM Rental Car app project.* 