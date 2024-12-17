import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.createTable
import aws.sdk.kotlin.services.dynamodb.deleteTable
import aws.sdk.kotlin.services.dynamodb.model.*
import aws.sdk.kotlin.services.dynamodb.waiters.waitUntilTableExists
import aws.sdk.kotlin.services.dynamodb.waiters.waitUntilTableNotExists
import kotlinx.coroutines.runBlocking


fun main() {

    println("Hello, Kotlin/JVM with Amazon Corretto 21!")
    println("Kotlin version: ${KotlinVersion.CURRENT}")
    println("Java version: ${System.getProperty("java.version")}")
    println("Java vendor: ${System.getProperty("java.vendor")}")

    val tableName = createTableExample()
    deleteTableExample(tableName)
}

fun createTableExample(): String? = runBlocking {
    DynamoDbClient.fromEnvironment().use { ddb ->
        val key = "id"
        val response = ddb.createTable {
            tableName = "test"
            attributeDefinitions = listOf(AttributeDefinition {
                attributeName = key
                attributeType = ScalarAttributeType.S
            })
            keySchema = listOf(KeySchemaElement {
                attributeName = key
                keyType = KeyType.Hash
            })
            billingMode = BillingMode.PayPerRequest
        }
        ddb.waitUntilTableExists {
            tableName = response.tableDescription?.tableName
        }
        println("The table was successfully created ${response.tableDescription?.tableArn}")
        response.tableDescription?.tableName
    }
}

fun deleteTableExample(name: String?) = runBlocking {
    DynamoDbClient.fromEnvironment().use { ddb ->
        val response = ddb.deleteTable {
            tableName = name
        }
        ddb.waitUntilTableNotExists {
            tableName = name
        }
        println("The table was successfully deleted ${response.tableDescription?.tableName}")
    }
}