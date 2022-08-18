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
### **要**使用 ViewBinding。
ViewBinding 是新版本 Android 开发支持库提供的自动生成类，其名称是布局文件名的驼峰转写。
#### 好👍
```kotlin
private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(LayoutInflater.from(this)) }
// 在稍后的地方设置布局
setContentView(binding.root) 
```

#### 坏👎
```kotlin
// 直接设置布局 ID
setContentView(R.layout.activity_main) 
```
### **要**使用服务定位器（Service Locator）模式。
旦夕引入了 [Koin](https://insert-koin.io/) 作为全局变量的管理工具，而非 Android 官方提供的 Dagger 或其他依赖注入（Dependency Injection）库，这是因为后者的灵活性很差，仅允许对 Activity、ViewModel 和 Fragment 注入依赖。
#### 好👍
```kotlin
// 在 GlobalState.kt 设置全局变量的生成器
val appModule = module {
    ...
    single { MyGlobalVariable() }
} 
// 在导入 KoinComponent 其他地方使用
private val globalValue: MyGlobalVariable by inject() 
```
#### 不是很坏，但是不推荐😕
```kotlin
// 在任何地方都用 KoinComponent 导入全局变量，无论它是哪一层的控件，甚至根本不是控件。
class MySimpleToolClass : KoinComponent {
    private val globalValue: MyGlobalVariable by inject()
}
// ❗在这种时候，应优先让类接受参数，而不是自己硬编码全局变量：
class MySimpleToolClass(private val globalValue: MyGlobalVariable) {
}
```
#### 坏👎
```kotlin
// 直接创建全局静态变量
companion object {
    var globalValue = MyGlobalValue()
}
```
#### 坏👎
```kotlin
// 储存 Context（如 Application、Activity、Service 等都是 Context）
companion object {
    var globalValue: Acitivty = getActivity()
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
    return requireNotNull(result){"Get null data from network."}
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
    try{
        // 一大堆会产生各种不可恢复的异常的请求代码……
    } catch (e: HTTPException) {
        throw ExactTypeException()
    }
    try{
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
    try{
        // 一大堆会产生各种不可恢复的异常的请求代码……
    } catch (e: Exception) {
        println(e)
    }
    try{
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

### **要**在控制层处理来自数据请求层的异常。
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
private val _uiState = MutableStateFlow(MyUiState())
val uiState: StateFlow<MyUiState> = _uiState.asStateFlow()

fun onClick(){
    // 更新状态
    _uiState.update { it.copy(clicked = true) }
}

// --------------------------------

// Fragment/Activity 中
data class MyUiState(
    val clicked: Boolean = false
)

// 或者对于 Activity，使用 onCreate() 回调
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.apply {
                watch(this@repeatOnLifecycle, { it.clicked }) {
                    // clicked 变更的事件发生
                }
            }
        }
    }
}
```
> **补充**
>
> 对于较为复杂的页面状态，可以根据各 State 更新的频繁与否，创建多个 `UiState`。避免由于某个变量频繁变更，而不得不在每一个变更时复制所有状态。

### **不推荐**在视图层以外调用界面方法。
视图层（如 `Activity` 和 `Fragment`）理应承担所有视图任务，如显示动画、显示对话框、显示上下文菜单、跳转到新页面等等。其他层，尤其是 `ViewModel`，不应当执行任何有关方法。

> **例外**
>
> 在自定义的视图-控制器一体类（如 `Feature`）中，`startActivity` 是可容忍的。

## 应用惯例
### **不要**过多地在通用类中使用只针对复旦 UIS 账号的实现。
旦夕是面向多类型账户系统的，不应想当然地把 `PersonInfo` 视作只包含 UIS 账号密码的数据类型，也不要在设置中过多出现「复旦」有关的字样。
```
# 好👍
登录账户
使用旦夕账号登录旦课
树洞登录
无法连接至服务器

# 坏👎
登录 UIS 账户
使用复旦树洞账号登录旦课
复旦树洞登录
无法连接至复旦服务器
```