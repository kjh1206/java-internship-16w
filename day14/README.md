# 学生管理系统（控制台版）

> Week 2 产出：接口 + 实现分层、自定义异常、文件持久化。核心代码 300 行以内。

## 运行

```bash
# 在 day14 目录下（students.csv 会生成在当前目录）
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

IDEA 里直接跑 `Main` 即可，注意 **Working directory 要设成 `day14`**，否则 `students.csv` 会落到别处。

## 功能

| 菜单 | 功能 | 说明 |
|---|---|---|
| 1 | 添加 | 学号重复会被拒绝 |
| 2 | 列表 | 表格打印全部学生 |
| 3 | 按学号查 | 查不到给提示，不抛异常 |
| 4 | 修改 | 每项回车即跳过，只改填了的字段 |
| 5 | 删除 | 返回是否真的删掉了 |
| 6 | 退出 | 数据已实时落盘，直接退即可 |

## 分层

```
StudentRepository（接口）
   ├── findAll() / findById(id) / add(s) / update(s) / delete(id)
   └── FileStudentRepository（实现：LinkedHashMap + CSV 文件）
Student（模型：字段校验 + toCsv/fromCsv）
Level（枚举：本科/研究生，带 code + label + ofCode）
InvalidFieldException（自定义受检异常：field + value + message）
Main（菜单 + 输入校验，唯一持有 Scanner 的地方）
```

## 数据格式（students.csv）

```
学号,姓名,年龄,层次code,成绩
1001,张三,20,1,88.5
1002,李四,22,2,91.0
```

- 逗号分隔，**姓名不允许包含逗号**（会在 `setName` 里拦住，免得破坏格式）
- 每次增删改后整表重写（数据量小，简单可靠；大了再改成追加 + 定期压缩）
- 启动时读取，坏行会跳过并打印 `[警告]`，不会因为一行脏数据整个程序起不来

## 校验规则（全部有实测）

| 字段 | 规则 | 触发异常 |
|---|---|---|
| 学号 | 正整数，且不能与已有学号重复 | `IllegalArgumentException` / `IllegalStateException` |
| 姓名 | 非空、≤20 字、不含逗号 | `IllegalArgumentException` |
| 年龄 | 1~150 | `InvalidFieldException(field="age")` |
| 成绩 | 有限数字，0~100 | `InvalidFieldException(field="grade")` |
| 层次 | 只能是 1 或 2 | `IllegalArgumentException` |

> 注意 `Double.NaN` 与 `Infinity`：`NaN < 0` 和 `NaN > 100` 都是 `false`，光写范围比较拦不住，必须显式判断（`setGrade` 里已经处理）。

## 已知限制

1. `students.csv` 用整表重写，10 万条以上会变慢——真要做大，换 SQLite/JDBC。
2. 没有并发控制，两个人同时改同一文件会互相覆盖。
3. 姓名不允许逗号是有意为之的简化；要支持任意字符得用 CSV 转义（`"张,三"`）。
4. 菜单是 if/switch 的线性结构，功能再多应该拆成命令对象。
