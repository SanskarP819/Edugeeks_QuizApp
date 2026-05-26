package com.example.edugeeksquiz.data.remote

import com.example.edugeeksquiz.domain.model.Difficulty
import com.example.edugeeksquiz.domain.model.Question
import com.example.edugeeksquiz.domain.model.QuizCategory


/**
 * Mock data source — replace with real API / Firebase calls in production.
 * Also serves as offline fallback.
 */
object MockQuestionSource {

    val categories = listOf(
        QuizCategory("android", "Android Dev", "📱", "Kotlin, Jetpack, Compose", 10, 0xFF4CAF50),
        QuizCategory("kotlin", "Kotlin", "🎯", "Language features & idioms", 10, 0xFF7C4DFF),
        QuizCategory("dsa", "Data Structures", "🧩", "Arrays, Trees, Graphs", 10, 0xFF2196F3),
        QuizCategory("design", "System Design", "🏗️", "Architecture & scalability", 10, 0xFFFF5722),
        QuizCategory("ai", "AI / ML", "🤖", "Concepts & applications", 10, 0xFFE91E63),
    )

    private val androidQuestions = listOf(
        Question("a1", "What is Jetpack Compose?", listOf("A XML layout framework","A declarative UI toolkit","An imperative UI library","A database library"), 1, "Jetpack Compose is Android's modern declarative UI toolkit.", "android", Difficulty.EASY, 30),
        Question("a2", "Which keyword is used to mark a Composable function?", listOf("@Compose","@UI","@Composable","@View"), 2, "@Composable marks a function as a Composable.", "android", Difficulty.EASY, 20),
        Question("a3", "What does `remember` do in Compose?", listOf("Saves state to disk","Retains state across recompositions","Clears state on recomposition","Shares state between activities"), 1, "remember retains state across recompositions within the same composition.", "android", Difficulty.MEDIUM, 30),
        Question("a4", "Which architecture component observes LiveData?", listOf("ViewModel","Repository","Observer","Activity only"), 2, "Observer pattern is used to watch LiveData changes.", "android", Difficulty.MEDIUM, 30),
        Question("a5", "What is the purpose of ViewModel in MVVM?", listOf("Manage database","Hold UI-related data lifecycle-consciously","Draw UI","Handle network requests directly"), 1, "ViewModel holds UI state and survives configuration changes.", "android", Difficulty.EASY, 25),
        Question("a6", "Which Compose modifier clips a Composable to a circle?", listOf("clip(RectangleShape)","clip(CircleShape)","shape(Circle)","border(circle=true)"), 1, "Modifier.clip(CircleShape) clips the composable to a circle.", "android", Difficulty.MEDIUM, 25),
        Question("a7", "What is recomposition in Jetpack Compose?", listOf("App restart","Screen rotation","Re-running Composable functions when state changes","Creating a new Activity"), 2, "Recomposition is Compose's way of re-rendering UI when state changes.", "android", Difficulty.MEDIUM, 30),
        Question("a8", "Which class manages navigation in Compose?", listOf("FragmentManager","NavController","Router","ActivityManager"), 1, "NavController manages navigation between Composables.", "android", Difficulty.EASY, 20),
        Question("a9", "What is `LaunchedEffect` used for?", listOf("Launch a new Activity","Run a coroutine in the composition scope","Add animations","Create side effects in ViewModel"), 1, "LaunchedEffect runs a coroutine tied to the composition lifecycle.", "android", Difficulty.HARD, 35),
        Question("a10", "Room is a wrapper over which database?", listOf("MongoDB","SQLite","PostgreSQL","Firebase"), 1, "Room provides an abstraction layer over SQLite.", "android", Difficulty.EASY, 20),
    )

    private val kotlinQuestions = listOf(
        Question("k1", "What is a data class in Kotlin?", listOf("A class that inherits data","A class auto-generating equals/hashCode/copy","An abstract class","An interface"), 1, "Data classes auto-generate equals, hashCode, copy, and toString.", "kotlin", Difficulty.EASY, 20),
        Question("k2", "What does the `?` operator signify in Kotlin?", listOf("Elvis operator","Safe call","Nullable type","Not null assertion"), 2, "The ? suffix marks a type as nullable.", "kotlin", Difficulty.EASY, 20),
        Question("k3", "What is a sealed class?", listOf("A final class","A class with restricted subclasses in a module","An abstract class","A data class"), 1, "Sealed classes restrict the class hierarchy to defined subclasses.", "kotlin", Difficulty.MEDIUM, 30),
        Question("k4", "Which keyword starts a coroutine?", listOf("async","thread","launch","coroutine"), 2, "launch starts a new coroutine.", "kotlin", Difficulty.EASY, 20),
        Question("k5", "What is the Elvis operator in Kotlin?", listOf("!!","?.","?:","::"), 2, "?: returns the right-hand value when the left is null.", "kotlin", Difficulty.EASY, 15),
        Question("k6", "What is `inline` function used for?", listOf("Improve performance by inlining lambdas","Mark function as final","Define extension function","Create anonymous function"), 0, "Inline functions reduce overhead by copying lambda code at call sites.", "kotlin", Difficulty.HARD, 35),
        Question("k7", "What does `by lazy` do?", listOf("Initializes immediately","Initializes on first access","Prevents initialization","Creates a lateinit property"), 1, "by lazy initializes the property on first access and caches the result.", "kotlin", Difficulty.MEDIUM, 25),
        Question("k8", "Which is NOT a scope function in Kotlin?", listOf("let","run","apply","create"), 3, "create is not a scope function. let, run, apply, also, with are.", "kotlin", Difficulty.MEDIUM, 30),
        Question("k9", "What is a Flow in Kotlin?", listOf("A list","A cold asynchronous data stream","A synchronous sequence","A coroutine scope"), 1, "Flow is a cold asynchronous data stream built on coroutines.", "kotlin", Difficulty.MEDIUM, 30),
        Question("k10", "What does `companion object` provide?", listOf("Instance methods","Static-like members on a class","Abstract methods","Extension functions"), 1, "companion object provides static-like members accessible on the class.", "kotlin", Difficulty.MEDIUM, 25),
    )

    private val dsaQuestions = listOf(
        Question("d1", "What is the time complexity of binary search?", listOf("O(n)","O(n²)","O(log n)","O(1)"), 2, "Binary search halves the search space each step giving O(log n).", "dsa", Difficulty.EASY, 25),
        Question("d2", "Which data structure uses LIFO?", listOf("Queue","Heap","Stack","Linked List"), 2, "Stack follows Last In First Out (LIFO) order.", "dsa", Difficulty.EASY, 15),
        Question("d3", "What is the worst-case time complexity of QuickSort?", listOf("O(n log n)","O(n²)","O(n)","O(log n)"), 1, "QuickSort worst case O(n²) when pivot is always the smallest/largest.", "dsa", Difficulty.MEDIUM, 30),
        Question("d4", "A complete binary tree with n nodes has height:", listOf("n","n/2","log₂(n)","n²"), 2, "Height of complete binary tree is floor(log₂ n).", "dsa", Difficulty.MEDIUM, 30),
        Question("d5", "Which traversal of BST gives sorted order?", listOf("Preorder","Postorder","Inorder","Level order"), 2, "Inorder traversal of BST yields elements in ascending order.", "dsa", Difficulty.EASY, 20),
        Question("d6", "What is the space complexity of merge sort?", listOf("O(1)","O(log n)","O(n)","O(n²)"), 2, "Merge sort needs O(n) auxiliary space for the merge step.", "dsa", Difficulty.MEDIUM, 25),
        Question("d7", "Which algorithm finds shortest path in unweighted graph?", listOf("DFS","BFS","Dijkstra","Bellman-Ford"), 1, "BFS finds shortest path in terms of number of edges in unweighted graphs.", "dsa", Difficulty.MEDIUM, 25),
        Question("d8", "A hash table has O(1) average case for:", listOf("Sorting","Traversal","Insert/Search/Delete","Finding max"), 2, "Hash tables provide O(1) average case for insert, search, and delete.", "dsa", Difficulty.EASY, 20),
        Question("d9", "What is dynamic programming?", listOf("Parallel programming","Breaking problems into subproblems caching results","Real-time programming","Memory management"), 1, "DP solves problems by breaking into overlapping subproblems and memoizing.", "dsa", Difficulty.MEDIUM, 30),
        Question("d10", "Dijkstra's algorithm fails with:", listOf("Directed graphs","Weighted graphs","Negative weight edges","Dense graphs"), 2, "Dijkstra's algorithm doesn't work correctly with negative weight edges.", "dsa", Difficulty.HARD, 35),
    )

    private val designQuestions = listOf(
        Question("s1", "What does REST stand for?", listOf("Remote Execution Service Transfer","Representational State Transfer","Real-time State Transfer","Remote State Transaction"), 1, "REST = Representational State Transfer, an architectural style for APIs.", "design", Difficulty.EASY, 20),
        Question("s2", "Which database is best for unstructured data?", listOf("MySQL","PostgreSQL","MongoDB","SQLite"), 2, "MongoDB is a NoSQL database well-suited for unstructured/flexible data.", "design", Difficulty.EASY, 20),
        Question("s3", "What is a CDN used for?", listOf("Database replication","Delivering content from geographically close servers","Authentication","Load balancing databases"), 1, "CDN caches content at edge servers closer to users for faster delivery.", "design", Difficulty.EASY, 25),
        Question("s4", "What does horizontal scaling mean?", listOf("Adding more RAM to a server","Adding more servers to distribute load","Upgrading CPU","Increasing disk space"), 1, "Horizontal scaling adds more machines to handle increased load.", "design", Difficulty.EASY, 20),
        Question("s5", "What is eventual consistency?", listOf("Immediate sync across all nodes","All nodes will be consistent given enough time","No consistency guarantee","ACID compliance"), 1, "Eventual consistency guarantees replicas converge over time without immediate sync.", "design", Difficulty.HARD, 35),
        Question("s6", "CAP theorem states you can have at most:", listOf("All three: Consistency, Availability, Partition tolerance","Two of: Consistency, Availability, Partition tolerance","One of the three","None in a distributed system"), 1, "CAP theorem: choose 2 of Consistency, Availability, Partition tolerance.", "design", Difficulty.HARD, 35),
        Question("s7", "What is a message queue used for?", listOf("Store user messages","Decouple services and enable async communication","Cache database queries","Store session data"), 1, "Message queues decouple producers and consumers enabling async processing.", "design", Difficulty.MEDIUM, 30),
        Question("s8", "Which HTTP method is idempotent?", listOf("POST","PATCH","PUT","None"), 2, "PUT is idempotent — multiple identical requests have the same effect.", "design", Difficulty.MEDIUM, 25),
        Question("s9", "What is the purpose of an API Gateway?", listOf("Direct database access","Single entry point managing routing, auth, rate limiting","Store API responses","Monitor server health"), 1, "API Gateway acts as a reverse proxy handling routing, auth, and rate limiting.", "design", Difficulty.MEDIUM, 30),
        Question("s10", "What does ACID stand for in databases?", listOf("Atomicity, Consistency, Isolation, Durability","Async, Concurrent, Indexed, Distributed","Atomicity, Concurrency, Isolation, Distribution","None of the above"), 0, "ACID: Atomicity, Consistency, Isolation, Durability — transaction properties.", "design", Difficulty.MEDIUM, 25),
    )

    private val aiQuestions = listOf(
        Question("ai1", "What is overfitting in ML?", listOf("Model performs well on all data","Model memorizes training data but fails on new data","Model uses too little data","Model trains too slowly"), 1, "Overfitting: model learns noise in training data, generalizes poorly.", "ai", Difficulty.MEDIUM, 25),
        Question("ai2", "What does a neural network's activation function do?", listOf("Stores weights","Introduces non-linearity","Normalizes inputs","Backpropagates gradients"), 1, "Activation functions introduce non-linearity allowing networks to learn complex patterns.", "ai", Difficulty.MEDIUM, 30),
        Question("ai3", "What is the vanishing gradient problem?", listOf("GPU memory issue","Gradients become extremely small during backpropagation","Too many parameters","Network not converging due to large learning rate"), 1, "Vanishing gradients cause very small updates in early layers slowing training.", "ai", Difficulty.HARD, 35),
        Question("ai4", "What does CNN stand for?", listOf("Computed Neural Network","Convolutional Neural Network","Cyclic Neural Node","Connected Node Network"), 1, "CNN = Convolutional Neural Network, used primarily for image tasks.", "ai", Difficulty.EASY, 15),
        Question("ai5", "What is transfer learning?", listOf("Moving data between GPUs","Using a pre-trained model as a starting point","Transferring model weights to CPU","Parallel training"), 1, "Transfer learning reuses knowledge from a pre-trained model for a new task.", "ai", Difficulty.MEDIUM, 25),
        Question("ai6", "What is a transformer in NLP?", listOf("A type of RNN","Architecture using self-attention mechanisms","A feature extraction tool","An encoder-only model"), 1, "Transformers use self-attention to model relationships in sequences.", "ai", Difficulty.HARD, 35),
        Question("ai7", "Supervised learning requires:", listOf("Unlabeled data","Labeled training data","Reward signals","No data"), 1, "Supervised learning trains on labeled input-output pairs.", "ai", Difficulty.EASY, 20),
        Question("ai8", "What is the purpose of dropout in neural networks?", listOf("Speed up training","Prevent overfitting by randomly deactivating neurons","Increase model size","Normalize activations"), 1, "Dropout randomly deactivates neurons during training to reduce overfitting.", "ai", Difficulty.MEDIUM, 30),
        Question("ai9", "What metric evaluates a binary classifier balancing precision and recall?", listOf("Accuracy","MSE","F1 Score","AUC"), 2, "F1 Score is the harmonic mean of precision and recall.", "ai", Difficulty.MEDIUM, 30),
        Question("ai10", "What does GPT stand for?", listOf("General Purpose Transformer","Generative Pre-trained Transformer","Gradient Processing Tool","Graph Processing Technique"), 1, "GPT = Generative Pre-trained Transformer, a large language model architecture.", "ai", Difficulty.EASY, 15),
    )

    fun getQuestionsForCategory(category: String): List<Question> = when (category) {
        "android" -> androidQuestions
        "kotlin" -> kotlinQuestions
        "dsa" -> dsaQuestions
        "design" -> designQuestions
        "ai" -> aiQuestions
        else -> androidQuestions
    }.shuffled()
}
