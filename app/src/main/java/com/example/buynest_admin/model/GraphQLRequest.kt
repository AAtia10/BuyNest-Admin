package com.example.buynest_admin.model

data class GraphQLRequest(
    val query: String,
    val variables: Map<String, Any>? = null
)

data class GraphQLResponse<T>(
    val data: T?,
    val errors: List<GraphQLError>?
)

data class GraphQLError(
    val message: String
)