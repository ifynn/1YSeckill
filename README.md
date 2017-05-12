### LICENSE

Copyright 2015 - 2017 Fynn

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     ![Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

## 简介
终于开源了！

该项目是一款类似目前火爆的一元夺宝类项目，现在把它拿出来与大家分享。在零零散散的时间里将其开发完成，其中不免有些仓促，也会因为有些后端云的功能限制，一些功能可能实现地不那么尽善尽美，望大家体谅，有什么不足还望积极指正。

整个项目的需求、视觉设计、图标设计、编码、发布均由本人独立完成，未经许可不得用于商业用途，违者必究！

## 项目结构
> * AppU  - 包含一系列项目中用到的工具类、框架类；
> * OYSeckill - 项目主模块；
> * Raw - 包含一些资源文件；
> * Snapshot - App 快照，包含一些 pp 截图。

#### OYSeckill 结构

```
app -- UI相关
    core -- 核心框架
    module -- 模块
        home -- 首页
        main -- 主 Activity
        find -- "发现"页
        account -- 账户
db -- 数据存储
model -- 数据模型
util -- 工具类
web -- 网络
widget -- 小组件
```

#### Raw 描述
`1yseckill_v2.0_release.apk` 文件使用的签名为本人的正式签名，包含所有功能，供大家演示使用；项目中的相关第三方开发者账户，需要自己注册后才能使用完整功能。

#### 截图
* 首页

    ![首页](https://github.com/ifynn/1YSeckill/blob/master/Snapshot/snapshot_home.png)

* 最近揭晓

    ![最近揭晓](https://github.com/ifynn/1YSeckill/blob/master/Snapshot/snapshot_recent.png)

* 商品详情

    ![商品详情](https://github.com/ifynn/1YSeckill/blob/master/Snapshot/snapshot_detail.png)

* 用户晒单

    ![用户晒单](https://github.com/ifynn/1YSeckill/blob/master/Snapshot/snapshot_share.png)

## 开源框架
项目中使用到了短信验证码、后端数据服务、集成分享、移动统计、移动支付、图片加载等功能，设计到的第三方开源服务有：

`Bmob移动后端云`  `Bmob消息推送`  `Bmob支付`  `Bmob短信验证码`  `Mob短信验证码` `Mob分享` `腾讯移动统计` `UIL`

#### Bmob.cn
* Bmob SDK - 提供了一套后台数据解决方案，包含从后端数据库增、删、改、查，以及文件的上传下载，集成了短信验证码功能；
* Bmob Pay - 提供了移动支付支持；
* Bmob Push - 用于推送消息给用户。

#### Mob.com
* SMS SDK - 短信验证码SDK，用于普通短信验证码的下发，比如用户注册；
* Share SDK - 集成分享SDK，支持QQ、微信、微博等大部分分享平台。

#### 腾讯移动统计
* MTA SDK - 腾讯移动应用统计工具。

#### UIL
* universal-image-loader - 一个图片缓存与加载的第三方开源框架。

## 注意事项

#### 移动后端云
某些移动云服务平台需要开发者注册并得到 App ID 才能正常使用 App 的所有功能，其中需要注册的平台有：
`Bmob`  `Mob`  `腾讯移动统计`

> `Bmob`  已经在项目中提供的本人的 App ID，开发者可以正常使用其中的功能，包含后端数据获取与存储、短信验证码、移动支付、消息推送等。

> `Mob`  需要自己注册并在项目中配置分享、短信验证码的 App Key，由于集成了微信、QQ、微博等平台的分享，所以开发者还需要注册所需要分享平台的开发者账号，并将得到的 App Key 配置在项目中，具体可以查看 Mob 官网开发指南。

> `腾讯移动统计`  这个就不多说了，也需要自己注册，并在项目中配置。

#### 其他
Raw 文件夹中提供的 apk 文件与项目属于不同的后端数据，所以二者数据不共享，在其中一端注册的新用户不与另一端共享数据。

## 结语
如果有什么问题，欢迎在 Issues 中提出，我会尽量回答。

