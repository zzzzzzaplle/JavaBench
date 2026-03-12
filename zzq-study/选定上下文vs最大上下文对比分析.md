# 选定上下文 vs 最大上下文 对比分析

## 一、核心区别

| 维度 | 选定上下文 (Selective) | 最大上下文 (Maximum) |
|------|------------------------|---------------------|
| **方法体** | 仅方法签名，无实现 | 完整实现代码 |
| **Token消耗** | ~2000 tokens | ~8000 tokens |
| **信息噪音** | 少 | 多 |
| **推荐程度** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |

## 二、具体示例对比

以 `FillableCell` 类为例：

### 2.1 选定上下文版本

```java
public class FillableCell extends Cell implements MapElement {

    private Pipe pipe;

    public FillableCell(Coordinate coord) {
    }

    public FillableCell(Coordinate coord, Pipe pipe) {
    }

    public Optional<Pipe> getPipe() {
    }

    @Override
    public char toSingleChar() {
    }

    public void setPipe(Pipe pipe) {
    }
}
```

**特点**：
- 只有方法签名，方法体为空
- 保留了字段声明
- 信息精简，无冗余

### 2.2 最大上下文版本

```java
public class FillableCell extends Cell implements MapElement {

    private Pipe pipe;

    public FillableCell(Coordinate coord) {
        super(coord);
        pipe = null;
    }

    public FillableCell(Coordinate coord, Pipe pipe) {
        super(coord);
        pipe = null;
    }

    public Optional<Pipe> getPipe() {
        // TODO
        return null;
    }

    @Override
    public char toSingleChar() {
        // TODO
        return '\0';
    }

    public void setPipe(Pipe pipe) {
        this.pipe = pipe;
    }
}
```

**特点**：
- 包含完整的方法实现
- 包含 TODO 标记
- 包含具体代码逻辑

## 三、对比分析

### 3.1 选定上下文优势

1. **Token效率高**
   - 平均输入 ~2000 tokens
   - 适合大规模测试
   - 降低API成本

2. **信息噪音少**
   - 只保留签名，不暴露实现细节
   - 避免模型"抄袭"已有实现
   - 更真实地测试代码生成能力

3. **聚焦核心任务**
   - 模型需要自己实现方法体
   - 不会受到其他类实现的干扰

### 3.2 最大上下文优势

1. **信息最全面**
   - 可以看到整个项目的完整代码
   - 有助于理解项目架构

2. **适合复杂依赖**
   - 当需要深入理解其他类的实现时有用

### 3.3 最大上下文劣势

1. **Token消耗大** (~8500 tokens)
2. **可能超出上下文窗口限制**
3. **信息噪音多**，可能干扰模型判断

## 四、实际应用建议

### 推荐使用：选定上下文

原因：
1. 更接近真实开发场景（开发者通常只看接口文档）
2. 更好地测试模型的代码生成能力
3. 成本更低，效率更高

### 适用场景对比

| 场景 | 推荐选择 |
|------|----------|
| 日常代码生成测试 | 选定上下文 |
| 大规模模型评估 | 选定上下文 |
| 研究模型对完整项目的理解 | 最大上下文 |
| 快速原型测试 | 最小上下文 |

## 五、数据量对比

以 PA19 的 Cell.java 任务为例：

| 指标 | 选定上下文 | 最大上下文 |
|------|-----------|-----------|
| code_context 长度 | ~1500字符 | ~4500字符 |
| 包含的类数量 | 6个 | 6个 |
| 包含的方法实现 | 0个（仅签名） | 部分实现 |

## 六、结论

**选定上下文**是更优的选择，它在信息完整性和效率之间取得了最佳平衡，能够更准确地评估模型的代码生成能力。
