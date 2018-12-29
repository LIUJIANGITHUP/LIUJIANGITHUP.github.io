---
layout: post
title:  springboot热部署
date:   2018-11-17 13:31:01 +0800
categories: springboot
tag: 部署
---

* content
{:toc}


# 摘要：
  开发中反复修改类、页面等资源，每次修改后都是需要重新启动才生效，这样每次启动都很麻烦，浪费了大量的时间，如果在我修改代码后不重启就能生效，那么必须在     pom.xml中添加如下配置就可以实现这样的功能，我们称之为**热部署**。
# 步骤（IDEA编辑器操作）
  1. CTRL + SHIFT + A --> 查找make project automatically --> 选中 
  2. CTRL + SHIFT + A --> 查找Registry --> 找到并勾选compiler.automake.allow.when.app.running 
  3. 在pom.xml中添加一下依赖，如下：
    ```
    
    <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-devtools</artifactId>
         <optional>true</optional>
         <scope>runtime</scope>
    </dependency>
    #注意：optional必须设置为true
    
    ```
  4. idea开启自动编译（File-setting-Compiler）
     File-setting-Compiler-Build Project automatically



