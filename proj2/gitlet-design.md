# Gitlet 设计文档

**姓名： **:

## 类和数据结构

### Main类
程序的主入口，对所有输入的命令检查其格式，格式正确调用
Commands类和Repository类的方法，自身不会实现任何命令，若
格式错误会打印错误信息并退出程序

#### 字段

没有字段，该类只是验证参数并调用方法

#### 重要方法
`static void printError(String message)`
该类或其他类在处理命令出错时会调用该方法，因此设为包级限制访问
该方法会根据情况打印ErrorMessage枚举类中的信息，然后立即终止程序



### Repository类
管理.gitlet目录结构， 以便其他类对.gitlet目录进行文件操作
自身也可以对该目录操作

#### 字段

1. `public static final File CWD = new File(System.getProperty("user.dir"))`
当前工作区，.gitlet的父目录
2. `public static final File GITLET_DIR = join(CWD, ".gitlet")`
核心目录，存放所有历史数据，对用户一般不可见
3. `public static final File BLOBS_DIR = join(GITLET_DIR, "blobs")`
文件存储区，存放所有通过add操作加入的文件，是版本更迭时
改变工作区文件的来源
4. `public static final File STAGED_AREA = join(GITLET_DIR, "stagedArea")`
暂存区，通过add和rm操作的文件会进入该区
5. `public static final File ADDITION = join(STAGED_AREA, "addition")`
通过add操作的文件进入该区
6. `public static final File REMOVAL = join(STAGED_AREA, "removal")`
通过rm操作进入该区
7. `public static final File COMMITS_DIR = join(GITLET_DIR, "commits")`
核心区，存放所有历史提交数据，在版本更迭时将工作区
所有文件变为与相应提交相同
8. `public static final File HEAD = join(GITLET_DIR, "head")`
头指针文件，头指针指向当前所处提交
9. `public static final File BRANCHES_DIR = join(GITLET_DIR, "branches")`
分支目录，存放所有创建的分支

#### 重要方法
1. `static void setUpPersistence()` 使用init命令时创建Repository所有File类
在工作区创建.gitlet目录
2. `public static Commit getCurrCommit()` 获取HEAD指针对应提交

### Commit类
此类定义单个提交，所有Commit类在执行commit命令时会被序列化
并加入到COMMITS_DIR中，对序列化值使用sha-1得出唯一标识
#### 字段
1. `private String message` 提交信息
2. `private Date timestamp` 提交时的时间戳
3. `private String parent1` 第一个父提交的sha-1值
4. `private String parent2` 第二个父提交的sha-1值（该项目最多两个父提交）
5. `private Map<String, String> blobs`
该提交追踪的文件，使用Blob类的存储的文件名映射Blob类的ID（即sha-1）

### Blob类
此类是对单个文件的引用，文件名或文件内容均相等才视为相同Blob类
而BLOB_DIR中不会出现两个相同的Blob，即ID都不同

### 字段

1. `private String filename` 引用文件的文件名
2. `private byte[] content` 引用文件的内容，以字节数组表示

## ErrorMessage枚举
枚举所有可能出现的错误消息以便调用
### 字段

1. `private final String message` 错误消息
### Commands类
处理所有命令的方法类

#### 字段
无，该类只有静态方法

#### 重要方法

1. `static void add(String filename)`
对应git的add命令，将工作区的文件加入到暂存区，如果未找到文件报错，
如果该文件与当前提交跟踪的该文件内容相同取消暂存，该项目仅支持添加单个文件
2. 
## Algorithms

## Persistence

