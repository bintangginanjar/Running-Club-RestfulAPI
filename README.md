# RESTFUL API for Running Club Site

Feel free to use & modify this API for register running club with its event

## 1. User Management

### a. Register User
Endpoint : POST /api/users

Request Header : None

Allowed User : Any Role

Request Body:
```json
{
    "username" : "username",
    "password" : "password",
    "role" : "ROLE_ADMIN / ROLE_USER"
}
```

Response Body:
```json
{
    "status": true,
    "messages": "User registration success",
    "errors": null,
    "data": {
        "username": "username",
        "role": [
            {
                "role": "ROLE_ADMIN"
            }
        ]
    }
}
```

### b. Get Current User
Endpoint : GET /api/users/current

Request Header :

* Authorization : Bearer + "Token" (mandatory)

Allowed User : 
* ROLE_ADMIN
* ROLE_USER

Response Body:
```json
{
    "status": true,
    "messages": "User fetching success",
    "errors": null,
    "data": {
        "username": "username",
        "role": [
            {
                "role": "ROLE_ADMIN"
            }
        ]
    }
}
```

### c. Update User Password
Endpoint : PATCH /api/users/current

Request Header :

* Authorization : Bearer + "Token" (mandatory)

Allowed User : 
* ROLE_ADMIN
* ROLE_USER

Request Body:
```json
{
    "username" : "username",
    "password" : "password"    
}
```

Response Body:
```json
{
    "status": true,
    "messages": "User password update success",
    "errors": null,
    "data": {
        "username": "bintang.ginanjar",
        "role": [
            {
                "role": "ROLE_ADMIN"
            }
        ]
    }
}
```

### d. Update User Role
Endpoint : PATCH /api/users/role

Request Header :

* Authorization : Bearer + "Token" (mandatory)

Allowed User : 
* ROLE_ADMIN

Request Body:
```json
{
    "username" : "username",
    "password" : "password"    
}
```

Response Body:
```json
{
    "status": true,
    "messages": "User role update success",
    "errors": null,
    "data": {
        "username": "username",
        "role": [
            {
                "role": "ROLE_USER"
            }
        ]
    }
}
```