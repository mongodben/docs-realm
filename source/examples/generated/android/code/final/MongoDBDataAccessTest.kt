package com.mongodb.realm.examples.kotlin

import android.util.Log
import com.mongodb.realm.examples.Expectation
import com.mongodb.realm.examples.RealmTest
import com.mongodb.realm.examples.YOUR_APP_ID
import com.mongodb.realm.examples.model.Plant
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration
import io.realm.mongodb.Credentials
import io.realm.mongodb.User
import io.realm.mongodb.mongo.events.BaseChangeEvent
import io.realm.mongodb.mongo.iterable.MongoCursor
import io.realm.mongodb.mongo.options.InsertManyResult
import io.realm.mongodb.mongo.options.UpdateOptions
import io.realm.mongodb.mongo.result.DeleteResult
import io.realm.mongodb.mongo.result.InsertOneResult
import io.realm.mongodb.mongo.result.UpdateResult
import org.bson.Document
import org.bson.types.ObjectId
import org.junit.Before
import org.junit.Test
import java.util.*


class MongoDBDataAccessTest : RealmTest() {
    @Before
    fun setUpData() {
        val expectation = Expectation()
        activity!!.runOnUiThread {
            val appID = YOUR_APP_ID // replace this with your App ID
            val app = App(AppConfiguration.Builder(appID).build())
            val credentials = Credentials.anonymous()
            app.loginAsync(
                credentials
            ) { it: App.Result<User?> ->
                if (it.isSuccess) {
                    Log.v("EXAMPLE", "Successfully authenticated.")
                    val user = app.currentUser()
                    val mongoClient =
                        user!!.getMongoClient("mongodb-atlas") // service for MongoDB Atlas cluster containing custom user data
                    val mongoDatabase =
                        mongoClient.getDatabase("plant-data-database")
                    val mongoCollection =
                        mongoDatabase.getCollection("plant-data-collection")
                    mongoCollection.insertMany(
                        Arrays.asList(
                            Plant(
                                ObjectId(),
                                "venus flytrap",
                                "full",
                                "white",
                                "perennial",
                                "Store 42"
                            ),
                            Plant(
                                ObjectId(),
                                "sweet basil",
                                "partial",
                                "green",
                                "annual",
                                "Store 42"
                            ),
                            Plant(
                                ObjectId(),
                                "thai basil",
                                "partial",
                                "green",
                                "perennial",
                                "Store 42"
                            ),
                            Plant(
                                ObjectId(),
                                "helianthus",
                                "full",
                                "yellow",
                                "annual",
                                "Store 42"
                            ),
                            Plant(
                                ObjectId(),
                                "petunia",
                                "full",
                                "purple",
                                "annual",
                                "Store 47"
                            )
                        )
                    )
                    Log.v("EXAMPLE", "Successfully Successfully inserted the sample data.")
                    expectation.fulfill()
                } else {
                    Log.e(
                        "EXAMPLE",
                        "Failed login: " + it.error.errorMessage
                    )
                }
            }
        }
        expectation.await()
    }

    @Test
    fun instantiateAMongoDBCollectionHandle() {
        val expectation = Expectation()
        activity!!.runOnUiThread {
            val appID: String = YOUR_APP_ID // replace this with your App ID
            val app = App(AppConfiguration.Builder(appID).build())
            val credentials = Credentials.anonymous()
            app.loginAsync(credentials) {
                if (it.isSuccess) {
                    Log.v("EXAMPLE", "Successfully authenticated.")
                    val user = app.currentUser()
                    val mongoClient =
                        user!!.getMongoClient("mongodb-atlas") // service for MongoDB Atlas cluster containing custom user data
                    val mongoDatabase =
                        mongoClient.getDatabase("plant-data-database")
                    val mongoCollection =
                        mongoDatabase.getCollection("plant-data-collection")
                    Log.v("EXAMPLE", "Successfully instantiated the MongoDB collection handle")
                    expectation.fulfill()
                } else {
                    Log.e("EXAMPLE", "Failed login: ${it.error.errorMessage}")
                }
            }
        }
        expectation.await()
    }


    @Test
    fun insertASingleDocument() {
        val expectation = Expectation()
        activity!!.runOnUiThread {
            val appID: String = YOUR_APP_ID // replace this with your App ID
            val app = App(AppConfiguration.Builder(appID).build())
            val credentials = Credentials.anonymous()
            app.loginAsync(credentials) {
                if (it.isSuccess) {
                    Log.v("EXAMPLE", "Successfully authenticated.")
                    val user = app.currentUser()
                    val mongoClient =
                        user!!.getMongoClient("mongodb-atlas") // service for MongoDB Atlas cluster containing custom user data
                    val mongoDatabase =
                        mongoClient.getDatabase("plant-data-database")
                    val mongoCollection =
                        mongoDatabase.getCollection("plant-data-collection")
                    Log.v("EXAMPLE", "Successfully instantiated the MongoDB collection handle")
                    val plant = Plant(
                        ObjectId(),
                        "lily of the valley",
                        "full",
                        "white",
                        "perennial",
                        "Store 47"
                    )
                    mongoCollection?.insertOne(plant)?.getAsync() { task ->
                        if (it.isSuccess) {
                            Log.v(
                                "EXAMPLE",
                                "successfully inserted a document with id: ${task.get().insertedId}"
                            )
                            expectation.fulfill()
                        } else {
                            Log.e("EXAMPLE", "failed to insert documents with: ${task.error}")
                        }
                    }
                } else {
                    Log.e("EXAMPLE", "Failed login: ${it.error.errorMessage}")
                }
            }
        }
        expectation.await()
    }


    @Test
    fun insertMultipleDocuments() {
        val expectation = Expectation()
        activity!!.runOnUiThread {
            val appID = YOUR_APP_ID // replace this with your App ID
            val app = App(AppConfiguration.Builder(appID).build())
            val credentials = Credentials.anonymous()
            app.loginAsync(
                credentials
            ) { it: App.Result<User?> ->
                if (it.isSuccess) {
                    Log.v("EXAMPLE", "Successfully authenticated.")
                    val user = app.currentUser()
                    val mongoClient =
                        user!!.getMongoClient("mongodb-atlas") // service for MongoDB Atlas cluster containing custom user data
                    val mongoDatabase =
                        mongoClient.getDatabase("plant-data-database")
                    val mongoCollection =
                        mongoDatabase.getCollection("plant-data-collection")
                    Log.v(
                        "EXAMPLE",
                        "Successfully instantiated the MongoDB collection handle"
                    )
                    val plants = Arrays.asList(
                        Plant(
                            ObjectId(),
                            "rhubarb",
                            "full",
                            "red",
                            "perennial",
                            "Store 47"
                        ),
                        Plant(
                            ObjectId(),
                            "wisteria lilac",
                            "partial",
                            "purple",
                            "perennial",
                            "Store 42"
                        ),
                        Plant(
                            ObjectId(),
                            "daffodil",
                            "full",
                            "yellow",
                            "perennial",
                            "Store 42"
                        )
                    )
                    mongoCollection.insertMany(plants).getAsync { task ->
                        if (task.isSuccess) {
                            val insertedCount = task.get().insertedIds.size
                            Log.v(
                                "EXAMPLE",
                                "successfully inserted $insertedCount documents into the collection."
                            )
                            expectation.fulfill()
                        } else {
                            Log.e("EXAMPLE", "failed to insert documents with: ${task.error}")
                        }
                    }
                } else {
                    Log.e(
                        "EXAMPLE",
                        "Failed login: " + it.error.errorMessage
                    )
                }
            }
        }
        expectation.await()
    }

    @Test
    fun findASingleDocument() {
        val expectation = Expectation()
        activity!!.runOnUiThread {
            val appID = YOUR_APP_ID // replace this with your App ID
            val app = App(AppConfiguration.Builder(appID).build())
            val credentials = Credentials.anonymous()
            app.loginAsync(
                credentials
            ) { it: App.Result<User?> ->
                if (it.isSuccess) {
                    Log.v("EXAMPLE", "Successfully authenticated.")
                    val user = app.currentUser()
                    val mongoClient =
                        user!!.getMongoClient("mongodb-atlas") // service for MongoDB Atlas cluster containing custom user data
                    val mongoDatabase =
                        mongoClient.getDatabase("plant-data-database")
                    val mongoCollection =
                        mongoDatabase.getCollection("plant-data-collection")
                    Log.v(
                        "EXAMPLE",
                        "Successfully instantiated the MongoDB collection handle"
                    )
                    val queryFilter = Document("type", "perennial")
                    mongoCollection.findOne(queryFilter)
                        .getAsync { task ->
                            if (task.isSuccess) {
                                val result = task.get()
                                Log.v("EXAMPLE", "successfully found a document: $result")
                                expectation.fulfill()
                            } else {
                                Log.e("EXAMPLE", "failed to find document with: ${task.error}")
                            }
                        }
                } else {
                    Log.e("EXAMPLE", "Failed login: " + it.error.errorMessage)
                }
            }
        }
        expectation.await()
    }

    @Test
    fun findMultipleDocuments() {
        val expectation = Expectation()
        activity!!.runOnUiThread {
            val appID = YOUR_APP_ID // replace this with your App ID
            val app = App(AppConfiguration.Builder(appID).build())
            val credentials = Credentials.anonymous()
            app.loginAsync(
                credentials
            ) { it: App.Result<User?> ->
                if (it.isSuccess) {
                    Log.v("EXAMPLE", "Successfully authenticated.")
                    val user = app.currentUser()
                    val mongoClient =
                        user!!.getMongoClient("mongodb-atlas") // service for MongoDB Atlas cluster containing custom user data
                    val mongoDatabase =
                        mongoClient.getDatabase("plant-data-database")
                    val mongoCollection =
                        mongoDatabase.getCollection("plant-data-collection")
                    Log.v(
                        "EXAMPLE",
                        "Successfully instantiated the MongoDB collection handle"
                    )
                    val queryFilter = Document("_partition", "Store 42")
                    val findTask = mongoCollection.find(queryFilter).iterator()
                    findTask.getAsync { task ->
                        if (task.isSuccess) {
                            val results = task.get()
                            Log.v("EXAMPLE", "successfully found all plants for Store 42:")
                            while (results.hasNext()) {
                                Log.v("EXAMPLE", results.next().toString())
                            }
                            expectation.fulfill()
                        } else {
                            Log.e("EXAMPLE", "failed to find documents with: ${task.error}")
                        }
                    }
                } else {
                    Log.e(
                        "EXAMPLE",
                        "Failed login: " + it.error.errorMessage
                    )
                }
            }
        }
        expectation.await()
    }


    @Test
    fun countDocuments() {
        val expectation = Expectation()
        activity!!.runOnUiThread {
            val appID = YOUR_APP_ID // replace this with your App ID
            val app = App(AppConfiguration.Builder(appID).build())
            val credentials = Credentials.anonymous()
            app.loginAsync(
                credentials
            ) { it: App.Result<User?> ->
                if (it.isSuccess) {
                    Log.v("EXAMPLE", "Successfully authenticated.")
                    val user = app.currentUser()
                    val mongoClient =
                        user!!.getMongoClient("mongodb-atlas") // service for MongoDB Atlas cluster containing custom user data
                    val mongoDatabase =
                        mongoClient.getDatabase("plant-data-database")
                    val mongoCollection =
                        mongoDatabase.getCollection("plant-data-collection")
                    Log.v("EXAMPLE", "Successfully instantiated the MongoDB collection handle")
                    mongoCollection.count().getAsync { task ->
                        if (task.isSuccess) {
                            val count = task.get()
                            Log.v("EXAMPLE", "successfully counted, number of documents in the collection: $count")
                            expectation.fulfill()
                        } else {
                            Log.e("EXAMPLE", "failed to count documents with: ${task.error}")
                        }
                    }
                } else {
                    Log.e(
                        "EXAMPLE",
                        "Failed login: " + it.error.errorMessage
                    )
                }
            }
        }
        expectation.await()
    }

    @Test
    fun updateASingleDocument() {
        val expectation = Expectation()
        activity!!.runOnUiThread {
            val appID = YOUR_APP_ID // replace this with your App ID
            val app = App(AppConfiguration.Builder(appID).build())
            val credentials = Credentials.anonymous()
            app.loginAsync(
                credentials
            ) { it: App.Result<User?> ->
                if (it.isSuccess) {
                    Log.v("EXAMPLE", "Successfully authenticated.")
                    val user = app.currentUser()
                    val mongoClient =
                        user!!.getMongoClient("mongodb-atlas") // service for MongoDB Atlas cluster containing custom user data
                    val mongoDatabase =
                        mongoClient.getDatabase("plant-data-database")
                    val mongoCollection =
                        mongoDatabase.getCollection("plant-data-collection")
                    Log.v(
                        "EXAMPLE",
                        "Successfully instantiated the MongoDB collection handle"
                    )
                    val queryFilter = Document("name", "petunia")
                    val updateDocument = Document("sunlight", "partial")
                    mongoCollection.updateOne(queryFilter, updateDocument).getAsync { task ->
                        if (task.isSuccess) {
                            val count = task.get().modifiedCount
                            if (count == 1L) {
                                Log.v("EXAMPLE", "successfully updated a document.")
                            } else {
                                Log.v("EXAMPLE", "did not update a document.")
                            }
                            expectation.fulfill()
                        } else {
                            Log.e("EXAMPLE", "failed to update document with: ${task.error}")
                        }
                    }
                } else {
                    Log.e(
                        "EXAMPLE",
                        "Failed login: " + it.error.errorMessage
                    )
                }
            }
        }
        expectation.await()
    }

    @Test
    fun updateMultipleDocuments() {
        val expectation = Expectation()
        activity!!.runOnUiThread {
            val appID = YOUR_APP_ID // replace this with your App ID
            val app = App(AppConfiguration.Builder(appID).build())
            val credentials = Credentials.anonymous()
            app.loginAsync(
                credentials
            ) { it: App.Result<User?> ->
                if (it.isSuccess) {
                    Log.v("EXAMPLE", "Successfully authenticated.")
                    val user = app.currentUser()
                    val mongoClient =
                        user!!.getMongoClient("mongodb-atlas") // service for MongoDB Atlas cluster containing custom user data
                    val mongoDatabase =
                        mongoClient.getDatabase("plant-data-database")
                    val mongoCollection =
                        mongoDatabase.getCollection("plant-data-collection")
                    Log.v(
                        "EXAMPLE",
                        "Successfully instantiated the MongoDB collection handle"
                    )
                    val queryFilter = Document("_partition", "Store 47")
                    val updateDocument = Document("_partition", "Store 51")
                    mongoCollection.updateMany(queryFilter, updateDocument).getAsync { task ->
                        if (task.isSuccess) {
                            val count = task.get().modifiedCount
                            if (count != 0L) {
                                Log.v("EXAMPLE", "successfully updated $count documents.")
                            } else {
                                Log.v("EXAMPLE", "did not update any documents.")
                            }
                            expectation.fulfill()
                        } else {
                            Log.e("EXAMPLE", "failed to update documents with: ${task.error}")
                        }
                    }
                } else {
                    Log.e(
                        "EXAMPLE",
                        "Failed login: " + it.error.errorMessage
                    )
                }
            }
        }
        expectation.await()
    }

    @Test
    fun upsertASingleDocument() {
        val expectation = Expectation()
        activity!!.runOnUiThread {
            val appID = YOUR_APP_ID // replace this with your App ID
            val app = App(AppConfiguration.Builder(appID).build())
            val credentials = Credentials.anonymous()
            app.loginAsync(
                credentials
            ) { it: App.Result<User?> ->
                if (it.isSuccess) {
                    Log.v("EXAMPLE", "Successfully authenticated.")
                    val user = app.currentUser()
                    val mongoClient =
                        user!!.getMongoClient("mongodb-atlas") // service for MongoDB Atlas cluster containing custom user data
                    val mongoDatabase =
                        mongoClient.getDatabase("plant-data-database")
                    val mongoCollection =
                        mongoDatabase.getCollection("plant-data-collection")
                    Log.v(
                        "EXAMPLE",
                        "Successfully instantiated the MongoDB collection handle"
                    )
                    val queryFilter = Document("sunlight", "full")
                        .append("type", "perennial")
                        .append("color", "green")
                        .append("_partition", "Store 47")
                    val updateDocument = Document("name", "sweet basil")
                    val updateOptions = UpdateOptions().upsert(true)
                    mongoCollection.updateOne(queryFilter, updateDocument, updateOptions)
                        .getAsync { task ->
                            if (task.isSuccess) {
                                if (task.get().upsertedId != null) {
                                    Log.v("EXAMPLE", "successfully upserted a document with id ${task.get().upsertedId}")
                                    expectation.fulfill()
                                } else {
                                    Log.v("EXAMPLE", "successfully updated a document.")
                                }
                            } else {
                                Log.e("EXAMPLE", "failed to update or insert document with: ${task.error}")
                            }
                        }
                } else {
                    Log.e("EXAMPLE", "Failed login: " + it.error.errorMessage)
                }
            }
        }
        expectation.await()
    }

    @Test
    fun deleteASingleDocument() {
        val expectation = Expectation()
        activity!!.runOnUiThread {
            val appID = YOUR_APP_ID // replace this with your App ID
            val app = App(AppConfiguration.Builder(appID).build())
            val credentials = Credentials.anonymous()
            app.loginAsync(
                credentials
            ) { it: App.Result<User?> ->
                if (it.isSuccess) {
                    Log.v("EXAMPLE", "Successfully authenticated.")
                    val user = app.currentUser()
                    val mongoClient =
                        user!!.getMongoClient("mongodb-atlas") // service for MongoDB Atlas cluster containing custom user data
                    val mongoDatabase =
                        mongoClient.getDatabase("plant-data-database")
                    val mongoCollection =
                        mongoDatabase.getCollection("plant-data-collection")
                    Log.v(
                        "EXAMPLE",
                        "Successfully instantiated the MongoDB collection handle"
                    )
                    val queryFilter = Document("color", "green")
                    mongoCollection.deleteOne(queryFilter).getAsync { task ->
                        if (task.isSuccess) {
                            val count = task.get().deletedCount
                            if (count == 1L) {
                                Log.v("EXAMPLE", "successfully deleted a document.")
                            } else {
                                Log.v("EXAMPLE", "did not delete a document.")
                            }
                            expectation.fulfill()
                        } else {
                            Log.e("EXAMPLE", "failed to delete document with: ${task.error}")
                        }
                    }
                } else {
                    Log.e(
                        "EXAMPLE",
                        "Failed login: " + it.error.errorMessage
                    )
                }
            }
        }
        expectation.await()
    }

    @Test
    fun deleteMultipleDocuments() {
        val expectation = Expectation()
        activity!!.runOnUiThread {
            val appID = YOUR_APP_ID // replace this with your App ID
            val app = App(AppConfiguration.Builder(appID).build())
            val credentials = Credentials.anonymous()
            app.loginAsync(
                credentials
            ) { it: App.Result<User?> ->
                if (it.isSuccess) {
                    Log.v("EXAMPLE", "Successfully authenticated.")
                    val user = app.currentUser()
                    val mongoClient =
                        user!!.getMongoClient("mongodb-atlas") // service for MongoDB Atlas cluster containing custom user data
                    val mongoDatabase =
                        mongoClient.getDatabase("plant-data-database")
                    val mongoCollection =
                        mongoDatabase.getCollection("plant-data-collection")
                    Log.v(
                        "EXAMPLE",
                        "Successfully instantiated the MongoDB collection handle"
                    )
                    val queryFilter = Document("sunlight", "full").append("type", "annual")
                    mongoCollection.deleteMany(queryFilter).getAsync { task ->
                        if (task.isSuccess) {
                            val count = task.get().deletedCount
                            if (count != 0L) {
                                Log.v("EXAMPLE", "successfully deleted $count documents.")
                            } else {
                                Log.v("EXAMPLE", "did not delete any documents.")
                            }
                            expectation.fulfill()
                        } else {
                            Log.e("EXAMPLE", "failed to delete documents with: ${task.error}")
                        }
                    }
                } else {
                    Log.e(
                        "EXAMPLE",
                        "Failed login: " + it.error.errorMessage
                    )
                }
            }
        }
        expectation.await()
    }

    @Test
    fun aggregateDocuments() {
        val expectation = Expectation()
        activity!!.runOnUiThread {
            val appID = YOUR_APP_ID // replace this with your App ID
            val app = App(AppConfiguration.Builder(appID).build())
            val credentials = Credentials.anonymous()
            app.loginAsync(
                credentials
            ) { it: App.Result<User?> ->
                if (it.isSuccess) {
                    Log.v("EXAMPLE", "Successfully authenticated.")
                    val user = app.currentUser()
                    val mongoClient =
                        user!!.getMongoClient("mongodb-atlas") // service for MongoDB Atlas cluster containing custom user data
                    val mongoDatabase =
                        mongoClient.getDatabase("plant-data-database")
                    val mongoCollection =
                        mongoDatabase.getCollection("plant-data-collection")
                    Log.v("EXAMPLE", "Successfully instantiated the MongoDB collection handle")
                    val pipeline = listOf(
                        Document(
                            "\$group", Document("_id", "\$type")
                                .append("totalCount", Document("\$sum", 1))
                        )
                    )
                    val aggregationTask =
                        mongoCollection.aggregate(pipeline).iterator()
                    aggregationTask.getAsync { task: App.Result<MongoCursor<Document>> ->
                        if (task.isSuccess) {
                            val results = task.get()
                            Log.d("EXAMPLE", "successfully aggregated the plants by type. Type summary:")
                            while (results.hasNext()) {
                                Log.v("EXAMPLE", results.next().toString())
                            }
                            expectation.fulfill()
                        } else {
                            Log.e("EXAMPLE", "failed to aggregate documents with: ${task.error}")
                        }
                    }
                } else {
                    Log.e(
                        "EXAMPLE",
                        "Failed login: " + it.error.errorMessage
                    )
                }
            }
        }
        expectation.await()
    }

    @Test
    fun watchDocuments() {
        val expectation = Expectation()
        activity!!.runOnUiThread {
            val appID = YOUR_APP_ID // replace this with your App ID
            val app = App(AppConfiguration.Builder(appID).build())
            val credentials = Credentials.anonymous()
            app.loginAsync(
                credentials
            ) { it: App.Result<User?> ->
                if (it.isSuccess) {
                    Log.v("EXAMPLE", "Successfully authenticated.")
                    val user = app.currentUser()
                    val mongoClient =
                        user!!.getMongoClient("mongodb-atlas") // service for MongoDB Atlas cluster containing custom user data
                    val mongoDatabase =
                        mongoClient.getDatabase("plant-data-database")
                    val mongoCollection =
                        mongoDatabase.getCollection("plant-data-collection")
                    Log.v(
                        "EXAMPLE",
                        "Successfully instantiated the MongoDB collection handle"
                    )
                    val watcher = mongoCollection.watchAsync()
                    watcher[{ result ->
                        if (result.isSuccess) {
                            Log.v("EXAMPLE", "Event type: ${result.get().operationType} full document: ${result.get().fullDocument}")
                        } else {
                            Log.e("EXAMPLE", "failed to subscribe to changes in the collection with : ${result.error}")
                        }
                    }]
                    val triffid =
                        Plant(
                            ObjectId(),
                            "triffid",
                            "low",
                            "green",
                            "perennial",
                            "Store 47"
                        )
                    mongoCollection.insertOne(triffid).getAsync { task ->
                        if (task.isSuccess) {
                            val insertedId = task.get().insertedId.asObjectId()
                            Log.v("EXAMPLE", "successfully inserted a document with id $insertedId")
                            expectation.fulfill()
                        } else {
                            Log.e("EXAMPLE", "failed to insert document with: ${task.error}")
                        }
                    }
                } else {
                    Log.e(
                        "EXAMPLE",
                        "Failed login: " + it.error.errorMessage
                    )
                }
            }
        }
        expectation.await()
    }

    @Test
    fun watchDocumentsWithFilter() {
        val expectation = Expectation()
        activity!!.runOnUiThread {
            val appID = YOUR_APP_ID // replace this with your App ID
            val app = App(AppConfiguration.Builder(appID).build())
            val credentials = Credentials.anonymous()
            app.loginAsync(
                credentials
            ) { it: App.Result<User?> ->
                if (it.isSuccess) {
                    Log.v("EXAMPLE", "Successfully authenticated.")
                    val user = app.currentUser()
                    val mongoClient =
                        user!!.getMongoClient("mongodb-atlas") // service for MongoDB Atlas cluster containing custom user data
                    val mongoDatabase =
                        mongoClient.getDatabase("plant-data-database")
                    val mongoCollection =
                        mongoDatabase.getCollection("plant-data-collection")
                    Log.v(
                        "EXAMPLE",
                        "Successfully instantiated the MongoDB collection handle"
                    )
                    val watcher = mongoCollection
                        .watchWithFilterAsync(Document("fullDocument._partition", "Store 42"))
                    watcher[{ result ->
                        if (result.isSuccess) {
                            Log.v("EXAMPLE", "Event type: ${result.get().operationType} full document: ${result.get().fullDocument}")
                        } else {
                            Log.e("EXAMPLE", "failed to subscribe to filtered changes in the collection with : ${result.error}")
                        }
                    }]
                    val plants = listOf(
                        Plant(
                            ObjectId(),
                            "triffid",
                            "low",
                            "green",
                            "perennial",
                            "Store 47"
                        ),
                        Plant(
                            ObjectId(),
                            "venomous tentacula",
                            "low",
                            "brown",
                            "annual",
                            "Store 42"
                        )
                    )
                    mongoCollection.insertMany(plants).getAsync { task ->
                        if (task.isSuccess) {
                            val insertedCount = task.get().insertedIds.size
                            Log.v("EXAMPLE", "successfully inserted $insertedCount documents into the collection.")
                            expectation.fulfill()
                        } else {
                            Log.e("EXAMPLE", "failed to insert documents with: ${task.error}")
                        }
                    }
                } else {
                    Log.e("EXAMPLE", "Failed login: " + it.error.errorMessage)
                }
            }
        }
        expectation.await()
    }
}
