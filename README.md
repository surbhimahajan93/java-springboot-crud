# Spring Boot MongoDB CRUD Application

A complete Spring Boot application with MongoDB integration for performing CRUD operations on products.

## Features

- **CRUD Operations**: Create, Read, Update, Delete products
- **Advanced Queries**: Search by category, price range, stock quantity
- **Validation**: Input validation with proper error handling
- **RESTful API**: Complete REST endpoints for all operations
- **MongoDB Integration**: Using Spring Data MongoDB
- **Gradle Build**: Modern dependency management with Gradle

## Prerequisites

- Java 17 or higher
- MongoDB (local installation or MongoDB Atlas)
- Gradle (or use the included wrapper)

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/springbootmongodbcrud/
│   │       ├── SpringBootMongoDbCrudApplication.java
│   │       ├── controller/
│   │       │   └── ProductController.java
│   │       ├── model/
│   │       │   └── Product.java
│   │       ├── repository/
│   │       │   └── ProductRepository.java
│   │       ├── service/
│   │       │   └── ProductService.java
│   │       └── exception/
│   │           └── GlobalExceptionHandler.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/example/springbootmongodbcrud/
            └── SpringBootMongoDbCrudApplicationTests.java
```

## Setup Instructions

### 1. MongoDB Setup

#### Option A: Local MongoDB
1. Install MongoDB on your system
2. Start MongoDB service
3. Create a database named `productdb`

#### Option B: MongoDB Atlas
1. Create a free account at [MongoDB Atlas](https://www.mongodb.com/atlas)
2. Create a cluster
3. Get your connection string
4. Update `application.properties` with your connection string

### 2. Application Setup

1. **Clone or navigate to the project directory**
   ```bash
   cd java-springboot-crud
   ```

2. **Build the project**
   ```bash
   ./gradlew build
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

   Or run the JAR file:
   ```bash
   java -jar build/libs/springboot-mongodb-crud-0.0.1-SNAPSHOT.jar
   ```

The application will start on `http://localhost:8080`

## API Endpoints

### Product Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/products` | Create a new product |
| GET | `/api/products` | Get all products |
| GET | `/api/products/{id}` | Get product by ID |
| GET | `/api/products/name/{name}` | Get product by name |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |

### Advanced Queries

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products/category/{category}` | Get products by category |
| GET | `/api/products/price-range?minPrice=X&maxPrice=Y` | Get products by price range |
| GET | `/api/products/low-stock?quantity=X` | Get products with low stock |
| GET | `/api/products/search?name=X` | Search products by name |
| GET | `/api/products/category/{category}/price-range?minPrice=X&maxPrice=Y` | Get products by category and price range |
| PATCH | `/api/products/{id}/stock?quantity=X` | Update stock quantity |

## Product Model

```json
{
  "id": "string",
  "name": "string (required, unique)",
  "description": "string",
  "price": "number (required, positive)",
  "category": "string",
  "stockQuantity": "number",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

## Example Usage

### Create a Product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "category": "Electronics",
    "stockQuantity": 10
  }'
```

### Get All Products
```bash
curl http://localhost:8080/api/products
```

### Get Product by ID
```bash
curl http://localhost:8080/api/products/{product-id}
```

### Update Product
```bash
curl -X PUT http://localhost:8080/api/products/{product-id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Laptop",
    "description": "Updated description",
    "price": 899.99,
    "category": "Electronics",
    "stockQuantity": 15
  }'
```

### Delete Product
```bash
curl -X DELETE http://localhost:8080/api/products/{product-id}
```

### Search Products by Category
```bash
curl http://localhost:8080/api/products/category/Electronics
```

### Get Products by Price Range
```bash
curl "http://localhost:8080/api/products/price-range?minPrice=100&maxPrice=1000"
```

## Configuration

The application uses the following default configuration in `application.properties`:

- **Server Port**: 8080
- **MongoDB Host**: localhost
- **MongoDB Port**: 27017
- **Database Name**: productdb

To use MongoDB Atlas, update the properties:

```properties
spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/productdb
```

## Testing

Run the tests using:
```bash
./gradlew test
```

## Docker Support

To run with Docker:

1. **Build the Docker image**
   ```bash
   docker build -t springboot-mongodb-crud .
   ```

2. **Run the container**
   ```bash
   docker run -p 8080:8080 springboot-mongodb-crud
   ```

## Troubleshooting

### Common Issues

1. **MongoDB Connection Error**
   - Ensure MongoDB is running
   - Check connection string in `application.properties`
   - Verify network connectivity

2. **Port Already in Use**
   - Change the port in `application.properties`
   - Kill the process using the port

3. **Validation Errors**
   - Check that required fields are provided
   - Ensure price is positive
   - Verify product name is unique

### Logs

Check application logs for detailed error information. The application logs at DEBUG level for MongoDB operations.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License. 