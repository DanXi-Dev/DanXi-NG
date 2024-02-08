# 风格指南

本文提出了任何贡献者在参加开发时必须遵守的规则。

## 开发流程相关

### **要**使用规范的 Commit 格式。

请参考 <https://ruby-china.org/topics/15737> 的详细 Commit Message 风格指导。

```
# 好👍
feat: create bottom menu for comment views (close #12)
update: some translation texts
remove: useless codes

# 坏👎
new menu
fix
update
clean
```

## 应用规范

### **要**使用依赖注入（Dependency Injection）。

旦夕引入了 [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) 作为 ViewModel 的依赖管理工具，
因为 Hilt 可以把依赖作用域限制在 `ViewModel` 或者 `Application` 的级别，不同的 `ViewModel` 可以共享不同的全局变量。
同时 Hilt 是编译时注入，不会影响运行时性能。

#### 好👍

```kotlin
// 使用 Annotation 设置依赖
@Singleton
class MyGlobalClass @Inject constructor() {
    // ...
}

// 在其他地方注入依赖
@ViewModelScoped
class MyViewModelScopedClass @Inject constructor(
    myGlobalClass: MyGlobalClass
) {
    // ...
}

// 在 `ViewModel` 中注入依赖
@HiltViewModel
class MyViewModel @inject constructor(
    myGlobalClass: MyGlobalClass
) : ViewModel() {
    // 获取 Context
    @ApplicationContext lateinit var context: Context
    // ...
}
```

### **要**尽可能使用紧凑的编写形式。

#### 好👍

```kotlin
fun foo(): String = "result"
```

#### 坏👎

```kotlin
fun foo(): String {
    val res = "result"
    return res
}
```

> **例外**
>
> 除非必须有这么做的理由。例如，紧凑之后会使得代码层级过多，变得难以阅读。或者需要方法保持开放，随时进行修改或插桩测试。

### **要**在 API 方法中返回非空类型的变量。

#### 好👍

```kotlin
suspend fun getDataFromNetwork(): String {
    val result: String? = innerCall()
    // 会在 result == null 时抛出异常
    return requireNotNull(result) { "Get null data from network." }
}
```

#### 坏👎

```kotlin
suspend fun getDataFromNetwork(): String? {
    val result: String? = innerCall()
    return result
}
```

> **例外**
>
> 除非对于 API 而言，返回 null 本身就是一种可用的信息。例如，获取发帖列表的 API 可能返回 null，表示没有发帖。

### **要**在网络请求层使用 suspend 方法。

#### 好👍

```kotlin
suspend fun getDataFromNetwork() = withContext(Dispatchers.IO){
    networkRequest()
}
```

#### 坏👎

```kotlin
fun getDataFromNetwork(): String {
    return networkRequest()
}
```

### **不要**在网络请求层过多地「消化」掉异常。

#### 好👍

```kotlin
suspend fun getDataFromNetwork(): String {
    // 一大堆会产生各种不可恢复的异常的请求代码……
    // 另一大堆会产生各种不可恢复的异常的响应解析代码……
    return result
}
```

#### 好👍

```kotlin
suspend fun getDataFromNetwork(): String {
    try {
        // 一大堆会产生各种不可恢复的异常的请求代码……
    } catch (e: HTTPException) {
        throw ExactTypeException()
    }
    try {
        // 另一大堆会产生各种不可恢复的异常的响应解析代码……
    } catch (e: FormatException) {
        throw AnotherExactTypeException()
    }
    return result
}
```

#### 坏👎

```kotlin
suspend fun getDataFromNetwork(): String {
    try {
        // 一大堆会产生各种不可恢复的异常的请求代码……
    } catch (e: Exception) {
        println(e)
    }
    try {
        // 另一大堆会产生各种不可恢复的异常的响应解析代码……
    } catch (e: Exception) {
        println(e)
    }
    return result
}
```

> **例外**
>
> 除非该异常是**可恢复的**。可恢复的定义是：即便不执行本身的主要逻辑第二次（例如：重新请求网络），也可以返回正确的结果。

### **要**在控制层（ `ViewModel` 层）处理来自数据请求层的异常。

#### 好👍

```kotlin
fun clickRefreshData() {
    showProgressBar()
    viewModelScope.launch {
        try {
            // 一大堆会产生各种不可恢复的异常的请求代码……
        } catch (e: Exception) {
            showErrorTips(e)
        }
    }
}
```

#### 坏👎

```kotlin
fun clickRefreshData() {
    showProgressBar()
    viewModelScope.launch {
        // 一大堆会产生各种不可恢复的异常的请求代码……
    }
}
```

### **要**使用 `MutableStateFlow` 实现数据交换。

#### 好👍

```kotlin
// ViewModel 中
data class MyUiState(
    val clicked: Boolean = false
)

private val _uiState = MutableStateFlow(MyUiState())
val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()

fun onClick() {
    // 更新状态
    _uiState.update { it.copy(clicked = true) }
}

// --------------------------------

// Compose 中
@Composable
fun MyComposable(viewModel: MyViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Button(onClick = { viewModel.onClick() }) {
        Text("Click me")
    }
}
```

> **补充**
>
> 对于较为复杂的页面状态，可以根据各 State 更新的频繁与否，创建多个 `UiState`。避免由于某个变量频繁变更，而不得不在每一个变更时复制所有状态。

### **不推荐**在视图层以外调用界面方法。

视图层（如 `Compose`）理应承担所有视图任务，如显示动画、显示对话框、显示上下文菜单、跳转到新页面等等。其他层，尤其是 `ViewModel`，不应当执行任何有关方法。

> **例外**
>
> 在自定义的视图-控制器一体类（如 `Feature`）中，`navigate` 是可容忍的。

## 应用惯例

### **要**针对不同账户系统使用不同的数据类型。

旦夕是面向多类型账户系统的，`PersonInfo` 是只针对 UIS 账号密码的数据类型。

```
# 好👍
登录复旦 UIS 账户
使用树洞账号登录旦课
树洞登录
无法连接至复旦 UIS 服务器

# 坏👎
登录账户
使用旦夕账号登录旦课
树洞登录
无法连接至服务器
```