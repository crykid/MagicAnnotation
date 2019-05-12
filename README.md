# **Magic--魔法注解**

## 一、背景及功能介绍

```我们项目中可能会碰到这样的情况：
我们可能为了开发方便，在页面的某个地方放了一个特殊的控件，供我们直接打开某个页面以
方便我们调试。但是方便的同时可能会出现问题，它们只能在特定的环境中出现，例如一个测
试的入口只能在开发环境显示而不能给我们的测试同学使用，或者生产环境绝对不能暴露给用户。
这时候我们这个库就营运而生。
我们可以灵活的通过注解的方式决定我们的特定ui在某个特定的环境出现。
```

## 二、使用方法
```
使用方式及其方便，仅仅需要以下三个步骤：
```

#### 1.在project的build.gradle中添加以下代码：
```
allprojects {
    repositories {
        ...
        //这一行
        maven { url 'https://jitpack.io' }
        }
    }
```

#### 2.在项目的module的build.gradle中添加以下代码：
```
dependencies {
    //这一行	
    implementation 'com.github.crykid:MagicDialog:v1.0.1'
    annotationProcessor "aaa.bbb.ccc"
} 
```
#### 3.在需要施法的View声明处施法
```
    //View声明处施法
    @BindView(R.id.tv_main_debug)
    @OnlyDebug()
    TextView tvDebug;

    @BindView(R.id.tv_main_release)
    @OnlyAvailable("release")
    TextView tvRelease;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        
        //在目标需要使用的Activity或fragment中，在view初始化之后，例如在Butterknife.bind之后生效魔法
        Magic.conjure(this, BuildConfig.BUILD_TYPE);

    }
    
```

## 三、版本日志
- v1.0.0:
    1.仅仅控制View在某个环境显示或不在Release显示；
