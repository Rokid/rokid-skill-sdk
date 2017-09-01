# rokid-dispatcher 工具使用说明

* 实例化`com.rokid.skill.dispatcher.RokidBus`对象

```markdown
RokidRequestBus bus = new RokidRequestBus();
```

* 将从若琪开放平台接收的json参数序列化为`com.rokid.skill.protocol.RokidRequest`对象
* 