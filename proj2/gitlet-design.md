# Gitlet 设计文档

**姓名： 陈锦照**

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

存储对所有可能使用到的子目录及文件的引用，实现很多对文件的IO方法
便于其他类直接对文件操作。实现对对象（COMMIT,BLOB）的管理，可以
根据需求读写对象。实现对分支指针，头指针的管理

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

10. `public static final File REMOTE_DIR = join(GITLET_DIR, "remotes")`

存储远程仓库的目录

11. `public static final int SHORTENED_LENGTH = 2`

存储commit和blob时的最短目录长度

12. `public static final String DEFAULT_BRANCH = "master"`默认分支名

#### 重要方法

1. `static void setUpPersistence()`

使用init命令时创建Repository所有File类
在工作区创建.gitlet目录

2. `public static Commit getCurrCommit()` 获取HEAD指针对应提交
3. `static <T extends Serializable> T getObjectByID(File dic, String ID, Class<T> cls`
   针对commit和blob对象，由于二级目录结构而建立的根据ID获取对象的方法
4. `static <T extends Serializable> void saveObject(File dic, String ID, T object)`
   将上述两个对象写入对应目录中
5. `static boolean isFileUntracked(String filename)`检验文件是否未被跟踪
6. `static Commit findLatestCommonAncestor(Commit currCommit, Commit otherBranch)`
   获取两个提交的公共最近祖先

### Commit类

该类定义单个提交，包括日志，时间戳，两个父提交的ID和对所有文件的引用
所有实例变量均未使用指针，以免序列化时时间复杂度提高，而是使用对象的ID
因此所有COMMIT都是内容可寻址的

#### 字段

1. `private String message` 提交信息
2. `private Date timestamp` 提交时的时间戳
3. `private String parent1` 第一个父提交的sha-1值
4. `private String parent2` 第二个父提交的sha-1值（该项目最多两个父提交）
5. `private Map<String, String> blobs`
   该提交追踪的文件，使用Blob类的存储的文件名映射Blob类的ID（即sha-1）

### Blob类

该类定义单个文件对象，包括文件名和文件内容（字节数组方式），只有相同的文件名
和完全相同的文件内容才算是相同的BLOB，实现getID方法使其成为内容可寻址对象

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

将文件加入暂存添加区，如果文件不存在于工作区则报错，移除暂存删除区该文件
如果当前提交有该文件相同内容放弃加入暂存区，否则加入暂存区并保存该BLOB（如果它不存在）

2. `static void commit(String message)`

非合并提交，因此只有一个父提交，提交时先复制父提交（时间戳和日志除外），
然后根据暂存区增删改文件，父ID是其父提交的ID。
提交后移动分支指针，清空暂存区。

3. `static void commit(String message, String secondParent) `合并提交，有两个父提交。
4. `static void remove(String filename)`

如果暂存添加区存在该文件，将其移除,如果文件被当前提交跟踪，
将其加入暂存删除区并从工作目录删除（如果用户没有删除）
如果是未跟踪文件（既不在暂存区也不被提交跟踪）会报错。

5. `static void log()`

从当前头提交开始，沿着父提交（第一父提交）依次打印该提交的信息知道最初的提交。

6. `static void global_log()`

打印目前为止拥有的所有提交，无确定顺序。

7. `static void find(String commitMessage)`

依据提交的日志消息查找提交ID，可能有多个提交拥有相同日志消息并查找出多个提交。

8. `static void status()`

获取当前状态，这包括现有分支和当前分支；暂存区状态，修改但未暂存文件以及未跟踪文件。

9. `static void checkout(String ID, String filename)`

将单个文件改成指定提交时的版本，若工作目录不存在会创建该文件，文件在指定提交不存在时报错。

10. `static void checkoutToBranch(String checkoutBranch)`

将工作区所有文件改成指定分支头的版本，不会被覆写的未跟踪文件不会受影响，若会被覆写则报错。
完成后当前分支切换为指定分支，并清空暂存区。

11. `static void createBranch(String newBranch)`

创建一个新的分支，不会切换到该分支。

12. `static void removeBranch(String branchName)`

删除一个已有分支如果它存在的话，不能删除当前分支。

13. `static void reset(String ID)`

将工作区所有文件改成指定提交的版本，并将当前分支头移至该提交，
这也意味着中间的提交被放弃（仍可以通过ID查找）。

14. `static void merge(String checkoutBranch)`

将给定分支与当前分支合并，以下简称当前分支为当前，给定分支为给定，所有文件遵循以下规则：
1.根据当前和给定分支获取最近共同祖先（或分裂点、拆分点等）
2.未跟踪文件若在给定分支存在且与分裂点内容不同将被覆写，这是不被允许的，会报错，
否则未跟踪文件可以保留。如果暂存区不为空（即可提交而未提交），也会报错
3.对其余文件，若给定分支存在且发生修改而当前未修改（都是相对于分裂点），切换到给定分支，并加入暂存。
4.如果当前未修改文件，在给定分支不存在，会执行rm操作
5.仅存在于给定分支的，会在工作区创建该文件并加入暂存
6.当前与给定分支以不同方式修改文件，这包括在拆分点不存在，当前与给定皆存在而内容不同；
在拆分点存在而当前和给定有一个不存在而另一个修改，或都存在内容不一样。
这被称为冲突，会将当前和给定以及冲突标识都写入文件并在终端打印冲突提示
7.其余情况都应什么都不做，保持当前状态。

### RemoteRepository类
表示一个远程仓库类，由于此项目的远程仓库是用本地文件夹模拟的
因此该类拥有对远程仓库与Repository相同的目录结构
也有类似的方法，以便于对远程仓库的文件读写

#### 字段
1. `private final String path`

远程仓库的路径（这里实际上是本地路径）

2. `File GITLET_DIR`  

远程仓库的持久化目录路径引用

3. `File COMMITS_DIR`

提交目录路径引用

4. `File BLOBS_DIR`

文件对象目录路径引用

5. `File HEAD`

头指针

6. `File BRANCHES_DIR`

分支目录路径引用

### RemoteCommands类

#### 字段

没有字段，该类只有静态方法

#### 重要方法

1. `static void addRemote(String remoteName, String path)`

将一个新的远程仓库加入本地记录，由于这是模拟远程
所以传入的路径仍是本地路径，需要包含.gitlet

2. `static void removeRemote(String remoteName)`

将一个远程从本地记录移除

3. `static void push(String remoteName, String remoteBranchName)`

根据给定的远程分支头提交，从本地当前头提交回溯查找历史记录是否有该给定提交，
若未找到则推送失败，否则把从查找位置到当前头提交的所有提交（和文件）全部复制到远程分支头
并更新远程分支头。

4. `static void fetch(String remoteName, String remoteBranchName)`

从远程分支头开始直到最初提交，获取所有本地不存在的提交（和文件）加入本地，
并创建新本地名为【远程名】/【远程分支】的分支，该操作与push是类似的，只是方向相反。

5. `static void pull(String remoteName, String remoteBranchName)`

fetch和merge命令的结合。先用fetch获取提交和文件，以及一个新分支
再将这个新分支合并到当前本地分支

### Utils类

此类为工具类（作者：P. N. Hilfinger）提供实用文件方法，
包括文件读写，序列化与反序列化，文件删除，目录文件迭代等

#### 字段

都是实用方法

### Dumpable接口和DumpObj类

用于查看文件序列化内容的调试类（作者：P. N. Hilfinger）

#### 字段

无，只是一个可执行程序

### GitletException异常类

用于检测异常（作者：P. N. Hilfinger）

## 算法设计

1. 使用安全哈希函数sha-1（作者：P. N. Hilfinger）生成commit和blob对象的唯一40位ID
   对于blob对象，文件名和文件内容都相同的具有相同ID；对于commit对象，
   字段中未使用任何指针，而用父提交的ID代替指针，用哈希表（文件名-blobID键值对）代替对文件的指针
   以此降低计算对应指针带来的额外时间开销。因此，对于在给定时间和给定日志的提交，在任何计算机上该
   提交始终拥有相同的ID，以此来实现commit和blob对象的内容可寻址性

2. 对于commit和blob对象的存储方式，采用分类在不同子目录，单文件以ID作为文件名，序列化内容为文件内容
   的方式。由于ID是40位，将其按照前两位字符再划分出一个子目录，即前两位相同的提交放在同一个子目录，防止父目录中
   文件过多。这样在文件数较少时会略微降低查找速度，而在文件数较多时能显著提高查找速度

3. 合并操作中的一个重要方法：查找两提交的公共最近祖先
   首先明确，每个提交通过1-2个父ID查找父提交，这时整个提交结构变为有向无环图

要找最近公共祖先，采用BFS算法，维护一个单队列和ID映射来源的哈希表，
来源用1和2分别表示来自提交1和提交2，来源是1的提交父提交也是来源1，同理对来源2，初始将两提交加入队列和哈希表
只要队列不为空，获取队头两个父ID并出队，对这两个父ID，如果已经在哈希表
且与当前来源不同，那个该父ID对应提交就是最近公共祖先得到输出，否则将非空父ID入队并加入哈希表
若队列为空还没有找到结果返回null（正常情况下不会发生）

时间复杂度在最坏情况下为$O(N),n为提交数$

4. 在远程操作中，push和fetch都采用BFS算法，由当前提交沿父提交距离当前提交层数逐层搜索

## 持久化设计

目录结构如下：

```aiignore
CWD                             <==== 工作目录
└── .gitlet                     <==== 存放所有可持续化数据
    ├── head                    <==== 存放当前分支
    └── branches                <==== 存放所有现存分支
        ├── master                
        ├── branch2
        └── ...
    └── remotes                <==== 存放远程仓库路径
        ├── remote1              
        ├── remote2
        └── ...
    └── stadgedArea              <==== 暂存区 
        └── addition             <==== 待添加区 
            ├── file1              
            ├── file2
            └── ...
        └── removal              <==== 待删除区 
            ├── file1            
            ├── file2
            └── ... 
    └── commits                  <==== 提交对象区
        └── 1a 
            ├── 7c1e72390...              
            └── ...
        └── 3b 
            ├── 890ac7889...              
            └── ...  
        └── ... 
    └── blobs                   <==== 文件对象区      
        └── 3d 
            ├── 8948ecaaa...               
            └── ...
        └── 77 
            ├── 96adaec78...              
            └── ...  
        └── ...           
```

Repository类将会开启持久性，这包括创建.gitlet及大部分子目录和文件。

这其中，head文件存储当前分支名，branches目录存放所有分支文件，
这些分支文件内容是对应提交的ID；stagedArea目录下设addition和removal子目录，
addition存放文件内容是对应Blob的ID, removal目录仅存放空文件以表示哪些文件要删除
remotes目录存储远程名及其路径。

commits目录存放所有提交，这些提交文件名是其ID，且按照前两个字符分类子目录，
文件内容是commit的序列化内容。仅通过commit命令和merge命令向该目录中添加文件
永远不会删除文件。

blobs目录与commits结构完全相同，文件名是blob的ID，内容是blob序列化内容
仅通过add命令向该目录添加文件，永远不会删除文件。

## 其他

### 使用集成测试

使用项目框架提供的tester.py脚本进行集成测试，自己编写大量测试.in文件
对涉及命令进行测试。

### 使用远程JVM调试

使用项目框架提供的runner.py脚本对单个测试文件进行调试，启用调试脚本建立连接后，
使用idea中的远程JVM进行调试。


