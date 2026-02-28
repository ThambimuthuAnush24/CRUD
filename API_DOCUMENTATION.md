# Product REST API Documentation

## Base URL
```
http://localhost:8080/api/products
```

## Endpoints

### 1. Get All Products
**GET** `/api/products`

Returns all products. Supports optional search by keyword.

**Query Parameters:**
- `keyword` (optional): Search term to filter products by name, brand, or category

**Example Request:**
```bash
curl http://localhost:8080/api/products
curl http://localhost:8080/api/products?keyword=laptop
```

**Example Response:**
```json
{
  "success": true,
  "count": 5,
  "data": [
    {
      "id": 1,
      "name": "Laptop",
      "brand": "Dell",
      "category": "Electronics",
      "price": 999.99,
      "description": "High-performance laptop",
      "createdAt": "2026-02-28T10:30:00",
      "imageFileName": "laptop.jpg"
    }
  ]
}
```

---

### 2. Get Product by ID
**GET** `/api/products/{id}`

Returns a single product by its ID.

**Example Request:**
```bash
curl http://localhost:8080/api/products/1
```

**Example Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Laptop",
    "brand": "Dell",
    "category": "Electronics",
    "price": 999.99,
    "description": "High-performance laptop",
    "createdAt": "2026-02-28T10:30:00",
    "imageFileName": "laptop.jpg"
  }
}
```

**Error Response (404):**
```json
{
  "success": false,
  "message": "Product not found with id: 99"
}
```

---

### 3. Create Product
**POST** `/api/products`

Creates a new product (without image upload via API).

**Request Body:**
```json
{
  "name": "Wireless Mouse",
  "brand": "Logitech",
  "category": "Accessories",
  "price": 29.99,
  "description": "Ergonomic wireless mouse with precision tracking"
}
```

**Example Request:**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Mouse",
    "brand": "Logitech",
    "category": "Accessories",
    "price": 29.99,
    "description": "Ergonomic wireless mouse"
  }'
```

**Example Response (201 Created):**
```json
{
  "success": true,
  "message": "Product created successfully",
  "data": {
    "id": 6,
    "name": "Wireless Mouse",
    "brand": "Logitech",
    "category": "Accessories",
    "price": 29.99,
    "description": "Ergonomic wireless mouse",
    "createdAt": "2026-02-28T14:25:00",
    "imageFileName": null
  }
}
```

---

### 4. Update Product
**PUT** `/api/products/{id}`

Updates an existing product.

**Request Body:**
```json
{
  "name": "Updated Product Name",
  "price": 149.99
}
```

**Example Request:**
```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gaming Laptop",
    "price": 1299.99
  }'
```

**Example Response:**
```json
{
  "success": true,
  "message": "Product updated successfully",
  "data": {
    "id": 1,
    "name": "Gaming Laptop",
    "brand": "Dell",
    "category": "Electronics",
    "price": 1299.99,
    "description": "High-performance laptop",
    "createdAt": "2026-02-28T10:30:00",
    "imageFileName": "laptop.jpg"
  }
}
```

---

### 5. Delete Product
**DELETE** `/api/products/{id}`

Deletes a product by its ID.

**Example Request:**
```bash
curl -X DELETE http://localhost:8080/api/products/6
```

**Example Response:**
```json
{
  "success": true,
  "message": "Product deleted successfully"
}
```

---

### 6. Get Products by Category
**GET** `/api/products/category/{category}`

Returns all products matching a specific category.

**Example Request:**
```bash
curl http://localhost:8080/api/products/category/Electronics
```

**Example Response:**
```json
{
  "success": true,
  "category": "Electronics",
  "count": 3,
  "data": [
    {
      "id": 1,
      "name": "Laptop",
      "brand": "Dell",
      "category": "Electronics",
      "price": 999.99,
      "description": "High-performance laptop",
      "createdAt": "2026-02-28T10:30:00",
      "imageFileName": "laptop.jpg"
    }
  ]
}
```

---

### 7. Get Product Statistics
**GET** `/api/products/stats`

Returns statistical information about all products.

**Example Request:**
```bash
curl http://localhost:8080/api/products/stats
```

**Example Response:**
```json
{
  "success": true,
  "data": {
    "totalProducts": 10,
    "totalValue": 5499.50,
    "averagePrice": 549.95,
    "maxPrice": 1299.99,
    "minPrice": 19.99
  }
}
```

---

## Error Handling

All endpoints return consistent error responses:

**Example Error Response:**
```json
{
  "success": false,
  "message": "Error description",
  "timestamp": "2026-02-28T14:30:00",
  "path": "/api/products/999"
}
```

**HTTP Status Codes:**
- `200 OK`: Successful GET, PUT, DELETE
- `201 Created`: Successful POST
- `400 Bad Request`: Invalid input data
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

---

## Testing with JavaScript (Fetch API)

```javascript
// Get all products
fetch('http://localhost:8080/api/products')
  .then(response => response.json())
  .then(data => console.log(data));

// Create a product
fetch('http://localhost:8080/api/products', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    name: 'New Product',
    brand: 'BrandName',
    category: 'Category',
    price: 99.99,
    description: 'Product description'
  })
})
  .then(response => response.json())
  .then(data => console.log(data));
```

---

## Notes

- All endpoints support CORS for frontend integration
- Image upload is not supported via REST API (use the web UI at `/products/create`)
- Dates are returned in ISO-8601 format
- All responses include a `success` boolean field
