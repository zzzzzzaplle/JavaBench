# PA19 PlantUML 类图生成结果

## 生成文件

| 文件名 | 说明 | 内容 |
|--------|------|------|
| PA19-plantuml.txt | 完整版本 | 包含所有依赖（含 java.*） |
| PA19-plantuml-clean.txt | 清理版本 | 仅项目类（推荐用于转 ecore） |

## 类统计

### 按包分布

| 包名 | 类数量 | 包含的类 |
|------|--------|----------|
| (default) | 1 | Main |
| game | 4 | CellStack, DelayBar, Game, PipeQueue |
| game.map | 1 | Map |
| game.map.cells | 3 | FillableCell, TerminationCell, Wall |
| game.pipes | 1 | Pipe |
| io | 1 | Deserializer |
| util | 3 | Coordinate, Direction(枚举), PipePatterns, StringUtils |
| **总计** | **15** | |

### 按类型分布

| 类型 | 数量 | 类名 |
|------|------|------|
| 普通类 | 13 | Main, CellStack, DelayBar, Game, PipeQueue, Map, FillableCell, TerminationCell, Wall, Pipe, Deserializer, Coordinate, PipePatterns, StringUtils |
| 抽象类 | 1 | Cell |
| 枚举 | 1 | Direction |
| 接口 | 1 | MapElement |

## 类关系分析

### 继承关系 (--|>)

```
game.map.cells.Cell (abstract)
├── game.map.cells.FillableCell
├── game.map.cells.TerminationCell
└── game.map.cells.Wall

game.MapElement (interface)
├── game.map.cells.Cell
├── game.map.cells.FillableCell
└── game.pipes.Pipe
```

### 依赖关系 (..>)

主要依赖流向：
- `game.Game` → 依赖多个模块（Map, Pipe, Cell, Deserializer）
- `game.map.Map` → 依赖 Cell 系列、Pipe、Deserializer
- `io.Deserializer` → 负责反序列化，依赖多个游戏元素类
- `game.map.cells.*` → 依赖 util 包的工具类

## 使用 plantuml-dependency 工具的命令

### 完整版本（包含 java.* 依赖）
```bash
java -jar plantuml-dependency-cli-1.4.0-jar-with-dependencies.jar \
  -o zzq-study/PA19-plantuml.txt \
  -b projects/PA19-Solution/src/main/java \
  -i "**/*.java" \
  -dt classes,interfaces,abstract_classes,enums,extensions,implementations,imports
```

### 清理版本（仅项目类）
```bash
java -jar plantuml-dependency-cli-1.4.0-jar-with-dependencies.jar \
  -o zzq-study/PA19-plantuml-clean.txt \
  -b projects/PA19-Solution/src/main/java \
  -i "**/*.java" \
  -dp "^(?!java\.)(.+)$" \
  -dt classes,interfaces,abstract_classes,enums,extensions,implementations,imports
```

## 参数说明

| 参数 | 说明 |
|------|------|
| `-o` | 输出文件路径 |
| `-b` | 源码根目录 |
| `-i` | 包含的文件模式 |
| `-dp` | 包名过滤（正则表达式） |
| `-dt` | 显示类型（类、接口、抽象类、枚举、继承、实现、导入） |

## 下一步：转换为 Ecore

推荐使用 `PA19-plantuml-clean.txt` 进行转换：

1. **包映射关系**：
   - PlantUML `package` → Ecore `EPackage`
   - 支持 7 个 EPackage（包括默认包）

2. **类映射关系**：
   - PlantUML `class` → Ecore `EClass`
   - PlantUML `enum` → Ecore `EEnum`
   - PlantUML `interface` → Ecore `EClass` (interface=true)

3. **关系映射关系**：
   - PlantUML `--|>` → Ecore `ESuperTypes`
   - PlantUML `..>` → Ecore `EReference` 或 `EAttribute`

## 可视化预览

可以使用 PlantUML 在线工具预览：
- https://plantuml.com/zh/online
- 粘贴 `PA19-plantuml-clean.txt` 内容即可生成类图
