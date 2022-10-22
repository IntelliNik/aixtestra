# StudentsController

All URIs are relative to `""`

The controller class is defined in **[StudentsController.java](../../src/main/java/com/aixtra/couchcode/controller/StudentsController.java)**

Method | HTTP request | Description
------------- | ------------- | -------------
[**ping**](#ping) | **GET** /ping | A simple endpoint to test interaction
[**solve**](#solve) | **POST** /solve | Tries to extract the information of a given task.

<a name="ping"></a>
# **ping**
```java
Mono<Object> StudentsController.ping()
```

A simple endpoint to test interaction

This endpoint is used to determine, whether or not the system is  generally available. It is used before every rating run. System which  are not available or unable to react to the ping in time, will be  disqualified and not further considered during the rating run. 



### Authorization
* **oAuth2**

### HTTP request headers
 - **Accepts Content-Type**: Not defined
 - **Produces Content-Type**: Not defined

<a name="solve"></a>
# **solve**
```java
Mono<Solution> StudentsController.solve(_body)
```

Tries to extract the information of a given task.

For a task image, provided in the request body in base64 format, the service tries to retrieve the information  about the product and embed it in the provided model. 

### Parameters
Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
**_body** | `CompletedFileUpload` | The task image in base64 encoding.  | [optional parameter]

### Return type
[**Solution**](../../docs/models/Solution.md)

### Authorization
* **oAuth2**

### HTTP request headers
 - **Accepts Content-Type**: `image/png`
 - **Produces Content-Type**: `application/json`

