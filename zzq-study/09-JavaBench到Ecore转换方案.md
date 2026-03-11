# JavaBench 到 Ecore 转换方案

## 一、转换思路概览

```
JavaBench                        iecoregen 输入
   ↓                                ↓
Java 代码骨架  ──────→  Ecore 元模型 + 需求文档
   ↓                                ↓
类/方法/字段          EClass/EOperation/EAttribute
继承/接口             ESuperTypes/EInterface
Javadoc/TODO          自然语言需求描述
```

## 二、可行性分析

### 2.1 JavaBench 内容与 Ecore 元素的映射关系

| JavaBench 元素 | Ecore 元素 | 映射难度 | 说明 |
|---------------|-----------|---------|------|
| **Class** | EClass | ⭐ 简单 | 直接映射 |
| **Interface** | EClass (abstract=true) | ⭐ 简单 | Ecore 支持接口概念 |
| **Method** | EOperation | ⭐⭐ 中等 | 需要处理参数和返回类型 |
| **Field** | EAttribute / EReference | ⭐⭐ 中等 | 区分基本类型和对象引用 |
| **Inheritance** | ESuperTypes | ⭐ 简单 | 直接映射 |
| **Implementation** | ESuperTypes (interface) | ⭐⭐ 中等 | 需要标记接口实现 |
| **Enum** | EEnum | ⭐ 简单 | Ecore 原生支持枚举 |
| **Generic** | EGenericType | ⭐⭐⭐ 复杂 | Ecore 泛型支持有限 |
| **Javadoc** | EAnnotation | ⭐⭐ 中等 | 存储为注释/文档 |
| **TODO标记** | 需求文档 | ⭐⭐⭐ 复杂 | 需要提取和结构化 |

### 2.2 转换优势

✅ **结构对齐**：Java 的 OOP 特性与 Ecore 的建模能力高度匹配  
✅ **质量保证**：JavaBench 经过验证，转换后的数据集质量有保证  
✅ **评估标准**：可直接使用 JavaBench 的测试用例评估生成结果  
✅ **无第三方依赖**：符合 iecoregen 的简化场景需求  

### 2.3 转换挑战

⚠️ **泛型处理**：Java 泛型到 Ecore 泛型的转换可能丢失信息  
⚠️ **静态成员**：Ecore 不直接支持静态方法/字段  
⚠️ **方法体实现**：Ecore EOperation 通常不包含实现细节  
⚠️ **需求提取**：从 Javadoc/TODO 提取结构化需求需要 NLP 技术  

## 三、具体转换方案

### 3.1 方案 A：基于 Java 反射的转换（推荐）

**工具链**：Java → EMF Ecore

```java
// 使用 Eclipse EMF 工具
// 1. 从 Java 代码生成 Ecore 模型
// 2. 提取需求文档

// 示例转换流程
public class JavaToEcoreConverter {
    
    public EPackage convertJavaClassToEcore(Class<?> javaClass) {
        // 创建 EPackage
        EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
        ePackage.setName(javaClass.getPackage().getName());
        
        // 创建 EClass
        EClass eClass = EcoreFactory.eINSTANCE.createEClass();
        eClass.setName(javaClass.getSimpleName());
        eClass.setAbstract(Modifier.isAbstract(javaClass.getModifiers()));
        eClass.setInterface(javaClass.isInterface());
        
        // 转换字段
        for (Field field : javaClass.getDeclaredFields()) {
            convertField(eClass, field);
        }
        
        // 转换方法（仅签名）
        for (Method method : javaClass.getDeclaredMethods()) {
            convertMethod(eClass, method);
        }
        
        // 处理继承
        if (javaClass.getSuperclass() != null) {
            eClass.getESuperTypes().add(
                convertJavaClassToEcore(javaClass.getSuperclass())
            );
        }
        
        // 处理接口实现
        for (Class<?> iface : javaClass.getInterfaces()) {
            eClass.getESuperTypes().add(
                convertJavaClassToEcore(iface)
            );
        }
        
        ePackage.getEClassifiers().add(eClass);
        return ePackage;
    }
    
    private void convertField(EClass eClass, Field field) {
        if (isPrimitiveOrString(field.getType())) {
            // EAttribute
            EAttribute attr = EcoreFactory.eINSTANCE.createEAttribute();
            attr.setName(field.getName());
            attr.setEType(getEDataType(field.getType()));
            eClass.getEStructuralFeatures().add(attr);
        } else {
            // EReference
            EReference ref = EcoreFactory.eINSTANCE.createEReference();
            ref.setName(field.getName());
            ref.setEType(convertJavaClassToEcore(field.getType()));
            eClass.getEStructuralFeatures().add(ref);
        }
    }
    
    private void convertMethod(EClass eClass, Method method) {
        EOperation eOp = EcoreFactory.eINSTANCE.createEOperation();
        eOp.setName(method.getName());
        
        // 返回类型
        if (method.getReturnType() != void.class) {
            eOp.setEType(getEDataType(method.getReturnType()));
        }
        
        // 参数
        for (Parameter param : method.getParameters()) {
            EParameter eParam = EcoreFactory.eINSTANCE.createEParameter();
            eParam.setName(param.getName());
            eParam.setEType(getEDataType(param.getType()));
            eOp.getEParameters().add(eParam);
        }
        
        eClass.getEOperations().add(eOp);
    }
}
```

**优点**：
- ✅ 自动化程度高
- ✅ 类型信息准确
- ✅ 可批量处理

**缺点**：
- ❌ 需要 Java 编译环境
- ❌ 需要处理依赖关系

### 3.2 方案 B：基于源码解析的转换

**工具链**：Java Source Code → AST → Ecore

```java
// 使用 JavaParser 解析 Java 源码
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class SourceToEcoreConverter {
    
    public EPackage convertSourceFile(File javaFile) {
        // 解析 Java 源文件
        CompilationUnit cu = JavaParser.parse(javaFile);
        
        // 提取类定义
        cu.getClassByName(className).ifPresent(classDecl -> {
            EClass eClass = convertClassDeclaration(classDecl);
            
            // 提取 Javadoc 作为需求
            classDecl.getJavadoc().ifPresent(javadoc -> {
                String requirement = extractRequirement(javadoc);
                // 存储为 EAnnotation 或单独的需求文档
                storeRequirement(eClass, requirement);
            });
            
            // 提取方法签名和 TODO 标记
            classDecl.getMethods().forEach(method -> {
                EOperation eOp = convertMethodDeclaration(method);
                
                // 检查是否有 TODO 标记
                method.getBody().ifPresent(body -> {
                    if (body.toString().contains("// TODO")) {
                        // 提取需求
                        String methodReq = extractMethodRequirement(method);
                        storeMethodRequirement(eOp, methodReq);
                    }
                });
            });
        });
    }
}
```

**优点**：
- ✅ 可提取 Javadoc 和注释
- ✅ 不需要编译环境
- ✅ 可处理不完整代码

**缺点**：
- ❌ 类型推断可能不准确
- ❌ 需要额外处理依赖

### 3.3 方案 C：混合方案（推荐）

结合方案 A 和 B 的优点：

```
步骤 1：使用 JavaParser 解析源码
       ├── 提取类结构
       ├── 提取 Javadoc
       └── 识别 TODO 标记

步骤 2：使用反射验证类型信息
       ├── 验证类型引用
       └── 填充缺失信息

步骤 3：生成 Ecore 模型
       ├── 创建 EPackage/EClass
       ├── 添加 EOperation/EAttribute
       └── 设置继承关系

步骤 4：生成需求文档
       ├── 提取 Javadoc
       ├── 结构化 TODO 需求
       └── 生成自然语言描述
```

## 四、需求文档生成方案

### 4.1 从 JavaBench 提取需求

**输入**：JavaBench 的 code 字段（包含 TODO 的代码骨架）

```java
// 示例输入（PA19/Cell.java）
public abstract class Cell implements MapElement {
    public final Coordinate coord;
    
    /**
     * Parses a {@link Cell} from a character.
     * @param c Character to parse. For example, 'W' refers to a wall.
     * @param coord Coordinate of the newly created cell.
     * @param terminationType If the character is a termination cell, its type.
     * @return A cell based on the given creation parameters, or null if invalid.
     */
    public static Cell fromChar(char c, Coordinate coord, TerminationCell.Type terminationType) {
        // TODO
        return null;
    }
}
```

**输出**：结构化需求文档

```json
{
  "class_name": "Cell",
  "class_type": "abstract",
  "requirements": {
    "class_level": "Represents a cell in the game map. This is an abstract class that should be extended by specific cell types.",
    "method_level": [
      {
        "method_name": "fromChar",
        "method_type": "static factory",
        "description": "Parses a Cell from a character representation",
        "parameters": [
          {
            "name": "c",
            "type": "char",
            "description": "Character to parse. 'W' = Wall, '.' = Cell, arrows for TerminationCell"
          },
          {
            "name": "coord",
            "type": "Coordinate",
            "description": "Coordinate of the newly created cell"
          },
          {
            "name": "terminationType",
            "type": "TerminationCell.Type",
            "description": "Type of termination cell (SOURCE or SINK), if applicable"
          }
        ],
        "return_value": {
          "type": "Cell",
          "description": "A cell instance based on the parameters, or null if invalid"
        },
        "implementation_hints": [
          "Handle character 'W' to create Wall",
          "Handle character '.' to create FillableCell",
          "Handle arrow characters (^, v, <, >) to create TerminationCell"
        ]
      }
    ]
  }
}
```

### 4.2 需求提取算法

```python
import re
import json
from typing import Dict, List

def extract_requirements(java_code: str) -> Dict:
    """从 Java 代码提取需求"""
    
    requirements = {
        'class_name': '',
        'class_type': '',
        'requirements': {
            'class_level': '',
            'method_level': []
        }
    }
    
    # 1. 提取类名和类型
    class_match = re.search(
        r'(public|private|protected)?\s*(abstract)?\s*(class|interface)\s+(\w+)',
        java_code
    )
    if class_match:
        requirements['class_name'] = class_match.group(4)
        requirements['class_type'] = 'abstract' if class_match.group(2) else 'concrete'
    
    # 2. 提取类级别的 Javadoc
    class_javadoc = re.search(r'/\*\*(.*?)\*/', java_code, re.DOTALL)
    if class_javadoc:
        requirements['requirements']['class_level'] = clean_javadoc(
            class_javadoc.group(1)
        )
    
    # 3. 提取方法级别的需求
    methods = extract_methods_with_todo(java_code)
    for method in methods:
        method_req = {
            'method_name': method['name'],
            'description': method['javadoc'],
            'parameters': method['params'],
            'return_value': method['return'],
            'implementation_hints': extract_hints_from_javadoc(method['javadoc'])
        }
        requirements['requirements']['method_level'].append(method_req)
    
    return requirements

def extract_methods_with_todo(java_code: str) -> List[Dict]:
    """提取包含 TODO 的方法"""
    methods = []
    
    # 匹配方法签名 + Javadoc + 方法体
    pattern = r'''
        (/\*\*.*?\*/)?\s*                      # Javadoc (optional)
        (public|private|protected)?\s*         # Visibility
        (static)?\s*                           # Static
        (\w+(?:<[\w\s,<>]+>)?)\s+              # Return type
        (\w+)\s*                               # Method name
        \((.*?)\)\s*                           # Parameters
        \{([^}]*// TODO[^}]*)\}                # Body with TODO
    '''
    
    matches = re.finditer(pattern, java_code, re.DOTALL | re.VERBOSE)
    for match in matches:
        method = {
            'javadoc': clean_javadoc(match.group(1) or ''),
            'return': match.group(4),
            'name': match.group(5),
            'params': parse_parameters(match.group(6)),
            'body': match.group(7)
        }
        methods.append(method)
    
    return methods

def extract_hints_from_javadoc(javadoc: str) -> List[str]:
    """从 Javadoc 提取实现提示"""
    hints = []
    
    # 提取 @param 说明
    params = re.findall(r'@param\s+(\w+)\s+(.*?)(?=@|$', javadoc, re.DOTALL)
    for param_name, param_desc in params:
        hints.append(f"Parameter '{param_name}': {param_desc.strip()}")
    
    # 提取 @return 说明
    return_match = re.search(r'@return\s+(.*?)(?=@|$)', javadoc, re.DOTALL)
    if return_match:
        hints.append(f"Return: {return_match.group(1).strip()}")
    
    # 提取 @see 和 {@link} 引用
    links = re.findall(r'\{@link\s+(\w+)\}', javadoc)
    if links:
        hints.append(f"Related classes: {', '.join(links)}")
    
    return hints
```

## 五、完整转换流程

### 5.1 自动化转换脚本

```python
#!/usr/bin/env python3
"""
JavaBench to Ecore + Requirements Converter
"""

import json
import subprocess
from pathlib import Path
from typing import Dict, List

class JavaBenchToEcoreConverter:
    
    def __init__(self, javabench_root: str, output_dir: str):
        self.javabench_root = Path(javabench_root)
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(exist_ok=True)
        
    def convert_all_projects(self):
        """转换所有 JavaBench 项目"""
        for project_id in ['PA19', 'PA20', 'PA21', 'PA22']:
            print(f"Converting {project_id}...")
            self.convert_project(project_id)
    
    def convert_project(self, project_id: str):
        """转换单个项目"""
        # 1. 加载 JavaBench 数据集
        dataset = self.load_dataset(project_id)
        
        # 2. 转换为 Ecore 模型
        ecore_model = self.convert_to_ecore(dataset)
        
        # 3. 提取需求文档
        requirements = self.extract_requirements(dataset)
        
        # 4. 保存结果
        self.save_ecore(ecore_model, project_id)
        self.save_requirements(requirements, project_id)
    
    def load_dataset(self, project_id: str) -> List[Dict]:
        """加载 JavaBench 数据集"""
        dataset_path = self.javabench_root / f'datasets/selective-context/data-{project_id}.jsonl'
        
        data = []
        with open(dataset_path, 'r', encoding='utf-8') as f:
            for line in f:
                data.append(json.loads(line.strip()))
        
        return data
    
    def convert_to_ecore(self, dataset: List[Dict]) -> Dict:
        """转换数据集为 Ecore 模型"""
        ecore = {
            'nsURI': 'http://javabench.example.com',
            'nsPrefix': 'javabench',
            'eClassifiers': []
        }
        
        for task in dataset:
            eclass = self.convert_task_to_eclass(task)
            ecore['eClassifiers'].append(eclass)
        
        return ecore
    
    def convert_task_to_eclass(self, task: Dict) -> Dict:
        """将单个任务转换为 EClass"""
        java_code = self.extract_java_code(task['code'])
        
        eclass = {
            'name': task['task_id'].split('/')[1].replace('.java', ''),
            'eSuperTypes': [],
            'eAttributes': [],
            'eReferences': [],
            'eOperations': []
        }
        
        # 解析 Java 代码结构
        # ... (使用前面定义的解析方法)
        
        return eclass
    
    def extract_requirements(self, dataset: List[Dict]) -> List[Dict]:
        """提取需求文档"""
        requirements = []
        
        for task in dataset:
            req = self.extract_task_requirements(task)
            requirements.append(req)
        
        return requirements
    
    def save_ecore(self, ecore: Dict, project_id: str):
        """保存 Ecore 模型"""
        output_path = self.output_dir / f'{project_id}_model.ecore'
        
        # 转换为 Ecore XMI 格式
        ecore_xmi = self.dict_to_ecore_xmi(ecore)
        
        with open(output_path, 'w', encoding='utf-8') as f:
            f.write(ecore_xmi)
    
    def save_requirements(self, requirements: List[Dict], project_id: str):
        """保存需求文档"""
        output_path = self.output_dir / f'{project_id}_requirements.json'
        
        with open(output_path, 'w', encoding='utf-8') as f:
            json.dump(requirements, f, indent=2, ensure_ascii=False)

# 使用示例
if __name__ == '__main__':
    converter = JavaBenchToEcoreConverter(
        javabench_root='E:/CodeBase/JavaBench',
        output_dir='E:/CodeBase/JavaBench/zzq-study/iecoregen_input'
    )
    converter.convert_all_projects()
```

### 5.2 生成的输出结构

```
zzq-study/iecoregen_input/
├── PA19_model.ecore              # Ecore 元模型
├── PA19_requirements.json        # 需求文档
├── PA20_model.ecore
├── PA20_requirements.json
├── PA21_model.ecore
├── PA21_requirements.json
├── PA22_model.ecore
└── PA22_requirements.json
```

## 六、使用建议

### 6.1 转换策略建议

**推荐策略**：分层转换

```
第一层：类结构
├── 包名、类名、类型（abstract/interface）
├── 继承关系、接口实现
└── 字段定义（EAttribute/EReference）

第二层：方法签名
├── 方法名、参数、返回类型
├── 访问修饰符
└── 异常声明

第三层：语义信息
├── Javadoc 注释
├── TODO 标记提取
└── 约束条件（@NotNull/@Nullable）

第四层：需求文档
├── 类级别需求
├── 方法级别需求
└── 实现提示
```

### 6.2 质量保证

**验证检查清单**：
- ✅ Ecore 模型完整性（所有类都已转换）
- ✅ 类型引用正确性（所有类型都能解析）
- ✅ 继承关系正确性（无循环依赖）
- ✅ 需求完整性（所有 TODO 都有对应需求）
- ✅ 语义保留（Javadoc 信息不丢失）

### 6.3 评估方法

**如何评估转换效果**：

1. **逆向验证**：从生成的 Ecore 模型再生成 Java 代码，与原 JavaBench 代码对比
2. **需求覆盖**：检查提取的需求是否覆盖所有 TODO 标记
3. **语义一致性**：人工审查需求文档是否准确描述了代码意图
4. **端到端测试**：使用 iecoregen 生成代码，用 JavaBench 的测试用例评估

## 七、潜在问题与解决方案

### 7.1 泛型处理

**问题**：Java 泛型在 Ecore 中支持有限

**解决方案**：
```xml
<!-- Ecore 中使用 EGenericType -->
<eClassifiers xsi:type="ecore:EClass" name="Game">
  <eStructuralFeatures xsi:type="ecore:EReference" name="players">
    <eGenericType eClassifier="ecore:EClass platform:/plugin/.../model.ecore#//Player">
      <eTypeArguments/>
    </eGenericType>
  </eStructuralFeatures>
</eClassifiers>
```

或简化为：
```xml
<!-- 忽略泛型，使用基本类型 -->
<eStructuralFeatures xsi:type="ecore:EReference" name="players" 
    eType="#//Player" upperBound="-1"/>
```

### 7.2 静态方法处理

**问题**：Ecore 不直接支持静态方法

**解决方案**：
```java
// 方案 1：转换为辅助类操作
// 创建一个 Helper 类，将静态方法作为实例方法

// 方案 2：在需求文档中标注
{
  "method_name": "fromChar",
  "modifiers": ["static", "public"],
  "note": "This is a static factory method, should be generated as static method in Java"
}
```

### 7.3 方法体丢失

**问题**：Ecore EOperation 不包含方法实现

**解决方案**：
```
将方法实现信息存储在：
1. EAnnotation 中（推荐）
2. 独立的需求文档中
3. 代码模板中
```

## 八、总结

### 8.1 转换方案总结

| 方案 | 适用场景 | 推荐度 |
|------|----------|--------|
| 方案 A：反射转换 | 有完整编译环境 | ⭐⭐⭐⭐ |
| 方案 B：源码解析 | 只有源代码 | ⭐⭐⭐ |
| **方案 C：混合方案** | **最佳实践** | ⭐⭐⭐⭐⭐ |

### 8.2 关键要点

✅ **可行**：JavaBench 可以转换为 Ecore + 需求格式  
✅ **质量**：JavaBench 的质量保证可以传递给转换后的数据  
✅ **评估**：可以直接使用 JavaBench 的测试用例评估生成结果  
⚠️ **注意**：需要处理泛型、静态方法等特殊情况  

### 8.3 下一步行动

**立即可以做的**：
1. 实现 JavaBench 数据集解析器
2. 提取类结构和方法签名
3. 生成 Ecore 模型文件
4. 提取和结构化需求文档

**后续研究**：
1. 评估转换质量
2. 优化需求提取算法
3. 建立端到端评估流程
