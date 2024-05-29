# GPC Recruitment App

Simple application for working with a xml file.

## API Endpoints

base url: `http://localhost:8080`

### Read XML File and Get Products Length

- **URL:** `/api/v1/products/read-file`
- **Method:** `GET`
- **Description:** Reads the XML file, parses it, and returns the number of products.

### Get All Products in JSON Format

- **URL:** `/api/v1/products/all`
- **Method:** `GET`
- **Description:** Retrieves all products from the XML file and returns them in JSON format.

### Get Products by Name

- **URL:** `/api/v1/products/{name}`
- **Method:** `GET`
- **Description:** Retrieves products with the specified name from the XML file.

### Get XML File Content

- **URL:** `/api/v1/products/xml`
- **Method:** `GET`
- **Description:** Retrieves the content of the XML file.
- **Response:** XML content as a string.

### Update XML File

- **URL:** `/api/v1/products/update-file`
- **Method:** `PUT`
- **Description:** Replaces the content of the XML file with the provided file.


## Running using Docker
 
Follow these steps to deploy the project using Docker:

1. Build the Docker image:
    ```
    docker compose build
    ```
2. Run the Docker container:
    ```
    docker compose up -d
    ```

## Technologies Used

Backend:
- Java 17
- Spring Boot 3.2.6
- jackson-dataformat-xml 2.15.4
- lombok 1.18.32
- springdoc-openapi-starter-webmvc-ui 2.0.2
- Maven 3.9.6

Frontend:
- react 18.3.1
- tailwind 3.4.3
- Typescript 4.9.5

### Swagger API documentation 

http://localhost:8080/swagger-ui/index.html

### Simple website to test the api (/frontend)

http://localhost:3000

