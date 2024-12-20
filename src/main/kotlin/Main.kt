package com.example

import aws.sdk.kotlin.hll.dynamodbmapper.DynamoDbItem
import aws.sdk.kotlin.hll.dynamodbmapper.DynamoDbMapper
import aws.sdk.kotlin.hll.dynamodbmapper.DynamoDbPartitionKey
import aws.sdk.kotlin.hll.dynamodbmapper.DynamoDbSortKey
import aws.sdk.kotlin.hll.dynamodbmapper.expressions.KeyFilter
import aws.sdk.kotlin.hll.dynamodbmapper.items.ItemSchema
import aws.sdk.kotlin.hll.dynamodbmapper.items.KeySpec
import aws.sdk.kotlin.hll.dynamodbmapper.model.Table
import aws.sdk.kotlin.hll.dynamodbmapper.operations.getItem
import aws.sdk.kotlin.hll.dynamodbmapper.operations.putItem
import aws.sdk.kotlin.hll.dynamodbmapper.operations.queryPaginated
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.createTable
import aws.sdk.kotlin.services.dynamodb.deleteTable
import aws.sdk.kotlin.services.dynamodb.describeTable
import aws.sdk.kotlin.services.dynamodb.model.*
import aws.sdk.kotlin.services.dynamodb.waiters.waitUntilTableExists
import aws.sdk.kotlin.services.dynamodb.waiters.waitUntilTableNotExists
import aws.smithy.kotlin.runtime.ExperimentalApi
import aws.smithy.kotlin.runtime.InternalApi
import com.example.dynamodbmapper.generatedschemas.BookConverter
import com.example.dynamodbmapper.generatedschemas.BookSchema
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalApi::class)
fun main() = runBlocking {

    println("Hello, Kotlin/JVM with Amazon Corretto 21!")
    println("Kotlin version: ${KotlinVersion.CURRENT}")
    println("Java version: ${System.getProperty("java.version")}")
    println("Java vendor: ${System.getProperty("java.vendor")}")

    Application().run()
}

@DynamoDbItem
data class Book(
    @DynamoDbPartitionKey
    val title: String,

    @DynamoDbSortKey
    val volume: String,

    val author: String,
    val publicationDate: String,
    val price: Int,
    val rate: Double,
) {
    companion object {
        const val TABLE_NAME = "books"

        val pkAttributeType = ScalarAttributeType.S
        val skAttributeType = ScalarAttributeType.S
    }

    object Lsi {
        const val INDEX_NAME = "books-lsi-by-rate"

        @OptIn(ExperimentalApi::class)
        val skKeySpec = KeySpec.Number("rate")
        val skAttributeType = ScalarAttributeType.N
    }

    object Gsi {
        const val INDEX_NAME = "books-gsi-by-author"

        @OptIn(ExperimentalApi::class)
        val pkKeySpec = KeySpec.String("author")
        val pkAttributeType = ScalarAttributeType.S

        @OptIn(ExperimentalApi::class)
        val skKeySpec = KeySpec.String("publicationDate")
        val skAttributeType = ScalarAttributeType.S
    }
}

class Application {
    @OptIn(ExperimentalApi::class, InternalApi::class)
    suspend fun run() {

        println("デモ始まり！")
        DynamoDbClient.fromEnvironment {}.use { ddb ->
            val table = getTable(ddb)
            createTableIfNotExists(table)
            putData(table)
            // getItemExample1(table)
            getItemExample2(table)
            queryLsiExample(table)
            queryGsiExample(table)
            deleteTable(table)
        }
        println("デモ完了！")
    }

    @OptIn(ExperimentalApi::class)
    private fun getTable(ddb: DynamoDbClient) =
    // 拡張関数のget${クラス名}Tableを使ってもOK
        // mapper.getBookTable(Book.TABLE_NAME)
        DynamoDbMapper(ddb).getTable(Book.TABLE_NAME, BookSchema)

    @OptIn(ExperimentalApi::class)
    private suspend fun createTableIfNotExists(table: Table.CompositeKey<Book, String, String>) {

        val tableSchema = table.schema
        val ddb = table.mapper.client

        try {
            ddb.describeTable {
                tableName = table.tableName
            }
            println("The table already exists")
            return
        } catch (e: ResourceNotFoundException) {
            println("The table does not exist")
        }

        val response = ddb.createTable {
            tableName = table.tableName
            attributeDefinitions = listOf(
                AttributeDefinition {
                    attributeName = tableSchema.partitionKey.name
                    attributeType = Book.pkAttributeType
                },
                AttributeDefinition {
                    attributeName = tableSchema.sortKey.name
                    attributeType = Book.skAttributeType
                },
                AttributeDefinition {
                    attributeName = Book.Lsi.skKeySpec.name
                    attributeType = Book.Lsi.skAttributeType
                },
                AttributeDefinition {
                    attributeName = Book.Gsi.pkKeySpec.name
                    attributeType = Book.Gsi.pkAttributeType
                },
                AttributeDefinition {
                    attributeName = Book.Gsi.skKeySpec.name
                    attributeType = Book.Gsi.skAttributeType
                }
            )
            keySchema = listOf(
                KeySchemaElement {
                    attributeName = tableSchema.partitionKey.name
                    keyType = KeyType.Hash
                },
                KeySchemaElement {
                    attributeName = tableSchema.sortKey.name
                    keyType = KeyType.Range
                }
            )
            localSecondaryIndexes = listOf(LocalSecondaryIndex {
                indexName = Book.Lsi.INDEX_NAME
                keySchema = listOf(
                    KeySchemaElement {
                        attributeName = tableSchema.partitionKey.name
                        keyType = KeyType.Hash
                    },
                    KeySchemaElement {
                        attributeName = Book.Lsi.skKeySpec.name
                        keyType = KeyType.Range
                    }
                )
                projection = Projection {
                    projectionType = ProjectionType.All
                }
            })
            globalSecondaryIndexes = listOf(GlobalSecondaryIndex {
                indexName = Book.Gsi.INDEX_NAME
                keySchema = listOf(
                    KeySchemaElement {
                        attributeName = Book.Gsi.pkKeySpec.name
                        keyType = KeyType.Hash
                    },
                    KeySchemaElement {
                        attributeName = Book.Gsi.skKeySpec.name
                        keyType = KeyType.Range
                    }
                )
                projection = Projection {
                    projectionType = ProjectionType.All
                }
                provisionedThroughput = ProvisionedThroughput {
                    readCapacityUnits = 1
                    writeCapacityUnits = 1
                }
            })
            provisionedThroughput = ProvisionedThroughput {
                readCapacityUnits = 1
                writeCapacityUnits = 1
            }
        }
        ddb.waitUntilTableExists {
            tableName = response.tableDescription?.tableName
        }
        println("The table was successfully created ${response.tableDescription?.tableArn}")
        response.tableDescription?.tableName
    }

    // 0. putItem　Bookをそのまま渡してputItemが可能
    @OptIn(ExperimentalApi::class)
    private suspend fun putData(table: Table.CompositeKey<Book, String, String>) {
        table.putItem {
            item = Book(
                title = "1から始めるKotlin",
                volume = "1",
                author = "激辛無理助",
                publicationDate = "2022-01-01",
                price = 1000,
                rate = 3.5
            )
        }
        table.putItem {
            item = Book(
                title = "1から始めるKotlin",
                volume = "2",
                author = "激辛無理助",
                publicationDate = "2023-01-01",
                price = 1000,
                rate = 1.2
            )
        }
        table.putItem {
            item = Book(
                title = "俺たちは雰囲気でKotlinをやっている",
                volume = "1",
                author = "激辛無理助",
                publicationDate = "2024-01-01",
                price = 5000,
                rate = 2.8
            )
        }
    }

    /* 1-1. getItem　パーティションキーとソートキーを指定するパターン */
    /* NotImplementedError が出るので、おそらく未開発 */
    @OptIn(ExperimentalApi::class)
    private suspend fun getItemExample1(table: Table.CompositeKey<Book, String, String>) {
        val item0 = table.getItem(
            partitionKey = "1から始めるKotlin",
            sortKey = "1"
        )
        println("getItem()の結果: $item0")
    }

    // 1-2. getItem() ビルダーDSLを使うパターン
    // Bookインスタンスを渡して検索する方のメソッドは動く
    // DynamoDBのキー以外の値は無視される
    @OptIn(ExperimentalApi::class)
    private suspend fun getItemExample2(table: Table.CompositeKey<Book, String, String>) {
        val item1 = table.getItem {
            key = Book(
                title = "1から始めるKotlin",
                volume = "1",

                // DynamoDBのキー以外の値は無視される
                author = "",
                publicationDate = "",
                price = 0,
                rate = 0.0,
            )
        }.item
        println("getItem()の結果: $item1")
    }

    // 2. query()　LSIを使ってqueryを行う
    @OptIn(ExperimentalApi::class)
    private suspend fun queryLsiExample(table: Table.CompositeKey<Book, String, String>) {
        val lsi = table.getIndex(
            name = Book.Lsi.INDEX_NAME,
            schema = ItemSchema(
                converter = BookConverter,
                partitionKey = BookSchema.partitionKey,
                sortKey = Book.Lsi.skKeySpec
            )
        )
        lsi.queryPaginated {
            keyCondition = KeyFilter(partitionKey = "1から始めるKotlin") {
                sortKey gt 3.0
            }
            filter {
                attr("price") lt 1500
            }
        }.collect { response ->
            response.items.orEmpty().forEach { item2 ->
                println("LSIを使った検索結果 : $item2")
            }
        }
    }

    // 3. query() GSIを使ってqueryを行う
    @OptIn(ExperimentalApi::class)
    private suspend fun queryGsiExample(table: Table.CompositeKey<Book, String, String>) {
        val gsi = table.getIndex(
            name = Book.Gsi.INDEX_NAME,
            schema = ItemSchema(
                converter = BookConverter,
                partitionKey = Book.Gsi.pkKeySpec,
                sortKey = Book.Gsi.skKeySpec
            )
        )
        gsi.queryPaginated {
            keyCondition = KeyFilter(partitionKey = "激辛無理助") {
                sortKey gt "2022-02-01"
            }
        }.collect { response ->
            response.items.orEmpty().forEach { item3 ->
                println("GSIを使った検索結果 : $item3")
            }
        }
    }

    @OptIn(ExperimentalApi::class)
    private suspend fun deleteTable(table: Table.CompositeKey<Book, String, String>) {
        val ddb = table.mapper.client
        val response = ddb.deleteTable {
            tableName = table.tableName
        }
        ddb.waitUntilTableNotExists {
            tableName = table.tableName
        }
        println("The table was successfully deleted ${response.tableDescription?.tableName}")
    }
}
